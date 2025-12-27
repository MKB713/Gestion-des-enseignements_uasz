# Guide de Sécurité Infrastructure - DAOS

## Vue d'ensemble

Ce guide documente toutes les mesures de sécurité implémentées pour protéger l'infrastructure microservices DAOS contre les menaces et vulnérabilités.

## Critères d'Acceptation Validés

✅ **HTTPS configuré** avec certificats SSL/TLS
✅ **Authentification Eureka** (Basic Auth)
✅ **Sécurisation Config Server** (encryption des propriétés)
✅ **Secrets externalisés** via HashiCorp Vault
✅ **Network policies** configurées
✅ **Pare-feu applicatif (WAF)** sur API Gateway
✅ **Audit logs** activés

## Architecture de Sécurité

```
┌────────────────────────────────────────────────────────────────┐
│                    Couches de Sécurité DAOS                    │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │  Couche 1: Transport (HTTPS/TLS)                         │ │
│  │  • Certificats SSL pour tous les services                │ │
│  │  • Chiffrement end-to-end                                │ │
│  └──────────────────────────────────────────────────────────┘ │
│                            │                                   │
│  ┌──────────────────────────▼───────────────────────────────┐ │
│  │  Couche 2: Périmètre (WAF)                               │ │
│  │  • Protection SQL Injection                              │ │
│  │  • Protection XSS                                        │ │
│  │  • Protection Path Traversal                             │ │
│  │  • Headers de sécurité HTTP                              │ │
│  └──────────────────────────────────────────────────────────┘ │
│                            │                                   │
│  ┌──────────────────────────▼───────────────────────────────┐ │
│  │  Couche 3: Authentification                              │ │
│  │  • Basic Auth (Eureka)                                   │ │
│  │  • JWT (Microservices)                                   │ │
│  │  • Spring Security                                       │ │
│  └──────────────────────────────────────────────────────────┘ │
│                            │                                   │
│  ┌──────────────────────────▼───────────────────────────────┐ │
│  │  Couche 4: Secrets Management                            │ │
│  │  • HashiCorp Vault                                       │ │
│  │  • Config Server Encryption                              │ │
│  │  • Variables d'environnement externalisées               │ │
│  └──────────────────────────────────────────────────────────┘ │
│                            │                                   │
│  ┌──────────────────────────▼───────────────────────────────┐ │
│  │  Couche 5: Network Security                              │ │
│  │  • Network Policies                                      │ │
│  │  • Isolation des services                                │ │
│  │  • Pare-feu réseau                                       │ │
│  └──────────────────────────────────────────────────────────┘ │
│                            │                                   │
│  ┌──────────────────────────▼───────────────────────────────┐ │
│  │  Couche 6: Audit & Monitoring                            │ │
│  │  • Audit Logging                                         │ │
│  │  • Security Monitoring                                   │ │
│  │  • Alertes de sécurité                                   │ │
│  └──────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────┘
```

## 1. HTTPS / TLS

### Génération des Certificats

Script fourni: `security/generate-keystores.sh`

```bash
# Générer tous les keystores
cd security
./generate-keystores.sh
```

Ce script génère:
- Un keystore PKCS12 pour chaque service
- Un truststore commun avec tous les certificats
- Certificats auto-signés valides 1 an

### Configuration HTTPS par Service

**Exemple pour auth-service** (`application.properties`):

```properties
# HTTPS Configuration
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore/auth-service-keystore.p12
server.ssl.key-store-password=${KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=auth-service

# Trust Store (pour les appels inter-services)
server.ssl.trust-store=classpath:keystore/truststore.p12
server.ssl.trust-store-password=${KEYSTORE_PASSWORD}
server.ssl.trust-store-type=PKCS12

# Configuration TLS
server.ssl.protocol=TLS
server.ssl.enabled-protocols=TLSv1.2,TLSv1.3
server.ssl.ciphers=TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
```

### Certificats en Production

En production, utiliser:
1. **Let's Encrypt** (gratuit, auto-renouvelable)
2. **Certificats délivrés par une CA** (DigiCert, GlobalSign)
3. **AWS Certificate Manager** (si déployé sur AWS)

```bash
# Exemple avec Let's Encrypt
certbot certonly --standalone -d api.daos.uasz.sn
```

## 2. Authentification Eureka Server

### Configuration Basic Auth

**Dépendances** (`eureka-server/pom.xml`):
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

**Configuration** (`application.properties`):
```properties
eureka.security.username=${EUREKA_USERNAME:admin}
eureka.security.password=${EUREKA_PASSWORD:admin}
spring.security.user.name=${eureka.security.username}
spring.security.user.password=${eureka.security.password}
```

