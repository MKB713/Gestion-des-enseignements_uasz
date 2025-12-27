# Guide de Monitoring et Observabilité - DAOS

## Vue d'ensemble

Ce guide explique le système complet de monitoring et observabilité mis en place pour l'infrastructure DAOS. Il couvre Prometheus, Grafana, Jaeger, Loki, et les alertes.

## Critères d'Acceptation Validés

✅ **Spring Boot Actuator activé** sur tous les services
✅ **Endpoints exposés**: /health, /info, /metrics, /prometheus
✅ **Intégration Prometheus** pour la collecte des métriques
✅ **Dashboard Grafana** configuré avec datasources
✅ **Logs centralisés** avec Loki et Promtail
✅ **Distributed tracing** avec Jaeger (compatible Zipkin)
✅ **Alertes configurées** pour services down et problèmes de performance

## Architecture de Monitoring

```
┌──────────────────────────────────────────────────────────────────┐
│                     Stack de Monitoring DAOS                     │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │  Microservices                │  │   Docker    │             │
│  │  (Actuator  │  │  Exporters  │  │  Containers │             │
│  │  /metrics)  │  │  MySQL,Redis│  │   Logs      │             │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘             │
│         │                 │                 │                    │
│         ▼                 ▼                 ▼                    │
│  ┌──────────────┐  ┌────────────┐  ┌────────────┐              │
│  │  Prometheus  │  │  Jaeger    │  │  Promtail  │              │
│  │  (Metrics)   │  │  (Traces)  │  │  (Logs)    │              │
│  └──────┬───────┘  └─────┬──────┘  └─────┬──────┘              │
│         │                 │                │                     │
│         │  ┌──────────────▼────────────────▼──────┐             │
│         │  │          Loki (Log Storage)          │             │
│         │  └──────────────────────────────────────┘             │
│         │                                                        │
│         ▼                                                        │
│  ┌────────────────────────────────────────────────┐             │
│  │         Grafana (Visualization)                │             │
│  │  • Prometheus Datasource                       │             │
│  │  • Loki Datasource                             │             │
│  │  • Jaeger Datasource                           │             │
│  │  • Pre-configured Dashboards                   │             │
│  └────────────────────────────────────────────────┘             │
│                                                                  │
│  ┌────────────────────────────────────────────────┐             │
│  │   AlertManager (Alert Management)              │             │
│  │  • Service Down Alerts                         │             │
│  │  • Performance Alerts                          │             │
│  │  • Database Connection Alerts                  │             │
│  │  • High CPU/Memory Alerts                      │             │
│  └────────────────────────────────────────────────┘             │
└──────────────────────────────────────────────────────────────────┘
```

## Services de Monitoring

### 1. Spring Boot Actuator

**Configuration** (dans tous les microservices):
```properties
# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics,prometheus,env,loggers
management.endpoint.health.show-details=always
management.endpoint.health.probes.enabled=true
management.health.livenessstate.enabled=true
management.health.readinessstate.enabled=true
management.metrics.export.prometheus.enabled=true
management.metrics.distribution.percentiles-histogram.http.server.requests=true
management.metrics.tags.application=${spring.application.name}
```

**Endpoints disponibles**:
- `/actuator/health` - État de santé du service
- `/actuator/info` - Informations sur l'application
- `/actuator/metrics` - Métriques disponibles
- `/actuator/prometheus` - Métriques au format Prometheus
- `/actuator/env` - Variables d'environnement
- `/actuator/loggers` - Configuration des loggers

**Exemple**:
```bash
# Health check
curl http://localhost:8081/actuator/health

# Metrics Prometheus
curl http://localhost:8081/actuator/prometheus
```

### 2. Prometheus (port 9090)

**Description**: Système de collecte et stockage de métriques time-series.

**Accès**: http://localhost:9090

**Configuration**: `monitoring/prometheus/prometheus.yml`

