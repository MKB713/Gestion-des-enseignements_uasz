# Corrections Appliquées au Deroulement Enseignement Service

## Problèmes Identifiés et Corrigés

### 1. Port MySQL Incorrect ✅
**Problème** : Le service tentait de se connecter à MySQL sur le port 3311, mais MySQL tourne sur le port 3306.

**Fichier** : `src/main/resources/application.properties` (ligne 10)

**Avant** :
```properties
spring.datasource.url=jdbc:mysql://localhost:3311/daos_deroulement_enseignement_db?createDatabaseIfNotExist=true
```

**Après** :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/daos_deroulement_enseignement_db?createDatabaseIfNotExist=true
```

### 2. Configuration Dupliquée ✅
**Problème** : Ligne de configuration vide et dupliquée causant des conflits.

**Fichier** : `src/main/resources/application.properties` (ligne 34)

**Supprimé** :
```properties
spring.config.import=optional:configserver:
```

Cette ligne était en conflit avec la ligne 6 qui contient déjà la bonne configuration.

## Comment Démarrer le Service

### Option 1 : Script Automatique (Recommandé)
```bash
cd deroulement-enseignement-service
start-service.bat
```

Le script vérifie automatiquement :
- ✅ Connexion MySQL
- ✅ Disponibilité d'Eureka
- ✅ Démarre le service

### Option 2 : Manuellement
```bash
cd deroulement-enseignement-service
mvn clean install
mvn spring-boot:run
```

### Option 3 : Avec votre IDE
1. Ouvrez le projet dans IntelliJ IDEA ou Eclipse
2. Exécutez `DeroulementEnseignementServiceApplication.java`

## Vérifications Avant le Démarrage

### 1. MySQL doit être démarré
Vérifiez que MySQL tourne :
```bash
mysql -h localhost -P 3306 -uroot -e "SELECT 1;"
```

### 2. Services Prérequis (Optionnel mais Recommandé)
Pour une intégration complète :
- ✅ **Config Server** (port 8888)
- ✅ **Eureka Server** (port 8761)
- ✅ **API Gateway** (port 8080)

### 3. Port 8086 Disponible
Vérifiez qu'aucun autre service n'utilise le port 8086 :
```bash
netstat -an | findstr "8086"
```

## Après le Démarrage

### 1. Vérifier le Statut
Le service devrait afficher :
```
Started DeroulementEnseignementServiceApplication in X seconds
```

### 2. Vérifier Eureka
Accédez à http://localhost:8761

Le service `DEROULEMENT-ENSEIGNEMENT-SERVICE` doit apparaître comme **UP**.

### 3. Vérifier la Base de Données
La base de données `daos_deroulement_enseignement_db` est créée automatiquement.

Vérifiez :
```bash
mysql -uroot -e "SHOW DATABASES LIKE 'daos_deroulement%';"
```

Vous devriez voir :
```
+---------------------------------------+
| Database (daos_deroulement%)          |
+---------------------------------------+
| daos_deroulement_enseignement_db      |
+---------------------------------------+
```

### 4. Vérifier les Tables
```bash
mysql -uroot daos_deroulement_enseignement_db -e "SHOW TABLES;"
```

Tables attendues :
- `classe`
- `etudiant`
- `historique_modification_note`
- `note_cahier_texte`

### 5. Tester les Endpoints

#### Swagger UI
Accédez à : http://localhost:8086/swagger-ui.html

#### API Directe
```bash
# Via API Gateway
curl http://localhost:8080/deroulement-enseignement-service/api/cahier-texte

# Directement
curl http://localhost:8086/api/cahier-texte
```

## Intégration Frontend

Le frontend est déjà configuré pour se connecter à ce service via l'API Gateway :

**Endpoints disponibles** :
- `GET /api/cahier-texte` - Liste
- `GET /api/cahier-texte/{id}` - Détails
- `GET /api/cahier-texte/classe/{classeId}` - Par classe
- `POST /api/cahier-texte` - Créer
- `PUT /api/cahier-texte/{id}` - Modifier
- `DELETE /api/cahier-texte/{id}` - Supprimer

**Configuration Frontend** :
```javascript
// front-end-General/src/config/api.js
DEROULEMENT: {
  CAHIER_TEXTE: 'http://localhost:8080/deroulement-enseignement-service/api/cahier-texte',
  ...
}
```

## Dépannage

### Erreur : "Communications link failure"
**Cause** : MySQL n'est pas démarré ou inaccessible.

**Solution** :
```bash
# Démarrer MySQL (selon votre installation)
net start MySQL80  # Windows
# ou
sudo systemctl start mysql  # Linux
```

### Erreur : "Port 8086 already in use"
**Cause** : Un autre processus utilise le port 8086.

**Solution** :
```bash
# Trouver le processus
netstat -ano | findstr "8086"

# Tuer le processus (remplacer PID)
taskkill /PID <PID> /F
```

### Erreur : "Unable to register with Eureka"
**Cause** : Eureka Server n'est pas démarré.

**Solution** : Le service peut fonctionner sans Eureka, mais pour l'enregistrement :
```bash
cd eureka-server
mvn spring-boot:run
```

### Le service démarre mais crashe ensuite
**Vérifiez les logs** pour voir l'erreur exacte :
- Erreur de mapping d'entité
- Problème de dépendance
- Erreur de configuration

**Logs** : Regardez la console pour les stack traces.

## Configuration Spring Security

Le service utilise Spring Security avec une configuration permissive pour le développement :

```java
// SecurityConfig.java
.authorizeHttpRequests(authorize -> authorize
    .anyRequest().permitAll()  // Tous les endpoints sont accessibles
)
.csrf(csrf -> csrf.disable())  // CSRF désactivé
```

**Note** : En production, ajoutez une vraie sécurité !

## Prochaines Étapes

1. ✅ Service démarré avec succès
2. ⏳ Ajouter des données de test
3. ⏳ Tester via le frontend
4. ⏳ Implémenter la logique métier complète
5. ⏳ Ajouter la sécurité en production

---

**Le service devrait maintenant démarrer correctement !** 🎉

Si vous rencontrez encore des problèmes, vérifiez :
1. Les logs de la console
2. Les logs MySQL
3. La connexion réseau (localhost)