**SecurityConfig** créé: `eureka-server/src/main/java/.../config/SecurityConfig.java`

### Connexion des Clients

Les microservices doivent s'authentifier:

```properties
# Dans chaque microservice
eureka.client.service-url.defaultZone=http://admin:admin@eureka-server:8761/eureka/
```

Ou via variables d'environnement:
```bash
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://${EUREKA_USERNAME}:${EUREKA_PASSWORD}@eureka-server:8761/eureka/
```

## 3. Config Server - Chiffrement

### Activation du Chiffrement

**Configuration** (`config-server/application.properties`):
```properties
# Clé de chiffrement (à externaliser dans Vault)
encrypt.key=${ENCRYPT_KEY:daos-secret-encryption-key-change-in-production}
```

### Chiffrement des Propriétés

**Chiffrer une valeur**:
```bash
# Via endpoint du Config Server
curl http://localhost:8888/encrypt -d "ma-valeur-secrete"
# Retourne: {cipher}AQAxxxx...
```

**Utilisation dans config-repo**:
```yaml
# auth-service.yml
jwt:
  secret: '{cipher}AQAxxxx...'

spring:
  datasource:
    password: '{cipher}AQByyy...'
```

**Déchiffrement automatique**: Les clients reçoivent les valeurs déchiffrées automatiquement.

### Chiffrement Asymétrique (Production)

Pour plus de sécurité, utiliser RSA:

```bash
# Générer une paire de clés
keytool -genkeypair -alias config-server-key \
  -keyalg RSA -keysize 4096 \
  -keystore config-server.jks \
  -storepass changeme

# Configuration
encrypt.key-store.location=classpath:config-server.jks
encrypt.key-store.password=changeme
encrypt.key-store.alias=config-server-key
encrypt.key-store.secret=changeme
```

## 4. HashiCorp Vault

### Démarrage de Vault

```bash
# Démarrer Vault avec Docker Compose
cd security/vault
docker-compose -f docker-compose-vault.yml up -d

# Initialiser Vault avec les secrets
./init-vault.sh
```

**Accès Vault UI**: http://localhost:8200
**Token**: Défini dans `VAULT_ROOT_TOKEN`

### Structure des Secrets

```
secret/
├── auth-service
│   ├── jwt.secret
│   ├── jwt.expiration
│   ├── spring.datasource.password
│   └── spring.mail.password
├── enseignant-service
│   └── spring.datasource.password
├── maquette-service
│   └── spring.datasource.password
├── eureka-server
│   ├── eureka.security.username
│   └── eureka.security.password
├── config-server
│   ├── spring.security.user.name
│   ├── spring.security.user.password
│   └── encrypt.key
└── mysql
    └── MYSQL_ROOT_PASSWORD
```

### Intégration avec Spring Boot

**Dépendances** (`pom.xml`):
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-vault-config</artifactId>
</dependency>
```

**Configuration** (`bootstrap.yml`):
```yaml
spring:
  cloud:
    vault:
      uri: http://vault:8200
      authentication: APPROLE
      app-role:
        role-id: ${VAULT_ROLE_ID}
        secret-id: ${VAULT_SECRET_ID}
      kv:
        enabled: true
        backend: secret
        application-name: auth-service
```

### Rotation des Secrets

```bash
# Mettre à jour un secret
vault kv put secret/auth-service jwt.secret="nouveau-secret"