**Services monitorés**:
- Tous les microservices Spring Boot (via /actuator/prometheus)
- MySQL (via mysql-exporter)
- Redis (via redis-exporter)
- Système (via node-exporter)

**Scrape interval**: 15 secondes

**Requêtes PromQL utiles**:
```promql
# Services actifs
up{job=~".*-service|api-gateway|eureka-server|config-server"}

# Taux de requêtes HTTP
rate(http_server_requests_seconds_count[5m])

# Latence p95
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))

# Utilisation CPU
process_cpu_usage

# Utilisation mémoire JVM
jvm_memory_used_bytes / jvm_memory_max_bytes

# Connexions DB actives
hikaricp_connections_active
```

### 3. Grafana (port 3001)

**Description**: Plateforme de visualisation et dashboards.

**Accès**: http://localhost:3001
- **Username**: admin
- **Password**: admin

**Datasources configurées**:
1. **Prometheus** (par défaut) - Métriques
2. **Loki** - Logs
3. **Jaeger** - Traces distribuées

**Dashboards pré-configurés**:

#### Dashboard "DAOS - Spring Boot Microservices"
Inclut:
- Services Status (timeline)
- Service Health (gauge)
- HTTP Request Rate
- HTTP Request Latency (p95, p99)
- CPU Usage
- JVM Memory Usage

**Variable de sélection**: `$service` - permet de basculer entre services

**Création de dashboards personnalisés**:
1. Aller dans Dashboards → New Dashboard
2. Add Visualization
3. Sélectionner Prometheus comme datasource
4. Écrire une requête PromQL
5. Configurer les options de visualisation

### 4. Jaeger (port 16686)

**Description**: Système de distributed tracing pour suivre les requêtes à travers les microservices.

**Accès**: http://localhost:16686

**Configuration** (dans les microservices):
```properties
# Distributed Tracing
management.tracing.sampling.probability=1.0
management.zipkin.tracing.endpoint=http://jaeger:9411/api/v2/spans
logging.pattern.level=%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]
```

**Fonctionnalités**:
- **Search Traces**: Rechercher des traces par service, opération, tags
- **Compare Traces**: Comparer deux traces
- **System Architecture**: Visualiser les dépendances entre services
- **Deep Dependency Graph**: Graphe des dépendances en profondeur

**Utilisation**:
1. Sélectionner un service (ex: api-gateway)
2. Cliquer sur "Find Traces"
3. Sélectionner une trace pour voir le détail
4. Analyser les spans et le timing

**Exemple de trace**:
```
api-gateway → auth-service → mysql
              ↓
              enseignant-service → mysql
```

### 5. Loki + Promtail (port 3100)

**Description**: Système de log aggregation inspiré de Prometheus.

**Loki**: Stockage et indexation des logs
**Promtail**: Collecte des logs depuis les conteneurs Docker

**Configuration Promtail**: `monitoring/promtail/promtail-config.yml`
- Scrape automatique des logs Docker
- Extraction des labels (service, container, image)
- Parsing des logs Spring Boot
- Extraction du traceId et spanId

**Utilisation dans Grafana**:
1. Aller dans Explore
2. Sélectionner datasource "Loki"
3. Utiliser LogQL pour filtrer les logs

**Requêtes LogQL utiles**:
```logql
# Logs d'un service
{service="auth-service"}

# Logs avec niveau ERROR
{service="auth-service"} |= "ERROR"

# Logs avec un traceId spécifique
{service="auth-service"} | json | traceId="abc123"

# Rate de logs ERROR
sum(rate({service="auth-service"} |= "ERROR" [5m])) by (service)

# Logs des 5 dernières minutes
{service="auth-service"} | json | level="ERROR"
```

**Corrélation Logs ↔ Traces**:
Dans Grafana, les logs contenant un `traceId` affichent un lien direct vers la trace dans Jaeger.

