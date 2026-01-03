# Guide de Démarrage et Connexion - DAOS UASZ

## Problème : Impossible de se connecter

### Pré-requis pour que la connexion fonctionne

Pour que la connexion fonctionne, **TOUS** ces services doivent être démarrés dans l'ordre :

1. ✅ **MySQL** (Port 3306)
2. ✅ **Config Server** (Port 8888)
3. ✅ **Eureka Server** (Port 8761)
4. ✅ **API Gateway** (Port 8080)
5. ✅ **Auth Service** (Port 8081)
6. ✅ **Frontend** (Port 5173)

---

## Étape 1 : Vérifier MySQL et Créer les Utilisateurs

### 1.1 Démarrer MySQL

```bash
# Windows
net start MySQL80

# Vérifier que MySQL est démarré
mysql -u root -p -e "SELECT 1;"
```

### 1.2 Créer les utilisateurs de test

```bash
# Se connecter à MySQL
mysql -u root -p

# Sélectionner la base de données
USE daos_auth_db;

# Vérifier si la table utilisateur existe
SHOW TABLES;

# Si la table existe, insérer les utilisateurs
source C:/Users/Abdou/Documents/Gestion-des-enseignements_uasz/init-users.sql
```

**OU** exécutez directement :

```bash
mysql -u root -p < C:/Users/Abdou/Documents/Gestion-des-enseignements_uasz/init-users.sql
```

### 1.3 Vérifier que les utilisateurs sont créés

```sql
SELECT id, email, role, etat FROM utilisateur;
```

Vous devriez voir :
- chef.departement@uasz.sn
- coordinateur@uasz.sn
- responsable.master@uasz.sn
- enseignant@uasz.sn
- etudiant@uasz.sn

---

## Étape 2 : Démarrer les Services Backend

**IMPORTANT** : Démarrez les services dans CET ORDRE précis.

### 2.1 Config Server (Terminal 1)

```bash
cd C:\Users\Abdou\Documents\Gestion-des-enseignements_uasz\config-server
"C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.4\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run
```

**Attendez** ce message :
```
Tomcat started on port 8888
```

### 2.2 Eureka Server (Terminal 2 - NOUVEAU)

```bash
cd C:\Users\Abdou\Documents\Gestion-des-enseignements_uasz\eureka-server
"C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.4\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run
```

**Attendez** ce message :
```
Tomcat started on port 8761
```

**Vérifiez** : Ouvrez http://localhost:8761 - Vous devez voir le dashboard Eureka.

### 2.3 API Gateway (Terminal 3 - NOUVEAU)

```bash
cd C:\Users\Abdou\Documents\Gestion-des-enseignements_uasz\api-gateway
"C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.4\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run
```

**Attendez** ce message :
```
Tomcat started on port 8080
```

### 2.4 Auth Service (Terminal 4 - NOUVEAU)

```bash
cd C:\Users\Abdou\Documents\Gestion-des-enseignements_uasz\auth-service
"C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.4\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run
```

**Attendez** ces messages :
```
Tomcat started on port 8081
Registered with Eureka
```

**Vérifiez Eureka** : Rafraîchissez http://localhost:8761
- Vous devez voir **AUTH-SERVICE** dans la liste des services UP.

---

## Étape 3 : Démarrer le Frontend

### 3.1 Démarrer Vite (Terminal 5 - NOUVEAU)

```bash
cd C:\Users\Abdou\Documents\Gestion-des-enseignements_uasz\front-end-General
npm run dev
```

**Attendez** ce message :
```
Local: http://localhost:5173/
```

### 3.2 Ouvrir l'application

Ouvrez votre navigateur : http://localhost:5173

---

## Étape 4 : Tester la Connexion

### 4.1 Accéder à la page de login

http://localhost:5173/login

### 4.2 Utiliser ces identifiants

**Email** : `responsable.master@uasz.sn`
**Mot de passe** : `password123`

### 4.3 Ouvrir la Console du Navigateur

**Chrome/Edge** : F12 → Onglet "Console"
**Firefox** : F12 → Onglet "Console"

### 4.4 Cliquer sur "Se connecter"

---

## Diagnostic des Erreurs

### Erreur 1 : "Failed to fetch" ou "Network Error"

**Cause** : Les services backend ne sont pas démarrés.

**Solution** :
1. Vérifiez que les 4 services tournent (Config, Eureka, Gateway, Auth)
2. Vérifiez http://localhost:8761 - AUTH-SERVICE doit être UP
3. Testez l'endpoint directement :

