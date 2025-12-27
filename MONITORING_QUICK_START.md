# Monitoring - Démarrage Rapide

## Installation en 5 Minutes

### 1. Démarrer le stack de monitoring
```bash
docker-compose up -d prometheus grafana jaeger loki promtail alertmanager
```

### 2. Démarrer les microservices
```bash
docker-compose up -d
```

### 3. Accéder aux interfaces

| Interface | URL | Credentials |
|-----------|-----|-------------|
| **Grafana** | http://localhost:3001 | admin / admin |
| **Prometheus** | http://localhost:9090 | - |
| **Jaeger** | http://localhost:16686 | - |
| **AlertManager** | http://localhost:9093 | - |

## Vérifications Rapides

### Test 1: Vérifier Prometheus

```bash
# Vérifier que Prometheus scrape les services
curl http://localhost:9090/api/v1/targets | jq '.data.activeTargets[] | {job: .labels.job, health: .health}'
```

Tous les services doivent être `"health": "up"`.

### Test 2: Vérifier Grafana

1. Ouvrir http://localhost:3001
2. Login: admin / admin
3. Aller dans Dashboards → Browse
4. Ouvrir "DAOS - Spring Boot Microservices"
5. Vérifier que des données s'affichent

### Test 3: Vérifier Jaeger

1. Ouvrir http://localhost:16686
2. Sélectionner "api-gateway" dans Service
3. Cliquer sur "Find Traces"
4. Vérifier que des traces apparaissent

### Test 4: Vérifier Loki

1. Dans Grafana, aller dans Explore
2. Sélectionner datasource "Loki"
3. Query: `{service="auth-service"}`
4. Vérifier que des logs apparaissent

### Test 5: Vérifier les Alertes

```bash
# Arrêter un service pour déclencher une alerte
docker stop auth-service

# Attendre 1-2 minutes

# Vérifier AlertManager
curl http://localhost:9093/api/v2/alerts | jq '.[] | {alertname: .labels.alertname, status: .status.state}'

# Redémarrer le service
docker start auth-service
```

## Endpoints Actuator

Vérifier les endpoints Actuator de chaque service:

```bash
# Health
curl http://localhost:8081/actuator/health | jq

# Info
curl http://localhost:8081/actuator/info | jq

# Metrics Prometheus
curl http://localhost:8081/actuator/prometheus
```

## Requêtes Prometheus Utiles

Dans Prometheus (http://localhost:9090):

### Services Status
```promql
up{job=~".*-service|api-gateway|eureka-server|config-server"}
```

### Request Rate
```promql
rate(http_server_requests_seconds_count[5m])
```

### Latency p95
```promql
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))
```

### CPU Usage
```promql
process_cpu_usage
```

### Memory Usage
```promql
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}
```

## Requêtes Loki Utiles

Dans Grafana Explore avec datasource Loki:

### Tous les logs d'un service
```logql
{service="auth-service"}
```

### Logs ERROR
```logql
{service="auth-service"} |= "ERROR"
```

### Logs avec traceId
```logql
{service="auth-service"} | json | traceId!=""
```

### Rate de logs ERROR
```logql
sum(rate({service="auth-service"} |= "ERROR" [5m])) by (service)
```

## Workflow Typique

### Scénario: Requête lente détectée

1. **Grafana**: Identifier le service avec haute latence
   - Dashboard → Sélectionner service → Voir "HTTP Request Latency"

2. **Jaeger**: Trouver la trace lente
   - Service → auth-service
   - Tags → duration > 2s
   - Analyser les spans

3. **Loki**: Vérifier les logs autour du problème
   - Query: `{service="auth-service"} | json | traceId="<traceId-from-jaeger>"`

4. **Prometheus**: Vérifier les métriques système
   - CPU, Memory, DB connections

## Alertes Configurées

### Critical
- **ServiceDown**: Service indisponible > 1 min
- **HighHttpErrorRate**: Erreurs HTTP > 5%
- **DatabaseConnectionPoolExhaustion**: Pool DB > 90%

### Warning
- **HighCpuUsage**: CPU > 80% pendant 5 min
- **HighMemoryUsage**: Mémoire > 90% pendant 5 min
- **SlowHttpRequests**: p95 > 2 secondes

Voir toutes les alertes: http://localhost:9090/alerts

## Troubleshooting Rapide

### Problème: Pas de données dans Grafana

```bash
# Vérifier Prometheus
docker logs daos-prometheus

# Vérifier les targets
curl http://localhost:9090/api/v1/targets

# Vérifier l'endpoint du service
curl http://localhost:8081/actuator/prometheus
```

### Problème: Pas de traces dans Jaeger

```bash
# Vérifier la configuration
curl http://localhost:8081/actuator/env | grep zipkin

# Vérifier Jaeger
docker logs daos-jaeger

# Générer du trafic
for i in {1..10}; do curl http://localhost:8080/actuator/health; done
```

### Problème: Pas de logs dans Loki

```bash
# Vérifier Promtail
docker logs daos-promtail

# Vérifier Loki
curl http://localhost:3100/ready

# Tester manuellement
docker logs auth-service
```

## Commandes Utiles

### Restart du monitoring
```bash
docker-compose restart prometheus grafana jaeger loki promtail alertmanager
```

### Rebuild après modification des configs
```bash
docker-compose up -d --force-recreate prometheus grafana
```

### Voir les métriques en temps réel
```bash
watch -n 2 'curl -s http://localhost:9090/api/v1/query?query=up | jq ".data.result[] | {job: .metric.job, value: .value[1]}"'
```

### Nettoyer les données
```bash
docker-compose down -v
docker volume rm gestion-des-enseignements_uasz_prometheus-data
docker volume rm gestion-des-enseignements_uasz_grafana-data
docker volume rm gestion-des-enseignements_uasz_loki-data
```

## Dashboards Recommandés

### Dashboard DAOS (pré-configuré)
- Services Status
- HTTP Request Rate & Latency
- CPU & Memory Usage
- JVM Metrics

### Créer un dashboard personnalisé

1. Grafana → Dashboards → New Dashboard
2. Add Visualization
3. Datasource: Prometheus
4. Query: votre requête PromQL
5. Configurer le graphique
6. Save Dashboard

## Documentation Complète

Pour plus de détails, consulter:
- [MONITORING_GUIDE.md](./MONITORING_GUIDE.md) - Guide complet
- Documentation Prometheus: https://prometheus.io/docs/
- Documentation Grafana: https://grafana.com/docs/
- Documentation Jaeger: https://www.jaegertracing.io/docs/

---

**Système de Monitoring DAOS**
Université Assane Seck de Ziguinchor (UASZ)