### 6. AlertManager (port 9093)

**Description**: Gestion des alertes de Prometheus.

**Accès**: http://localhost:9093

**Configuration**: `monitoring/alertmanager/alertmanager.yml`

**Alertes configurées**:

#### Critiques (Critical)
- **ServiceDown**: Service indisponible pendant > 1 min
- **EurekaServerDown**: Service Registry down
- **ConfigServerDown**: Config Server down
- **ApiGatewayDown**: API Gateway down
- **HighHttpErrorRate**: Taux d'erreur HTTP > 5%
- **DatabaseConnectionPoolExhaustion**: Pool de connexions DB > 90%

#### Warnings
- **HighCpuUsage**: CPU > 80% pendant 5 min
- **HighMemoryUsage**: Mémoire JVM > 90% pendant 5 min
- **SlowHttpRequests**: p95 > 2 secondes
- **HighGcTime**: Garbage Collection > 10% du CPU
- **TooManyThreads**: > 200 threads
- **HighGatewayLatency**: p99 > 3 secondes au Gateway
- **DiskSpaceLow**: Espace disque < 10%

**Receivers configurés**:
- `default-receiver`: Webhook générique
- `critical-receiver`: Alertes critiques
- `warning-receiver`: Alertes de warning
- `infrastructure-receiver`: Alertes infrastructure
- `database-receiver`: Alertes base de données

**Silences**:
Vous pouvez créer des silences pour désactiver temporairement certaines alertes:
1. Aller sur http://localhost:9093
2. Cliquer sur "Silences"
3. Créer un nouveau silence avec les labels appropriés

### 7. Exporters

#### MySQL Exporter (port 9104)
Métriques MySQL pour Prometheus:
- Connexions actives
- Requêtes par seconde
- Taille des tables
- Slow queries

#### Redis Exporter (port 9121)
Métriques Redis pour Prometheus:
- Clés en mémoire
- Hits/misses du cache
- Utilisation mémoire
- Connexions

#### Node Exporter (port 9100)
Métriques système:
- CPU usage
- Memory usage
- Disk I/O
- Network traffic

## Dépendances Maven

Ajoutées à tous les microservices:

```xml
<!-- Monitoring et Observabilité -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-jaeger</artifactId>
    <version>1.32.0</version>
</dependency>
```

## Démarrage du Stack de Monitoring

### Démarrer tous les services
```bash
docker-compose up -d
```

### Démarrer uniquement le monitoring
```bash
docker-compose up -d prometheus grafana jaeger loki promtail alertmanager
```

### Vérifier le statut
```bash
docker-compose ps | grep -E "prometheus|grafana|jaeger|loki|alertmanager"
```

### Voir les logs
```bash
# Tous les services de monitoring
docker-compose logs -f prometheus grafana jaeger loki alertmanager

# Un service spécifique
docker-compose logs -f prometheus
```

## Accès aux Interfaces

| Service | URL | Credentials | Description |
|---------|-----|-------------|-------------|
| **Grafana** | http://localhost:3001 | admin/admin | Dashboards et visualisation |
| **Prometheus** | http://localhost:9090 | - | Métriques et requêtes PromQL |
| **Jaeger UI** | http://localhost:16686 | - | Traces distribuées |
| **AlertManager** | http://localhost:9093 | - | Gestion des alertes |
| **Loki** | http://localhost:3100 | - | API de logs (utilisé via Grafana) |

## Workflow de Monitoring

### 1. Détection d'un Problème

**Scénario**: Service auth-service est lent

