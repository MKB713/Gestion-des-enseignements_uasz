# Guide CI/CD - DAOS Gestion des Enseignements UASZ

Guide complet pour le pipeline CI/CD automatisé avec GitLab CI, SonarQube, Docker et déploiement automatique.

## Table des Matières

1. [Vue d'ensemble](#vue-densemble)
2. [Architecture du Pipeline](#architecture-du-pipeline)
3. [Prérequis](#prérequis)
4. [Installation et Configuration](#installation-et-configuration)
5. [Utilisation du Pipeline](#utilisation-du-pipeline)
6. [SonarQube - Analyse de Code](#sonarqube---analyse-de-code)
7. [Déploiement Automatique](#déploiement-automatique)
8. [Notifications](#notifications)
9. [Troubleshooting](#troubleshooting)
10. [Best Practices](#best-practices)

---

## Vue d'ensemble

Le pipeline CI/CD DAOS automatise:

✅ **Build** - Compilation de tous les microservices Java et du frontend
✅ **Test** - Exécution des tests unitaires et d'intégration
✅ **Analyze** - Analyse statique de code avec SonarQube
✅ **Docker Build** - Construction des images Docker
✅ **Docker Push** - Publication vers le Docker Registry
✅ **Deploy** - Déploiement automatique en TEST et manuel en PROD
✅ **Notify** - Notifications email des résultats

### Bénéfices

- ⚡ **Livraisons rapides**: Déploiement en quelques minutes
- 🔒 **Qualité garantie**: Tests et analyse automatiques
- 🚀 **Zéro downtime**: Rolling updates en production
- 📧 **Visibilité**: Notifications email automatiques
- 🔄 **Rollback**: Retour arrière automatique en cas d'échec

---

## Architecture du Pipeline

### Diagramme du Pipeline

```
┌──────────────┐
│   Commit     │  Developer pousse du code
└──────┬───────┘
       │
       v
┌──────────────────────────────────────────────────────┐
│                   STAGE 1: BUILD                     │
│  - Compilation de tous les microservices Maven       │
│  - Build du frontend Node.js                        │
│  - Génération des artifacts (.jar, dist/)           │
└──────┬───────────────────────────────────────────────┘
       │
       v
┌──────────────────────────────────────────────────────┐
│                   STAGE 2: TEST                      │
│  - Tests unitaires (JUnit)                          │
│  - Tests d'intégration avec MySQL et Redis          │
│  - Tests frontend (Jest, React Testing Library)     │
│  - Rapports de couverture (JaCoCo)                  │
└──────┬───────────────────────────────────────────────┘
       │
       v
┌──────────────────────────────────────────────────────┐
│                   STAGE 3: ANALYZE                   │
│  - Analyse SonarQube de tous les services           │
│  - Vérification des Quality Gates                   │
│  - Détection bugs, vulnérabilités, code smells      │
└──────┬───────────────────────────────────────────────┘
       │
       v
┌──────────────────────────────────────────────────────┐
│                STAGE 4: DOCKER BUILD                 │
│  - Build des images Docker par service              │
│  - Tag avec SHA du commit + latest                  │
│  - Optimisation multi-stage build                   │
└──────┬───────────────────────────────────────────────┘
       │
       v
┌──────────────────────────────────────────────────────┐
│                STAGE 5: DOCKER PUSH                  │
│  - Authentification au Docker Registry              │
│  - Push de toutes les images                        │
│  - Disponible pour déploiement                      │
└──────┬───────────────────────────────────────────────┘
       │
       v
┌──────────────────────────────────────────────────────┐
│                   STAGE 6: DEPLOY                    │
│  TEST: Automatique sur branche develop              │
│  PROD: Manuel sur branche main                      │
│  - Rolling update sans downtime                     │
│  - Health checks automatiques                       │
│  - Rollback automatique si échec                    │
└──────┬───────────────────────────────────────────────┘
       │
       v
┌──────────────────────────────────────────────────────┐
│                   STAGE 7: NOTIFY                    │
│  - Email de succès ou échec                         │
│  - Détails du commit et du pipeline                 │
└──────────────────────────────────────────────────────┘
```

### Services Concernés

**Backend (9 microservices Spring Boot):**
- config-server
- eureka-server
- api-gateway
- auth-service
- enseignant-service
- maquette-service
- choix-enseignement-service
- deroulement-enseignement-service
- emploi-temps-service

**Frontend:**
- front-end-main (React/Angular)

**Infrastructure:**
- MySQL 8.0
- Redis 7
- SonarQube 10.3

---

## Prérequis

### 1. GitLab Runner

Installer un GitLab Runner avec Docker:

```bash
# Installer GitLab Runner
curl -L https://packages.gitlab.com/install/repositories/runner/gitlab-runner/script.deb.sh | sudo bash
sudo apt-get install gitlab-runner

# Enregistrer le runner
sudo gitlab-runner register \
  --url https://gitlab.com/ \
  --registration-token YOUR_TOKEN \
  --executor docker \
  --docker-image docker:24-dind \
  --docker-privileged
```

### 2. SonarQube

Démarrer SonarQube avec Docker Compose:

```bash
cd ci-cd
docker-compose -f docker-compose-sonarqube.yml up -d

# Attendre le démarrage (2-3 minutes)
docker logs -f daos-sonarqube

# Initialiser SonarQube
chmod +x init-sonarqube.sh
./init-sonarqube.sh
```

Accès: http://localhost:9000
Login: admin / admin (à changer)

### 3. Serveurs de Déploiement

**Serveur TEST:**
```bash
# Installer Docker et Docker Compose
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Créer l'utilisateur de déploiement
sudo useradd -m -s /bin/bash deploy
sudo mkdir /opt/daos
sudo chown deploy:deploy /opt/daos

# Configurer SSH
sudo su - deploy
mkdir ~/.ssh
chmod 700 ~/.ssh
# Ajouter la clé publique dans ~/.ssh/authorized_keys
```

**Serveur PRODUCTION:** (même configuration)

### 4. Variables GitLab CI

Configurer toutes les variables listées dans [gitlab-ci-variables.md](ci-cd/gitlab-ci-variables.md):

**GitLab > Settings > CI/CD > Variables**

Variables obligatoires:
- `SONAR_HOST_URL`
- `SONAR_TOKEN`
- `TEST_SERVER_HOST`
- `TEST_SERVER_USER`
- `SSH_PRIVATE_KEY`
- `NOTIFICATION_EMAIL`
- `SENDGRID_API_KEY` (optionnel)

---

## Installation et Configuration

### Étape 1: Cloner le Repository

```bash
git clone https://gitlab.com/votre-groupe/daos.git
cd daos
```

### Étape 2: Vérifier les Fichiers CI/CD

```bash
ls -la .gitlab-ci.yml
ls -la ci-cd/
```

Fichiers présents:
- `.gitlab-ci.yml` - Pipeline principal
- `sonar-project.properties` - Configuration SonarQube
- `ci-cd/docker-compose-sonarqube.yml` - SonarQube Docker
- `ci-cd/init-sonarqube.sh` - Initialisation SonarQube
- `ci-cd/deploy-test.sh` - Script déploiement TEST
- `ci-cd/deploy-prod.sh` - Script déploiement PROD
- `ci-cd/gitlab-ci-variables.md` - Documentation variables

### Étape 3: Démarrer SonarQube Localement (Optionnel)

```bash
cd ci-cd
docker-compose -f docker-compose-sonarqube.yml up -d

# Vérifier le démarrage
docker logs -f daos-sonarqube

# Initialiser (une fois démarré)
chmod +x init-sonarqube.sh
./init-sonarqube.sh
```

### Étape 4: Configurer les Variables GitLab

1. Aller dans GitLab: **Settings > CI/CD > Variables**
2. Ajouter les variables suivantes (minimum):

```bash
# SonarQube
SONAR_HOST_URL=http://votre-ip:9000
SONAR_TOKEN=squ_xxxxxxxxxxxxx

# Déploiement TEST
TEST_SERVER_HOST=10.0.2.50
TEST_SERVER_USER=deploy
SSH_PRIVATE_KEY=<contenu de la clé SSH privée>

# Notifications
NOTIFICATION_EMAIL=devops@uasz.sn
SENDGRID_API_KEY=SG.xxxxxxxxxxxxx
```

### Étape 5: Copier docker-compose.yml sur les Serveurs

```bash
# Sur le serveur TEST
scp docker-compose.yml deploy@test-server:/opt/daos/
scp .env.example deploy@test-server:/opt/daos/.env

# Sur le serveur PROD
scp docker-compose.yml deploy@prod-server:/opt/daos/
scp .env.example deploy@prod-server:/opt/daos/.env
```

### Étape 6: Premier Commit

```bash
git add .
git commit -m "feat: Configure CI/CD pipeline"
git push origin develop
```

Le pipeline se lance automatiquement!

---

## Utilisation du Pipeline

### Workflow de Développement

#### 1. Développement sur Feature Branch

```bash
git checkout -b feature/nouvelle-fonctionnalite
# Développer...
git add .
git commit -m "feat: Nouvelle fonctionnalité"
git push origin feature/nouvelle-fonctionnalite
```

**Résultat:** Aucun pipeline (sauf si configuré)

#### 2. Merge Request vers Develop

```bash
# Créer une Merge Request dans GitLab
# GitLab UI: Create Merge Request
```

**Résultat:** Pipeline complet s'exécute (Build > Test > Analyze)

#### 3. Merge vers Develop

```bash
# Après approbation de la MR
git checkout develop
git merge feature/nouvelle-fonctionnalite
git push origin develop
```

**Résultat:** Pipeline complet + Build Docker + Push Registry + Option déploiement TEST

#### 4. Release vers Main (Production)

```bash
git checkout main
git merge develop
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin main --tags
```

**Résultat:** Pipeline complet + Option déploiement PRODUCTION (manuel)

### Déclencher le Pipeline Manuellement

Dans GitLab:
1. **CI/CD > Pipelines**
2. **Run Pipeline**
3. Sélectionner la branche
4. Cliquer **Run Pipeline**

### Déploiement Manuel

Le déploiement en TEST et PRODUCTION nécessite une action manuelle:

1. Aller dans **CI/CD > Pipelines**
2. Cliquer sur le pipeline en cours
3. Dans le stage **deploy**, cliquer sur le bouton ▶️ **Play**
4. Confirmer le déploiement

---

## SonarQube - Analyse de Code

### Configuration SonarQube

Le fichier `sonar-project.properties` configure:
- Chemins des sources Java et JavaScript
- Exclusions (tests, generated code)
- Rapports de couverture (JaCoCo)
- Quality Gates

### Quality Gates Configurés

| Métrique | Condition | Seuil |
|----------|-----------|-------|
| Coverage | ≥ | 80% |
| Bugs | = | 0 |
| Vulnerabilities | = | 0 |
| Code Smells | ≤ | 10 |
| Duplications | ≤ | 3% |

### Consulter les Résultats SonarQube

1. Ouvrir SonarQube: http://votre-sonarqube:9000
2. Se connecter (admin / admin)
3. Voir tous les projets DAOS
4. Cliquer sur un projet pour les détails

### Interpréter les Résultats

**Quality Gate: PASSED ✅**
- Le code respecte tous les critères de qualité
- Le pipeline peut continuer

**Quality Gate: FAILED ❌**
- Le code ne respecte pas les critères
- Le pipeline continue mais avec warning
- À corriger avant le merge

### Exemple de Corrections

**Bug détecté:**
```java
// AVANT (Bug: NullPointerException possible)
public String getName() {
    return user.getName();
}

// APRÈS (Correction)
public String getName() {
    return user != null ? user.getName() : "Unknown";
}
```

**Vulnérabilité détectée:**
```java
// AVANT (Vulnérabilité: SQL Injection)
String query = "SELECT * FROM users WHERE id = " + userId;

// APRÈS (Correction: Prepared Statement)
String query = "SELECT * FROM users WHERE id = ?";
PreparedStatement stmt = connection.prepareStatement(query);
stmt.setInt(1, userId);
```

---

## Déploiement Automatique

### Architecture de Déploiement

```
┌─────────────────────┐
│   GitLab Runner     │
│   (CI/CD Server)    │
└──────────┬──────────┘
           │ SSH
           v
┌─────────────────────┐         ┌─────────────────────┐
│   TEST Server       │         │   PROD Server       │
│   10.0.2.50         │         │   10.0.3.50         │
│                     │         │                     │
│  /opt/daos/         │         │  /opt/daos/         │
│  ├── docker-compose │         │  ├── docker-compose │
│  ├── .env           │         │  ├── .env           │
│  └── backups/       │         │  └── backups/       │
└─────────────────────┘         └─────────────────────┘
```

### Script deploy-test.sh

**Fonctionnalités:**
1. ✅ Vérification des prérequis (Docker, Docker Compose)
2. ✅ Création de sauvegarde (état conteneurs, .env)
3. ✅ Arrêt des services actuels
4. ✅ Pull des nouvelles images Docker
5. ✅ Démarrage des nouveaux services
6. ✅ Health checks automatiques
7. ✅ Rollback automatique en cas d'échec

**Utilisation standalone:**
```bash
# Sur le serveur TEST
cd /opt/daos
./ci-cd/deploy-test.sh
```

### Script deploy-prod.sh

**Fonctionnalités supplémentaires:**
1. ✅ Confirmation manuelle obligatoire
2. ✅ Vérification de l'espace disque
3. ✅ Sauvegarde complète de MySQL
4. ✅ Rolling update sans downtime
5. ✅ Smoke tests complets
6. ✅ Surveillance post-déploiement (5 minutes)
7. ✅ Nettoyage des anciennes sauvegardes

**Utilisation standalone:**
```bash
# Sur le serveur PRODUCTION
cd /opt/daos
./ci-cd/deploy-prod.sh

# Confirmation requise:
# Êtes-vous sûr de vouloir continuer? (oui/non): oui
```

### Rolling Update (Production)

Le rolling update met à jour les services progressivement:

1. Config Server ⬆️ (nouveau conteneur démarre)
2. Eureka Server ⬆️
3. API Gateway ⬆️
4. Services métier ⬆️ (un par un)
5. Frontend ⬆️

**Avantages:**
- ✅ Pas de downtime
- ✅ Détection rapide des problèmes
- ✅ Rollback partiel possible

### Rollback Automatique

En cas d'échec du déploiement:

1. 🛑 Arrêt des nouveaux conteneurs
2. ⏪ Restauration des variables .env
3. ⏪ Restauration de MySQL (si backup disponible)
4. ✅ Redémarrage des anciens conteneurs
5. 📧 Notification d'échec envoyée

**Rollback manuel:**
```bash
# Sur le serveur
cd /opt/daos/backups

# Lister les sauvegardes
ls -lh backup-*.tar.gz

# Restaurer une sauvegarde
tar -xzf backup-20231225-143000.tar.gz
cp .env-20231225-143000 ../.env
docker exec -i daos-mysql mysql -u root -proot < mysql-backup-20231225-143000.sql
cd ..
docker-compose up -d
```

---

## Notifications

### Configuration SendGrid

1. **Créer un compte SendGrid:**
   - Aller sur https://sendgrid.com
   - S'inscrire (plan gratuit: 100 emails/jour)

2. **Générer une clé API:**
   ```
   SendGrid > Settings > API Keys > Create API Key
   Nom: GitLab CI DAOS
   Permissions: Mail Send
   ```

3. **Vérifier l'expéditeur:**
   ```
   Settings > Sender Authentication
   Verify a Single Sender
   Email: noreply@uasz.sn
   ```

4. **Ajouter dans GitLab CI:**
   ```
   SENDGRID_API_KEY=SG.xxxxxxxxxxxxx
   NOTIFICATION_EMAIL=devops@uasz.sn
   ```

### Format des Emails

**Email de Succès ✅:**
```
From: GitLab CI DAOS <noreply@uasz.sn>
To: devops@uasz.sn
Subject: ✅ Pipeline CI/CD DAOS - SUCCESS

Pipeline #12345 completed successfully

Commit: a1b2c3d4
Branch: main
Author: Jean Dupont
Duration: 8min 32s

View pipeline: https://gitlab.com/daos/pipelines/12345
```

**Email d'Échec ❌:**
```
From: GitLab CI DAOS <noreply@uasz.sn>
To: devops@uasz.sn
Subject: ❌ Pipeline CI/CD DAOS - FAILED

Pipeline #12346 FAILED

Commit: e5f6g7h8
Branch: develop
Author: Marie Martin
Failed Stage: test
Duration: 3min 15s

View pipeline: https://gitlab.com/daos/pipelines/12346
```

### Alternative: Webhook Discord/Slack

Si pas d'email, utiliser un webhook:

```yaml
notify:success:
  script:
    - |
      curl -X POST "$DISCORD_WEBHOOK_URL" \
        -H "Content-Type: application/json" \
        -d "{
          \"content\": \"✅ Pipeline DAOS réussi!\",
          \"embeds\": [{
            \"title\": \"Pipeline #$CI_PIPELINE_ID\",
            \"description\": \"Commit $CI_COMMIT_SHORT_SHA par $GITLAB_USER_NAME\",
            \"color\": 3066993
          }]
        }"
```

---

## Troubleshooting

### Problème: Pipeline ne démarre pas

**Symptôme:** Aucun pipeline après un commit

**Causes possibles:**
1. Runner GitLab non disponible
2. Fichier `.gitlab-ci.yml` invalide
3. Branche non configurée pour CI/CD

**Solutions:**
```bash
# Vérifier la syntaxe du .gitlab-ci.yml
cat .gitlab-ci.yml | docker run --rm -i ghcr.io/tomtom-international/commisery-action:1 check

# Vérifier les runners disponibles
# GitLab > Settings > CI/CD > Runners

# Forcer le lancement
# GitLab > CI/CD > Run Pipeline
```

### Problème: Build Maven échoue

**Symptôme:** `mvn clean package` échoue

**Causes possibles:**
1. Dépendances Maven non disponibles
2. Version Java incorrecte
3. Erreur de compilation

**Solutions:**
```bash
# Nettoyer le cache Maven
# GitLab > CI/CD > Pipelines > Clear Runner Cache

# Vérifier localement
cd auth-service
mvn clean package

# Vérifier la version Java dans .gitlab-ci.yml
# image: maven:3.9-eclipse-temurin-17
```

### Problème: Tests échouent

**Symptôme:** Stage test en erreur

**Causes possibles:**
1. Services MySQL/Redis non disponibles
2. Tests mal écrits
3. Configuration de test incorrecte

**Solutions:**
```bash
# Vérifier les services dans .gitlab-ci.yml
services:
  - mysql:8.0
  - redis:7-alpine

# Exécuter les tests localement
cd auth-service
mvn test

# Ignorer temporairement les tests (non recommandé)
mvn clean package -DskipTests
```

### Problème: SonarQube inaccessible

**Symptôme:** `sonar:sonar` échoue avec "Connection refused"

**Causes possibles:**
1. SonarQube non démarré
2. URL incorrecte
3. Token invalide

**Solutions:**
```bash
# Vérifier SonarQube
curl http://votre-sonarqube:9000/api/system/status

# Vérifier les variables GitLab
# SONAR_HOST_URL doit être accessible depuis le runner
# Tester: ping votre-sonarqube

# Régénérer le token
./ci-cd/init-sonarqube.sh
```

### Problème: Docker build échoue

**Symptôme:** `docker build` échoue

**Causes possibles:**
1. Dockerfile incorrect
2. Docker daemon inaccessible
3. Manque d'espace disque

**Solutions:**
```bash
# Vérifier le Dockerfile localement
cd auth-service
docker build -t test .

# Vérifier l'espace disque sur le runner
df -h

# Nettoyer les images Docker
docker system prune -a
```

### Problème: Déploiement échoue

**Symptôme:** SSH connection refused ou health check failed

**Causes possibles:**
1. Serveur inaccessible
2. Clé SSH incorrecte
3. Services ne démarrent pas

**Solutions:**
```bash
# Tester SSH manuellement
ssh deploy@test-server

# Vérifier la clé SSH dans GitLab
# Elle doit être au format PEM sans passphrase

# Vérifier les logs sur le serveur
ssh deploy@test-server
cd /opt/daos
docker-compose logs -f
```

### Problème: Email non reçu

**Symptôme:** Pas d'email de notification

**Causes possibles:**
1. SendGrid API key invalide
2. Email expéditeur non vérifié
3. Email dans les spams

**Solutions:**
```bash
# Tester SendGrid manuellement
curl -X POST "https://api.sendgrid.com/v3/mail/send" \
  -H "Authorization: Bearer $SENDGRID_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "personalizations": [{
      "to": [{"email": "votre@email.com"}]
    }],
    "from": {"email": "noreply@uasz.sn"},
    "subject": "Test",
    "content": [{"type": "text/plain", "value": "Test email"}]
  }'

# Vérifier SendGrid Dashboard
# https://app.sendgrid.com > Activity
```

---

## Best Practices

### 1. Gestion des Branches

```
main (production)
  └─ develop (test)
      ├─ feature/auth
      ├─ feature/notifications
      └─ bugfix/login-error
```

**Règles:**
- ✅ `main` = Production stable
- ✅ `develop` = Environnement de test
- ✅ `feature/*` = Nouvelles fonctionnalités
- ✅ `bugfix/*` = Corrections de bugs
- ✅ `hotfix/*` = Corrections urgentes production

### 2. Commits Conventionnels

```bash
# Format
<type>(<scope>): <description>

# Exemples
feat(auth): Add JWT token refresh
fix(api-gateway): Fix CORS configuration
docs(readme): Update installation guide
test(enseignant): Add unit tests for service
refactor(maquette): Simplify entity mapping
```

**Types:**
- `feat`: Nouvelle fonctionnalité
- `fix`: Correction de bug
- `docs`: Documentation
- `test`: Tests
- `refactor`: Refactoring
- `perf`: Amélioration performance
- `ci`: Modification CI/CD

### 3. Tests

**Pyramid de tests:**
```
         /\
        /  \  E2E (End-to-End)
       /____\
      /      \  Integration Tests
     /________\
    /          \  Unit Tests
   /____________\
```

**Couverture minimale:**
- Unit Tests: 80%
- Integration Tests: 60%
- E2E Tests: Scénarios critiques

### 4. Code Review

**Checklist avant Merge:**
- ✅ Tests passent (verts)
- ✅ SonarQube Quality Gate passed
- ✅ Code review approuvé
- ✅ Documentation à jour
- ✅ Pas de secrets dans le code
- ✅ Logs significatifs ajoutés

### 5. Sécurité

**Secrets Management:**
```bash
# ❌ JAMAIS ça
spring.datasource.password=root123

# ✅ Toujours ça
spring.datasource.password=${DB_PASSWORD}
```

**Dans GitLab CI:**
- ✅ Utiliser les variables protégées et masquées
- ✅ Ne jamais logger les secrets
- ✅ Utiliser HashiCorp Vault pour les secrets sensibles

### 6. Monitoring

**Après chaque déploiement:**
1. ✅ Vérifier les logs: `docker-compose logs -f`
2. ✅ Vérifier Grafana: Dashboards système et applicatifs
3. ✅ Vérifier Jaeger: Traces distribuées
4. ✅ Vérifier les métriques: CPU, RAM, I/O

### 7. Rollback Strategy

**Plan de rollback:**
1. **Automatique** (< 5 min): Échec health check → rollback auto
2. **Manuel rapide** (< 15 min): Restaurer backup
3. **Manuel complet** (< 1h): Redéployer version précédente

**Commande rollback rapide:**
```bash
cd /opt/daos
docker-compose down
git checkout tags/v1.0.0
docker-compose up -d
```

### 8. Documentation

**À maintenir à jour:**
- ✅ README.md (guide installation)
- ✅ API documentation (Swagger)
- ✅ Architecture diagrams
- ✅ Runbooks (procédures opérationnelles)
- ✅ Changelog (CHANGELOG.md)

---

## Annexes

### A. Fichiers du Projet

```
daos/
├── .gitlab-ci.yml                    # Pipeline principal
├── sonar-project.properties          # Config SonarQube
├── docker-compose.yml                # Services Docker
├── .env.example                      # Variables d'environnement
│
├── ci-cd/
│   ├── docker-compose-sonarqube.yml  # SonarQube standalone
│   ├── init-sonarqube.sh             # Initialisation SonarQube
│   ├── deploy-test.sh                # Déploiement TEST
│   ├── deploy-prod.sh                # Déploiement PROD
│   └── gitlab-ci-variables.md        # Documentation variables
│
├── config-server/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│
├── eureka-server/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│
├── api-gateway/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│
├── [autres microservices...]
│
└── front-end-main/
    ├── Dockerfile
    ├── package.json
    └── src/
```

### B. Commandes Utiles

```bash
# GitLab CI
gitlab-runner status                  # Statut du runner
gitlab-runner verify                  # Vérifier la configuration
gitlab-ci-multi-runner exec docker test  # Tester un job localement

# Docker
docker-compose ps                     # Voir les conteneurs
docker-compose logs -f SERVICE        # Voir les logs d'un service
docker system df                      # Espace disque Docker
docker system prune -a                # Nettoyer Docker

# SonarQube
curl $SONAR_URL/api/system/status    # Statut SonarQube
curl $SONAR_URL/api/qualitygates/project_status?projectKey=daos-auth-service

# Déploiement
ssh deploy@server "cd /opt/daos && docker-compose ps"
ssh deploy@server "cd /opt/daos && docker-compose restart api-gateway"
```

### C. Ressources

**Documentation officielle:**
- GitLab CI/CD: https://docs.gitlab.com/ee/ci/
- SonarQube: https://docs.sonarqube.org/
- Docker: https://docs.docker.com/
- SendGrid: https://docs.sendgrid.com/

**Outils:**
- GitLab CI Lint: https://gitlab.com/ci/lint
- SonarQube Quality Gates: https://docs.sonarqube.org/latest/user-guide/quality-gates/
- Docker Hub: https://hub.docker.com/

---

## Support

Pour toute question ou problème:

1. **Consulter la documentation**: Ce guide + gitlab-ci-variables.md
2. **Vérifier les logs GitLab**: CI/CD > Pipelines > Job logs
3. **Consulter SonarQube**: http://votre-sonarqube:9000
4. **Contact**: devops@uasz.sn

---

**Version:** 1.0.0
**Dernière mise à jour:** 2023-12-25
**Auteur:** Équipe DevOps DAOS
