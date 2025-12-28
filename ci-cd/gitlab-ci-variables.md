# Variables GitLab CI/CD pour DAOS

Ce document liste toutes les variables d'environnement à configurer dans GitLab CI pour le pipeline DAOS.

## Comment configurer les variables dans GitLab

1. Aller dans votre projet GitLab
2. Settings > CI/CD > Variables
3. Cliquer sur "Add variable"
4. Ajouter chaque variable ci-dessous

## Variables Docker Registry

### `CI_REGISTRY`
- **Description**: URL du Docker Registry GitLab
- **Valeur**: `registry.gitlab.com` (ou votre registry privé)
- **Protected**: ☑ Yes
- **Masked**: ☐ No

### `CI_REGISTRY_USER`
- **Description**: Nom d'utilisateur pour le Docker Registry
- **Valeur**: `gitlab-ci-token` (automatique GitLab) ou votre username
- **Protected**: ☑ Yes
- **Masked**: ☐ No

### `CI_REGISTRY_PASSWORD`
- **Description**: Mot de passe pour le Docker Registry
- **Valeur**: `$CI_JOB_TOKEN` (automatique GitLab) ou votre token
- **Protected**: ☑ Yes
- **Masked**: ☑ Yes

### `CI_REGISTRY_IMAGE`
- **Description**: Préfixe des images Docker
- **Valeur**: `registry.gitlab.com/votre-groupe/daos`
- **Protected**: ☐ No
- **Masked**: ☐ No

## Variables SonarQube

### `SONAR_HOST_URL`
- **Description**: URL du serveur SonarQube
- **Valeur**: `http://votre-sonarqube:9000` ou `https://sonarcloud.io`
- **Protected**: ☐ No
- **Masked**: ☐ No
- **Exemple**: `http://10.0.1.50:9000`

### `SONAR_TOKEN`
- **Description**: Token d'authentification SonarQube
- **Valeur**: Généré via le script `init-sonarqube.sh` ou dans SonarQube UI
- **Protected**: ☑ Yes
- **Masked**: ☑ Yes
- **Comment obtenir**:
  ```bash
  ./ci-cd/init-sonarqube.sh
  # ou
  # SonarQube > My Account > Security > Generate Token
  ```

### `SONAR_ADMIN_USER` (optionnel)
- **Description**: Nom d'utilisateur admin SonarQube
- **Valeur**: `admin`
- **Protected**: ☑ Yes
- **Masked**: ☐ No

### `SONAR_ADMIN_PASS` (optionnel)
- **Description**: Mot de passe admin SonarQube
- **Valeur**: Votre mot de passe SonarQube
- **Protected**: ☑ Yes
- **Masked**: ☑ Yes

## Variables de Déploiement - Environnement TEST

### `TEST_SERVER_HOST`
- **Description**: Adresse IP ou hostname du serveur de test
- **Valeur**: `10.0.2.50` ou `test.daos.uasz.sn`
- **Protected**: ☐ No
- **Masked**: ☐ No

### `TEST_SERVER_USER`
- **Description**: Utilisateur SSH pour le serveur de test
- **Valeur**: `deploy` ou `root`
- **Protected**: ☐ No
- **Masked**: ☐ No

### `SSH_PRIVATE_KEY`
- **Description**: Clé SSH privée pour connexion aux serveurs
- **Valeur**: Contenu de votre clé privée SSH
- **Protected**: ☑ Yes
- **Masked**: ☑ Yes
- **Type**: File
- **Comment générer**:
  ```bash
  ssh-keygen -t rsa -b 4096 -C "gitlab-ci@daos"
  cat ~/.ssh/id_rsa
  # Copier le contenu dans la variable
  ```

## Variables de Déploiement - Environnement PRODUCTION

### `PROD_SERVER_HOST`
- **Description**: Adresse IP ou hostname du serveur de production
- **Valeur**: `10.0.3.50` ou `prod.daos.uasz.sn`
- **Protected**: ☑ Yes
- **Masked**: ☐ No

### `PROD_SERVER_USER`
- **Description**: Utilisateur SSH pour le serveur de production
- **Valeur**: `deploy`
- **Protected**: ☑ Yes
- **Masked**: ☐ No

## Variables de Notification Email

### `EMAIL_NOTIFICATION_URL`
- **Description**: URL de l'API pour envoyer les emails
- **Valeur**: `https://api.sendgrid.com/v3/mail/send` ou votre API SMTP
- **Protected**: ☐ No
- **Masked**: ☐ No
- **Alternatives**:
  - SendGrid: `https://api.sendgrid.com/v3/mail/send`
  - Mailgun: `https://api.mailgun.net/v3/YOUR_DOMAIN/messages`
  - SMTP2GO: `https://api.smtp2go.com/v3/email/send`

### `NOTIFICATION_EMAIL`
- **Description**: Adresse email pour recevoir les notifications
- **Valeur**: `devops@uasz.sn` ou `votre-email@example.com`
- **Protected**: ☐ No
- **Masked**: ☐ No

### `SENDGRID_API_KEY` (si utilisation SendGrid)
- **Description**: Clé API SendGrid pour l'envoi d'emails
- **Valeur**: `SG.xxxxxxxxxxxxxxxxxxxxx`
- **Protected**: ☑ Yes
- **Masked**: ☑ Yes

## Variables de Sécurité (optionnelles mais recommandées)

