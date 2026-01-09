# Guide de Démarrage - Maquette Service

## Problèmes Courants et Solutions

### 1. Vérifier que MySQL est démarré

Le service nécessite MySQL. Vérifiez que MySQL est démarré sur le port 3306.

**Test** : Ouvrez MySQL Workbench ou phpMyAdmin et connectez-vous à `localhost:3306`

### 2. Vérifier que le port 8083 n'est pas utilisé

Le maquette-service utilise le port 8083.

**Windows - Vérifier le port** :
```cmd
netstat -ano | findstr :8083
```

**Si le port est occupé**, tuez le processus :
```cmd
taskkill /PID <PID_NUMBER> /F
```

### 3. Compiler le service

Depuis VS Code ou votre terminal :

**Avec Maven installé** :
```bash
cd maquette-service
mvn clean install -DskipTests
```

**Avec Java installé** (via IDE) :
- Ouvrez le projet dans IntelliJ IDEA ou Eclipse
- Clic droit sur le projet → Maven → Reload Project
- Puis Build → Rebuild Project

### 4. Démarrer le service

**Via IDE (Recommandé)** :
- Ouvrez `MaquetteServiceApplication.java`
- Clic droit → Run 'MaquetteServiceApplication'

**Via ligne de commande** :
```bash
cd maquette-service
java -jar target/maquette-service-1.0.0.jar
```

### 5. Vérifier que le service a démarré

Le service devrait afficher dans les logs :
```
Tomcat started on port(s): 8083 (http)
Started MaquetteServiceApplication in X.XXX seconds
```

**Test** : Ouvrez http://localhost:8083/api/maquette/ues dans votre navigateur
- Vous devriez voir une liste JSON des UE

### 6. Erreurs Communes

#### Erreur : "Failed to configure a DataSource"
- **Cause** : MySQL n'est pas démarré
- **Solution** : Démarrez MySQL

#### Erreur : "Port 8083 already in use"
- **Cause** : Une ancienne instance du service tourne encore
- **Solution** : Tuez le processus avec `taskkill` (voir point 2)

#### Erreur : "Cannot resolve symbol" ou erreurs de compilation
- **Cause** : Dépendances Maven non téléchargées
- **Solution** : Exécutez `mvn clean install`

#### Erreur : "Unable to connect to Eureka"
- **Cause** : Eureka Server n'est pas démarré
- **Solution** : C'est normal, le service démarrera quand même (juste des warnings)

### 7. Vérification après démarrage

Une fois démarré, testez ces endpoints :

- http://localhost:8083/api/maquette/ues → Liste des UE
- http://localhost:8083/api/maquette/ecs → Liste des EC
- http://localhost:8083/api/maquette/modules → Liste des Modules
- http://localhost:8083/swagger-ui.html → Documentation Swagger

## Démarrage Rapide (Ordre Complet)

1. ✅ Démarrer MySQL
2. ✅ Vérifier que le port 8083 est libre
3. ✅ Compiler : `mvn clean install -DskipTests`
4. ✅ Démarrer : Run `MaquetteServiceApplication.java`
5. ✅ Tester : http://localhost:8083/api/maquette/ues

## Logs à Vérifier

Si le service ne démarre pas, lisez attentivement les logs dans la console. Cherchez :
- `ERROR` : Erreurs critiques
- `Exception` : Exceptions Java
- `Caused by` : Cause racine de l'erreur
