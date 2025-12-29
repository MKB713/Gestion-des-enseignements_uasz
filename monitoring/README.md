# Monitoring Stack - DAOS

Ce répertoire contient toutes les configurations pour le stack de monitoring et observabilité de DAOS.

## Structure

```
monitoring/
├── prometheus/
│   ├── prometheus.yml          # Configuration Prometheus
│   └── alert.rules.yml         # Règles d'alertes
├── grafana/
│   ├── provisioning/
│   │   ├── datasources/
│   │   │   └── datasources.yml # Configuration datasources
│   │   └── dashboards/
│   │       └── dashboards.yml  # Configuration dashboards
│   └── dashboards/
│       └── spring-boot-dashboard.json  # Dashboard pré-configuré
├── loki/
│   └── loki-config.yml         # Configuration Loki
├── promtail/
│   └── promtail-config.yml     # Configuration Promtail
├── alertmanager/
│   └── alertmanager.yml        # Configuration AlertManager
└── README.md
```

## Services

### Prometheus (port 9090)
- Collecte des métriques de tous les services
- Scrape interval: 15s
- Stockage: volume Docker `prometheus-data`
- Règles d'alertes configurées

**Targets**:
- Eureka Server (8761)
- Config Server (8888)
- API Gateway (8080)
- 6 microservices métier (8081-8086)
- MySQL Exporter (9104)
- Redis Exporter (9121)
- Node Exporter (9100)

### Grafana (port 3001)
- Visualisation des métriques, logs et traces
- Credentials: admin / admin
- Datasources pré-configurées: Prometheus, Loki, Jaeger
- Dashboard pré-configuré pour Spring Boot

### Jaeger (port 16686)
- Distributed tracing
- Compatible Zipkin (port 9411)
- UI pour visualiser les traces

### Loki (port 3100)
- Agrégation des logs
- Intégration avec Promtail
- Stockage: volume Docker `loki-data`

### Promtail
- Collecte des logs Docker
- Parsing des logs Spring Boot
- Extraction des labels et traceId

### AlertManager (port 9093)
- Gestion des alertes Prometheus
- Routing par severité
- Webhooks configurés

## Configuration

### Modifier la configuration Prometheus

1. Éditer `prometheus/prometheus.yml`
2. Ajouter/modifier les jobs de scraping
3. Redémarrer Prometheus:
```bash
docker-compose restart prometheus
```

### Ajouter une règle d'alerte

1. Éditer `prometheus/alert.rules.yml`
2. Ajouter une nouvelle règle:
```yaml
- alert: MonAlerte
  expr: ma_metrique > seuil
  for: 5m
  labels:
    severity: warning
  annotations:
    summary: "Description de l'alerte"
```
3. Recharger Prometheus:
```bash
curl -X POST http://localhost:9090/-/reload
```

### Ajouter un dashboard Grafana

1. Créer le dashboard dans Grafana UI
2. Export JSON
3. Copier dans `grafana/dashboards/`
4. Le dashboard sera automatiquement provisionné

### Configurer les notifications AlertManager

1. Éditer `alertmanager/alertmanager.yml`
2. Configurer les receivers (email, Slack, webhook)
3. Redémarrer AlertManager:
```bash
docker-compose restart alertmanager
```

## Métriques Exposées

Tous les microservices exposent:
- `/actuator/health` - Health check
- `/actuator/info` - Application info
- `/actuator/metrics` - Liste des métriques
- `/actuator/prometheus` - Métriques format Prometheus

## Alertes Configurées

### Critical
- ServiceDown
- EurekaServerDown
- ConfigServerDown
- ApiGatewayDown
- HighHttpErrorRate
- DatabaseConnectionPoolExhaustion

### Warning
- HighCpuUsage
- HighMemoryUsage
- SlowHttpRequests
- HighGatewayLatency
- HighGcTime
- TooManyThreads
- DiskSpaceLow

## Volumes Docker

- `prometheus-data`: Données Prometheus
- `grafana-data`: Dashboards et config Grafana
- `loki-data`: Logs Loki
- `alertmanager-data`: État AlertManager

## Guides

- [MONITORING_GUIDE.md](../MONITORING_GUIDE.md) - Guide complet
- [MONITORING_QUICK_START.md](../MONITORING_QUICK_START.md) - Démarrage rapide

## Support

Pour toute question sur le monitoring:
1. Consulter la documentation complète
2. Vérifier les logs Docker
3. Tester les endpoints Actuator