```bash
curl -X POST http://localhost:8080/auth-service/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"responsable.master@uasz.sn\",\"password\":\"password123\"}"
```

### Erreur 2 : "404 Not Found"

**Cause** : API Gateway ne route pas correctement vers auth-service.

**Solution** :
1. Vérifiez que auth-service est enregistré dans Eureka : http://localhost:8761
2. Vérifiez la configuration de routing dans api-gateway

### Erreur 3 : "401 Unauthorized" ou "Identifiants invalides"

**Cause** : Les utilisateurs n'existent pas en base ou les mots de passe ne correspondent pas.

**Solution** :
```bash
# Réinitialiser tous les mots de passe
mysql -u root -p < C:/Users/Abdou/Documents/Gestion-des-enseignements_uasz/reset-all-passwords.sql

# Vérifier les utilisateurs
mysql -u root -p -e "USE daos_auth_db; SELECT email, role FROM utilisateur;"
```

### Erreur 4 : "CORS Error"

**Cause** : Le backend bloque les requêtes du frontend.

**Solution** : Vérifiez que AuthController.java a bien :
```java
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
```

### Erreur 5 : Connection Refused sur MySQL

**Cause** : MySQL n'est pas démarré.

**Solution** :
```bash
net start MySQL80
```

---

## Tests Manuels de l'API

### Test 1 : Vérifier l'API Gateway

```bash
curl http://localhost:8080/actuator/health
```

**Attendu** : `{"status":"UP"}`

### Test 2 : Vérifier Auth Service directement

```bash
curl http://localhost:8081/actuator/health
```

**Attendu** : `{"status":"UP"}`

### Test 3 : Tester le login via Gateway

```bash
curl -X POST http://localhost:8080/auth-service/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"responsable.master@uasz.sn\",\"password\":\"password123\"}"
```

**Attendu** : Un JSON avec `access_token`, `refresh_token`, et `user`.

### Test 4 : Tester le login directement

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"responsable.master@uasz.sn\",\"password\":\"password123\"}"
```

**Attendu** : Un JSON avec `access_token`, `refresh_token`, et `user`.

---

## Checklist Complète

Avant d'essayer de vous connecter, vérifiez :

- [ ] MySQL est démarré (port 3306)
- [ ] Les utilisateurs existent dans `daos_auth_db.utilisateur`
- [ ] Config Server est UP (port 8888)
- [ ] Eureka Server est UP (port 8761)
- [ ] API Gateway est UP (port 8080)
- [ ] Auth Service est UP (port 8081)
- [ ] Auth Service est enregistré dans Eureka (visible sur http://localhost:8761)
- [ ] Frontend Vite est démarré (port 5173)
- [ ] La console du navigateur ne montre pas d'erreurs CORS
- [ ] Le test curl fonctionne

---

## Identifiants de Test

| Email | Mot de passe | Rôle |
|-------|--------------|------|
| chef.departement@uasz.sn | password123 | CHEF_DE_DEPARTEMENT |
| coordinateur@uasz.sn | password123 | COORDONATEUR_DES_LICENCES |
| **responsable.master@uasz.sn** | **password123** | **RESPONSABLE_MASTER** |
| enseignant@uasz.sn | password123 | ENSEIGNANT |
| etudiant@uasz.sn | password123 | ETUDIANT |

---

## Script de Démarrage Automatique (Optionnel)

Créez un fichier `start-all.bat` :

```batch
@echo off
echo Demarrage de tous les services DAOS...

start "Config Server" cmd /k "cd config-server && mvn spring-boot:run"
timeout /t 30

start "Eureka Server" cmd /k "cd eureka-server && mvn spring-boot:run"
timeout /t 30

start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run"
timeout /t 20

start "Auth Service" cmd /k "cd auth-service && mvn spring-boot:run"
timeout /t 20

start "Frontend" cmd /k "cd front-end-General && npm run dev"

echo Tous les services sont en cours de demarrage !
echo Attendez environ 2 minutes puis ouvrez http://localhost:5173
pause
```

---

## Support

Si le problème persiste après avoir suivi ce guide :

1. **Copiez les logs d'erreur** de la console du navigateur (F12)
2. **Copiez les logs** des terminaux backend
3. **Vérifiez** les messages d'erreur spécifiques

Les erreurs les plus courantes sont :
- Services non démarrés
- Mauvais ordre de démarrage
- Utilisateurs non créés en base
- Ports déjà utilisés