### `ENCRYPT_KEY`
- **Description**: Clé de chiffrement pour Config Server
- **Valeur**: Chaîne aléatoire de 32+ caractères
- **Protected**: ☑ Yes
- **Masked**: ☑ Yes
- **Générer**:
  ```bash
  openssl rand -base64 32
  ```

### `VAULT_ROOT_TOKEN`
- **Description**: Token root pour HashiCorp Vault
- **Valeur**: Token généré lors de l'initialisation de Vault
- **Protected**: ☑ Yes
- **Masked**: ☑ Yes

### `EUREKA_USERNAME`
- **Description**: Nom d'utilisateur pour Eureka Server
- **Valeur**: `admin` ou personnalisé
- **Protected**: ☑ Yes
- **Masked**: ☐ No

### `EUREKA_PASSWORD`
- **Description**: Mot de passe pour Eureka Server
- **Valeur**: Mot de passe sécurisé
- **Protected**: ☑ Yes
- **Masked**: ☑ Yes

## Variables MySQL (pour les tests d'intégration)

### `MYSQL_ROOT_PASSWORD`
- **Description**: Mot de passe root MySQL pour les tests
- **Valeur**: `root` (test uniquement)
- **Protected**: ☐ No
- **Masked**: ☑ Yes

### `MYSQL_DATABASE`
- **Description**: Nom de la base de données de test
- **Valeur**: `daos_test`
- **Protected**: ☐ No
- **Masked**: ☐ No

## Configuration SendGrid pour les Notifications Email

Si vous utilisez SendGrid pour les notifications:

### Étape 1: Créer un compte SendGrid
1. S'inscrire sur https://sendgrid.com
2. Vérifier votre email

### Étape 2: Générer une clé API
1. Settings > API Keys > Create API Key
2. Nom: `GitLab CI DAOS`
3. Permissions: Full Access ou Mail Send
4. Copier la clé générée

### Étape 3: Vérifier l'email expéditeur
1. Settings > Sender Authentication
2. Verify a Single Sender
3. Ajouter votre email: `noreply@uasz.sn`

### Étape 4: Modifier le script de notification dans `.gitlab-ci.yml`

Remplacer dans les jobs `notify:success` et `notify:failure`:

```yaml
script:
  - |
    curl -X POST "https://api.sendgrid.com/v3/mail/send" \
      -H "Authorization: Bearer $SENDGRID_API_KEY" \
      -H "Content-Type: application/json" \
      -d '{
        "personalizations": [{
          "to": [{"email": "'"$NOTIFICATION_EMAIL"'"}],
          "subject": "✅ Pipeline CI/CD DAOS - SUCCESS"
        }],
        "from": {"email": "noreply@uasz.sn", "name": "GitLab CI DAOS"},
        "content": [{
          "type": "text/plain",
          "value": "Pipeline #'"$CI_PIPELINE_ID"' completed successfully for commit '"$CI_COMMIT_SHORT_SHA"' on branch '"$CI_COMMIT_REF_NAME"' by '"$GITLAB_USER_NAME"'"
        }]
      }'
```

## Résumé des Variables Obligatoires

### Minimum pour faire fonctionner le pipeline:

1. **Docker Registry** (automatique GitLab):
   - ✅ `CI_REGISTRY_USER` (automatique)
   - ✅ `CI_REGISTRY_PASSWORD` (automatique)

2. **SonarQube** (pour l'analyse de code):
   - ⚠️ `SONAR_HOST_URL`
   - ⚠️ `SONAR_TOKEN`

3. **Déploiement** (si vous voulez déployer):
   - ⚠️ `TEST_SERVER_HOST`
   - ⚠️ `TEST_SERVER_USER`
   - ⚠️ `SSH_PRIVATE_KEY`

4. **Notifications** (optionnel):
   - ⚠️ `EMAIL_NOTIFICATION_URL`
   - ⚠️ `NOTIFICATION_EMAIL`
   - ⚠️ `SENDGRID_API_KEY` (si SendGrid)

## Vérification de la Configuration

Après avoir configuré les variables, vérifiez la configuration:

```bash
# Dans GitLab CI, ajouter un job temporaire de debug
debug:variables:
  stage: .pre
  script:
    - echo "CI_REGISTRY=$CI_REGISTRY"
    - echo "SONAR_HOST_URL=$SONAR_HOST_URL"
    - echo "TEST_SERVER_HOST=$TEST_SERVER_HOST"
    # NE JAMAIS AFFICHER LES SECRETS!
```

## Troubleshooting

### Erreur: "Cannot connect to Docker daemon"
- Vérifier que le runner GitLab a accès à Docker
- Vérifier que le service `docker:dind` est configuré

### Erreur: "SonarQube analysis failed"
- Vérifier `SONAR_HOST_URL` (accessible depuis le runner?)
- Vérifier `SONAR_TOKEN` (valide?)
- Vérifier que SonarQube est démarré

### Erreur: "SSH connection refused"
- Vérifier `TEST_SERVER_HOST` (IP correcte?)
- Vérifier `SSH_PRIVATE_KEY` (format correct?)
- Vérifier que la clé publique est dans `~/.ssh/authorized_keys` du serveur

### Erreur: "Email notification failed"
- Vérifier `EMAIL_NOTIFICATION_URL`
- Vérifier `SENDGRID_API_KEY`
- Vérifier que l'email expéditeur est vérifié dans SendGrid