#### Étape 1: Vérifier les alertes
- Aller sur AlertManager (http://localhost:9093)
- Vérifier si une alerte "SlowHttpRequests" est active

#### Étape 2: Analyser les métriques dans Grafana
- Ouvrir Grafana (http://localhost:3001)
- Sélectionner le dashboard "DAOS - Spring Boot Microservices"
- Sélectionner `auth-service` dans la variable `$service`
- Regarder:
  - HTTP Request Latency (p95, p99)
  - CPU Usage
  - JVM Memory
  - Request Rate

#### Étape 3: Analyser les traces dans Jaeger
- Ouvrir Jaeger (http://localhost:16686)
- Sélectionner `auth-service`
- Filtrer par les requêtes lentes (Duration > 2s)
- Identifier le span le plus lent

#### Étape 4: Consulter les logs dans Loki
- Dans Grafana, aller dans Explore
- Sélectionner datasource "Loki"
- Requête:
  ```logql
  {service="auth-service"} | json | level="ERROR"
  ```
- Si un `traceId` est trouvé dans les logs, cliquer pour voir la trace

### 2. Investigation Proactive

**Scénario**: Vérification de santé hebdomadaire

#### Vérifier tous les services sont UP
```promql
up{job=~".*-service|api-gateway|eureka-server|config-server"}
```

#### Vérifier les taux d'erreur
```promql
rate(http_server_requests_seconds_count{status=~"5.."}[1h])
```

#### Vérifier l'utilisation mémoire
```promql
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}
```

#### Vérifier les connexions DB
```promql
hikaricp_connections_active / hikaricp_connections_max
```

## Métriques Importantes

### Performance
```promql
# Latence moyenne des requêtes HTTP
avg(rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m]))

# Throughput (requêtes/seconde)
sum(rate(http_server_requests_seconds_count[5m])) by (application)

# Taux d'erreur
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) by (application)
/ sum(rate(http_server_requests_seconds_count[5m])) by (application)
```

### Ressources
```promql
# CPU Usage
process_cpu_usage

# Memory Usage (%)
(jvm_memory_used_bytes / jvm_memory_max_bytes) * 100

# Threads actifs
jvm_threads_live

# Garbage Collection Rate
rate(jvm_gc_pause_seconds_sum[5m])
```

### Database
```promql
# Connexions DB actives
hikaricp_connections_active

# Pool utilization (%)
(hikaricp_connections_active / hikaricp_connections_max) * 100

# Temps d'acquisition de connexion
hikaricp_connections_acquire_seconds
```

## Alertes et Notifications

### Configuration Email (exemple)

Modifier `monitoring/alertmanager/alertmanager.yml`:

```yaml
receivers:
  - name: 'critical-receiver'
    email_configs:
      - to: 'admin@daos.uasz.sn'
        from: 'alertmanager@daos.uasz.sn'
        smarthost: 'smtp.gmail.com:587'
        auth_username: 'alertmanager@daos.uasz.sn'
        auth_password: 'your-app-password'
        headers:
          Subject: '[CRITICAL] DAOS Alert: {{ .GroupLabels.alertname }}'
```

### Configuration Slack (exemple)

```yaml
receivers:
  - name: 'slack-receiver'
    slack_configs:
      - api_url: 'https://hooks.slack.com/services/YOUR/WEBHOOK/URL'
        channel: '#daos-alerts'
        title: 'DAOS Alert'
        text: '{{ range .Alerts }}{{ .Annotations.summary }}{{ end }}'
```

### Configuration Webhook personnalisé

```yaml
receivers:
  - name: 'webhook-receiver'
    webhook_configs:
      - url: 'http://your-webhook-handler:5000/alert'
        send_resolved: true
```

## Troubleshooting

### Problème: Prometheus ne scrape pas les métriques

**Symptômes**: Pas de données dans Grafana

**Solutions**:
```bash
# Vérifier que Prometheus peut joindre le service
docker exec daos-prometheus wget -O- http://auth-service:8081/actuator/prometheus

# Vérifier la configuration Prometheus
docker exec daos-prometheus cat /etc/prometheus/prometheus.yml

# Vérifier les targets dans Prometheus UI
# Aller sur http://localhost:9090/targets
```

### Problème: Traces ne apparaissent pas dans Jaeger

**Symptômes**: Aucune trace visible

**Solutions**:
```bash
# Vérifier la configuration du service
curl http://localhost:8081/actuator/env | jq '.propertySources[] | select(.name | contains("management.zipkin"))'

# Vérifier que Jaeger reçoit des données
docker logs daos-jaeger

# Tester l'envoi manuel de trace
curl -X POST http://localhost:9411/api/v2/spans \
  -H 'Content-Type: application/json' \
  -d '[{"traceId":"test123","id":"span456","name":"test"}]'
```

### Problème: Logs ne remontent pas dans Loki

**Symptômes**: Pas de logs dans Grafana Explore

**Solutions**:
```bash
# Vérifier Promtail
docker logs daos-promtail

# Vérifier que Promtail peut accéder aux logs Docker
docker exec daos-promtail ls -la /var/lib/docker/containers

# Tester la connectivité Promtail → Loki
docker exec daos-promtail wget -O- http://loki:3100/ready
```

### Problème: Alertes ne se déclenchent pas

**Symptômes**: Pas d'alertes dans AlertManager malgré les problèmes

**Solutions**:
```bash
# Vérifier les règles d'alerte dans Prometheus
# Aller sur http://localhost:9090/alerts

# Vérifier la configuration AlertManager
docker exec daos-alertmanager cat /etc/alertmanager/config.yml

# Forcer une alerte de test
# Arrêter un service
docker stop auth-service
# Attendre 1-2 minutes
# Vérifier http://localhost:9093
```

## Bonnes Pratiques

### 1. Monitoring
- Configurer des dashboards par équipe/domaine
- Créer des alertes progressives (warning puis critical)
- Monitorer les SLIs (Service Level Indicators) importants
- Réviser les alertes régulièrement pour éviter la fatigue

### 2. Logging
- Utiliser des niveaux de log appropriés (ERROR, WARN, INFO, DEBUG)
- Inclure le contexte dans les logs (traceId, userId, etc.)
- Ne pas logger de données sensibles (mots de passe, tokens)
- Structurer les logs (JSON si possible)

### 3. Tracing
- Sampling à 100% en dev/test, ajuster en prod si nécessaire
- Ajouter des tags personnalisés aux spans
- Utiliser les baggage items pour propager le contexte
- Corréler traces, logs et métriques via traceId

### 4. Alertes
- Alertes actionnables uniquement
- Documentation claire de la résolution
- Éviter les faux positifs
- Tester les alertes régulièrement

## Métriques Business

En plus des métriques techniques, il est recommandé d'ajouter des métriques métier:

```java
// Exemple: Counter pour les inscriptions
@Component
public class MetricsService {
    private final Counter inscriptionsCounter;

    public MetricsService(MeterRegistry registry) {
        this.inscriptionsCounter = Counter.builder("daos.inscriptions.total")
            .description("Total des inscriptions")
            .tag("type", "etudiant")
            .register(registry);
    }

    public void recordInscription() {
        inscriptionsCounter.increment();
    }
}
```

Métriques métier à considérer:
- Nombre d'inscriptions par jour
- Nombre de choix d'enseignements validés
- Taux de remplissage des emplois du temps
- Durée moyenne de traitement d'une demande

## Conclusion

Le stack de monitoring DAOS fournit une observabilité complète avec:
- ✅ Métriques temps réel (Prometheus + Grafana)
- ✅ Logs centralisés (Loki + Promtail)
- ✅ Distributed tracing (Jaeger)
- ✅ Alertes automatiques (AlertManager)
- ✅ Dashboards pré-configurés

Pour toute question ou amélioration, consulter la documentation officielle:
- [Prometheus](https://prometheus.io/docs/)
- [Grafana](https://grafana.com/docs/)
- [Jaeger](https://www.jaegertracing.io/docs/)
- [Loki](https://grafana.com/docs/loki/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