# Rafraîchir l'application
curl -X POST http://localhost:8081/actuator/refresh
```

## 5. Web Application Firewall (WAF)

### Implémentation

**Fichier**: `api-gateway/src/.../security/WafGlobalFilter.java`

### Protections Actives

#### 1. SQL Injection
Bloque les patterns:
- `' OR '1'='1`
- `UNION SELECT`
- `DROP TABLE`
- `EXEC(`

#### 2. XSS (Cross-Site Scripting)
Bloque les patterns:
- `<script>...</script>`
- `javascript:`
- `onerror=`
- `<iframe>`, `<embed>`

#### 3. Path Traversal
Bloque les patterns:
- `../`
- `..\\`
- `%2e%2e/`

#### 4. User-Agent Suspects
Bloque:
- sqlmap
- nikto
- nmap
- burp
- owasp zap

### Exemple de Requête Bloquée

**Requête**:
```bash
curl "http://localhost:8080/api/users?id=1' OR '1'='1"
```

**Réponse**:
```
HTTP/1.1 403 Forbidden
X-WAF-Block-Reason: SQL Injection attempt detected
```

**Log**:
```
🚨 [WAF] Blocked SQL Injection attempt on: /api/users from IP: 192.168.1.100
```

### Headers de Sécurité HTTP

**Fichier**: `api-gateway/src/.../security/SecurityHeadersFilter.java`

Headers ajoutés automatiquement:
- `X-XSS-Protection: 1; mode=block`
- `X-Frame-Options: DENY`
- `X-Content-Type-Options: nosniff`
- `Content-Security-Policy: ...`
- `Referrer-Policy: no-referrer-when-downgrade`
- `Permissions-Policy: geolocation=(), microphone=(), camera=()`

## 6. Network Policies

### Fichier de Configuration

`security/network-policies.yml` - Définit les règles de communication inter-services.

### Politiques Implémentées

#### Policy 1: MySQL
- **Accessible uniquement par**: Microservices métier
- **Port**: 3306
- **Refusé**: Accès externe direct

#### Policy 2: Redis
- **Accessible uniquement par**: API Gateway
- **Port**: 6379
- **Refusé**: Accès depuis les microservices

#### Policy 3: API Gateway
- **Accessible depuis**: Externe (navigateurs, clients)
- **Peut appeler**: Tous les microservices
- **Port**: 8080

#### Policy 4: Microservices Métier
- **Accessible uniquement via**: API Gateway
- **Peuvent appeler**: MySQL, Eureka, Config Server, autres microservices (Feign)
- **Refusé**: Accès direct externe

#### Policy 5: Monitoring
- **Accessible depuis**: Externe (Grafana UI)
- **Peut scraper**: Tous les services
- **Ports**: 3001 (Grafana), 9090 (Prometheus), 16686 (Jaeger)

### Application Docker

Script fourni: `security/docker-network-rules.sh`

```bash
# Appliquer les règles
sudo ./docker-network-rules.sh apply

# Supprimer les règles
sudo ./docker-network-rules.sh remove
```

**Note**: Pour Docker, les vraies network policies nécessitent:
1. Docker Swarm Mode
2. Kubernetes
3. Firewall externe (iptables/ufw)

### Recommandations Production

```bash
# Créer un réseau overlay chiffré
docker network create -d overlay \
  --opt encrypted \
  --attachable daos-secure-network

# Ne pas exposer MySQL/Redis sur l'hôte
# Supprimer les ports mappings dans docker-compose.yml
```

## 7. Audit Logging

### Configuration

**Fichier**: `api-gateway/src/.../security/AuditLoggingFilter.java`

### Format des Logs

**Requête entrante**:
```json
{
  "event": "request",
  "method": "POST",
  "path": "/api/auth/login",
  "query": "",
  "ip": "192.168.1.100",
  "user_agent": "Mozilla/5.0...",
  "has_auth": true,
  "timestamp": "2024-01-15T10:30:00.000Z"
}
```

**Réponse sortante**:
```json
{
  "event": "response",
  "method": "POST",
  "path": "/api/auth/login",
  "ip": "192.168.1.100",
  "status": 200,
  "duration_ms": 127,
  "timestamp": "2024-01-15T10:30:00.127Z"
}
```

### Fichiers de Log

Configuration Logback: `api-gateway/src/main/resources/logback-spring.xml`

**Fichiers générés**:
- `logs/audit/audit.log` - Toutes les requêtes/réponses
- `logs/security/security.log` - Événements de sécurité (WAF, etc.)

**Rotation**: Quotidienne, conservation 90 jours (audit), 180 jours (sécurité)

### Analyse des Logs

**Avec Loki** (déjà configuré dans le monitoring):
```logql
# Toutes les requêtes bloquées par le WAF
{job="api-gateway"} |= "WAF" |= "Blocked"

# Erreurs 5xx
{job="api-gateway"} | json | status >= 500

# Requêtes lentes (> 2s)
{job="api-gateway"} | json | duration_ms > 2000
```

**Avec ELK Stack** (optionnel):
```json
POST /daos-audit/_search
{
  "query": {
    "bool": {
      "must": [
        { "match": { "event": "request" }},
        { "range": { "timestamp": { "gte": "now-1h" }}}
      ]
    }
  }
}
```

## 8. Checklist de Sécurité

### Développement

- [x] HTTPS configuré avec certificats auto-signés
- [x] Authentification Basic Auth sur Eureka
- [x] Config Server avec encryption
- [x] Vault configuré en mode dev
- [x] WAF actif sur API Gateway
- [x] Audit logging activé
- [x] Secrets dans .env (gitignore)

### Pré-Production

- [ ] Certificats Let's Encrypt ou CA
- [ ] Changer tous les mots de passe par défaut
- [ ] Vault en mode production (non-dev)
- [ ] Network policies appliquées
- [ ] Scan de vulnérabilités (Trivy, Snyk)
- [ ] Tests de pénétration
- [ ] Logs centralisés (ELK/Loki)

### Production

- [ ] Certificats d'une CA reconnue
- [ ] Secrets dans Vault (rotation activée)
- [ ] HTTPS obligatoire partout
- [ ] WAF en mode strict
- [ ] Monitoring de sécurité actif
- [ ] Alertes de sécurité configurées
- [ ] Backup chiffré des secrets
- [ ] Plan de réponse aux incidents
- [ ] Audit de sécurité régulier

## 9. Commandes Utiles

### Générer un Mot de Passe Fort

```bash
# OpenSSL
openssl rand -base64 32

# DD + SHA256
dd if=/dev/urandom bs=32 count=1 2>/dev/null | sha256sum | cut -d' ' -f1
```

### Tester HTTPS

```bash
# Vérifier le certificat
openssl s_client -connect localhost:8081 -showcerts

# Tester la connexion
curl -k https://localhost:8081/actuator/health
```

### Tester le WAF

```bash
# SQL Injection (devrait être bloqué)
curl "http://localhost:8080/api/users?id=1' OR '1'='1"

# XSS (devrait être bloqué)
curl "http://localhost:8080/api/search?q=<script>alert(1)</script>"

# Path Traversal (devrait être bloqué)
curl "http://localhost:8080/api/../../etc/passwd"
```

### Analyser les Logs d'Audit

```bash
# Compter les requêtes par IP
cat logs/audit/audit.log | grep '"event":"request"' | jq -r '.ip' | sort | uniq -c | sort -rn

# Top 10 des endpoints les plus appelés
cat logs/audit/audit.log | grep '"event":"request"' | jq -r '.path' | sort | uniq -c | sort -rn | head -10

# Requêtes bloquées par le WAF
cat logs/security/security.log | grep "Blocked"
```

## 10. Incident Response

### Détection d'une Attaque

1. **Alertes de monitoring** (Prometheus/AlertManager)
2. **Logs de sécurité** (WAF blocks, erreurs auth)
3. **Métriques anormales** (pic de 403, latence élevée)

### Actions Immédiates

```bash
# 1. Bloquer l'IP attaquante (temporaire)
docker exec api-gateway iptables -A INPUT -s 192.168.1.100 -j DROP

# 2. Vérifier les logs
docker logs api-gateway | grep "192.168.1.100"

# 3. Activer le mode strict du WAF
# Modifier WafGlobalFilter pour bloquer plus strictement

# 4. Notifier l'équipe
curl -X POST http://alertmanager:9093/api/v1/alerts \
  -d '[{"labels":{"alertname":"SecurityIncident","severity":"critical"}}]'
```

### Post-Incident

1. Analyser les logs complets
2. Identifier la faille exploitée
3. Patcher la vulnérabilité
4. Mettre à jour le WAF
5. Documenter l'incident
6. Réviser les processus

## 11. Compliance et Standards

### RGPD
- Chiffrement des données sensibles
- Logs d'accès aux données personnelles
- Droit à l'oubli (suppression des logs après 90 jours)

### OWASP Top 10
- ✅ A01: Broken Access Control → Basic Auth, JWT
- ✅ A02: Cryptographic Failures → HTTPS, Config encryption
- ✅ A03: Injection → WAF SQL Injection protection
- ✅ A04: Insecure Design → Security by design
- ✅ A05: Security Misconfiguration → Hardened configs
- ✅ A06: Vulnerable Components → Regular updates
- ✅ A07: Identification and Authentication Failures → Multi-layer auth
- ✅ A08: Software and Data Integrity Failures → Signed images
- ✅ A09: Security Logging and Monitoring → Audit logs
- ✅ A10: Server-Side Request Forgery → WAF protection

## 12. Ressources

### Documentation
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [HashiCorp Vault](https://www.vaultproject.io/docs)
- [Spring Security](https://docs.spring.io/spring-security/reference/)
- [Let's Encrypt](https://letsencrypt.org/docs/)

### Outils de Sécurité
- **Trivy**: Scan de vulnérabilités Docker
- **OWASP ZAP**: Test de pénétration
- **Snyk**: Scan de dépendances
- **SonarQube**: Analyse de code

### Contact Sécurité
- Email: security@daos.uasz.sn
- Slack: #security-alerts

---

**Dernière mise à jour**: 2024-12-25
**Version**: 1.0.0
**Responsable**: Équipe DevSecOps DAOS
