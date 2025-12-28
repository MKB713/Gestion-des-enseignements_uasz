# Guide Docker Desktop - Visualisation de l'Application

## 🎯 Comment voir votre application dans Docker Desktop

### Étape 1 : Ouvrir Docker Desktop

1. Cliquez sur l'icône **Docker Desktop** dans la barre des tâches Windows
2. Attendez que Docker Desktop s'ouvre

### Étape 2 : Naviguer vers les Containers

Dans Docker Desktop, vous verrez sur le côté gauche :
- 🏠 **Home**
- 📦 **Containers** ← Cliquez ici !
- 🖼️ **Images**
- 📚 **Volumes**
- 🔧 **Dev Environments**

### Étape 3 : Voir vos services

Une fois dans **Containers**, vous devriez voir un groupe nommé :

```
📁 gestion-des-enseignements_uasz
    ├── 🟢 daos-mysql (Running)
    ├── 🟢 daos-redis (Running)
    ├── 🟢 eureka-server (Running)
    ├── 🟢 config-server (Running)
    ├── 🟢 api-gateway (Running)
    ├── 🟢 auth-service (Running)
    ├── 🟢 enseignant-service (Running)
    ├── 🟢 maquette-service (Running)
    ├── 🟢 choix-enseignement-service (Running)
    ├── 🟢 emploi-temps-service (Running)
    ├── 🟢 deroulement-enseignement-service (Running)
    └── 🟢 front-end-main (Running)
```

### Légende des états :
- 🟢 **Vert (Running)** = Service fonctionne correctement
- 🟡 **Jaune (Starting)** = Service en cours de démarrage
- 🔴 **Rouge (Exited)** = Service arrêté ou en erreur
- 🔵 **Bleu (Restarting)** = Service en cours de redémarrage

---

## 🚀 Démarrage depuis Docker Desktop (Interface Graphique)

### Méthode 1 : Démarrage via l'interface

1. Ouvrez **Docker Desktop**
2. Allez dans **Containers**
3. Cliquez sur **▶️ Start** sur le groupe `gestion-des-enseignements_uasz`
4. Attendez 2-3 minutes que tous les services démarrent

### Méthode 2 : Démarrage via CMD

**Option A - Avec le script :**
```cmd
cd C:\Users\Abdou\Documents\Gestion-des-enseignements_uasz
docker-start.bat start
```

**Option B - Avec docker-compose :**
```cmd
cd C:\Users\Abdou\Documents\Gestion-des-enseignements_uasz
docker-compose up -d
```

---

## 📊 Informations détaillées pour chaque service

Cliquez sur un conteneur dans Docker Desktop pour voir :

### Onglet **Logs** 📝
- Les logs en temps réel du service
- Utile pour déboguer
- Recherche avec Ctrl+F

### Onglet **Inspect** 🔍
- Configuration détaillée
- Variables d'environnement
- Réseaux connectés
- Volumes montés

### Onglet **Stats** 📈
- Utilisation CPU
- Utilisation Mémoire
- Trafic réseau
- I/O disque

### Onglet **Exec** ⚡
- Exécuter des commandes dans le conteneur
- Utile pour le débogage avancé

---

## 🌐 Accès aux services depuis votre navigateur

Une fois tous les services **🟢 Running** :

### Services Web Accessibles

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend React** | http://localhost:3000 | Interface utilisateur principale |
| **API Gateway** | http://localhost:8080 | Point d'entrée API |
| **Eureka Dashboard** | http://localhost:8761 | Registry des services |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | Documentation API interactive |
| **Config Server** | http://localhost:8888 | Serveur de configuration |

### Services avec Monitoring (si activés)

| Service | URL | Description |
|---------|-----|-------------|
| **Grafana** | http://localhost:3001 | Tableaux de bord (admin/admin) |
| **Prometheus** | http://localhost:9090 | Métriques |
| **Jaeger** | http://localhost:16686 | Tracing distribué |

---

## 🔄 Ordre de démarrage automatique

Docker Compose gère automatiquement l'ordre grâce aux **healthchecks** :

```
1️⃣ MySQL + Redis (30 secondes)
        ↓
2️⃣ Eureka Server (1 minute)
        ↓
3️⃣ Config Server (30 secondes)
        ↓
4️⃣ API Gateway + Microservices (1-2 minutes)
        ↓
5️⃣ Frontend (30 secondes)
```

**Temps total : 3-4 minutes** ⏱️

---

## ✅ Vérification que tout fonctionne

### Test 1 : Vérifier Eureka (Service Registry)

1. Ouvrez http://localhost:8761
2. Vous devriez voir tous les services enregistrés :
   - API-GATEWAY
   - AUTH-SERVICE
   - ENSEIGNANT-SERVICE
   - MAQUETTE-SERVICE
   - CHOIX-ENSEIGNEMENT-SERVICE
   - EMPLOI-TEMPS-SERVICE
   - DEROULEMENT-ENSEIGNEMENT-SERVICE

✅ Si vous voyez tous ces services, **c'est bon !**

### Test 2 : Vérifier l'API Gateway

Ouvrez http://localhost:8080/actuator/health

Résultat attendu :
```json
{
  "status": "UP"
}
```

### Test 3 : Vérifier le Frontend

Ouvrez http://localhost:3000

✅ La page de connexion doit s'afficher

### Test 4 : Vérifier MySQL

Dans Docker Desktop :
1. Cliquez sur **daos-mysql**
2. Allez dans **Exec**
3. Tapez : `mysql -u root -p`
4. Entrez le mot de passe (celui de votre .env)

---

## 🛠️ Actions courantes dans Docker Desktop

### Redémarrer un service

1. Cliquez sur le conteneur (ex: auth-service)
2. Cliquez sur **⏹️ Stop**
3. Attendez qu'il s'arrête
4. Cliquez sur **▶️ Start**

### Voir les logs d'un service

1. Cliquez sur le conteneur
2. Allez dans l'onglet **Logs**
3. Utilisez la barre de recherche pour filtrer

### Supprimer tous les services

1. Dans **Containers**, cliquez sur le groupe `gestion-des-enseignements_uasz`
2. Cliquez sur **🗑️ Delete**
3. Confirmez

**Puis pour redémarrer :**
```cmd
docker-compose up -d
```

---

## 📸 Captures d'écran attendues

### Dans Docker Desktop - Containers

Vous devriez voir quelque chose comme :

```
📁 gestion-des-enseignements_uasz (12 containers)
    Running: 12/12

    Name                              Status    CPU    Memory    Port
    ─────────────────────────────────────────────────────────────────
    daos-mysql                        Running   5%     120MB     3306
    daos-redis                        Running   2%     15MB      6379
    eureka-server                     Running   3%     350MB     8761
    config-server                     Running   2%     300MB     8888
    api-gateway                       Running   4%     380MB     8080
    auth-service                      Running   3%     320MB     8081
    enseignant-service                Running   2%     310MB     8082
    maquette-service                  Running   2%     310MB     8083
    choix-enseignement-service        Running   2%     300MB     8084
    emploi-temps-service              Running   3%     330MB     8085
    deroulement-enseignement-service  Running   2%     310MB     8086
    front-end-main                    Running   1%     25MB      3000
```

---

## 🎨 Dans votre navigateur

### Eureka Dashboard (http://localhost:8761)

Vous devriez voir :

```
Application         AMIs        Availability Zones    Status
─────────────────────────────────────────────────────────────
API-GATEWAY         n/a (1)     (1)                  UP
AUTH-SERVICE        n/a (1)     (1)                  UP
ENSEIGNANT-SERVICE  n/a (1)     (1)                  UP
MAQUETTE-SERVICE    n/a (1)     (1)                  UP
...
```

### Frontend (http://localhost:3000)

Une belle page de connexion avec :
- Logo UASZ
- Formulaire de connexion
- Bouton "Se connecter"

---

## ⚡ Commandes rapides depuis CMD

### Voir le statut
```cmd
cd C:\Users\Abdou\Documents\Gestion-des-enseignements_uasz
docker-compose ps
```

### Voir les logs (tous les services)
```cmd
docker-compose logs -f
```

### Voir les logs d'un service spécifique
```cmd
docker-compose logs -f auth-service
docker-compose logs -f eureka-server
```

### Arrêter tout
```cmd
docker-compose down
```

### Redémarrer tout
```cmd
docker-compose restart
```

### Rebuild et redémarrer (après modification du code)
```cmd
docker-compose up --build -d
```

---

## 🆘 Problèmes courants

### Problème : Service en 🔴 Rouge (Exited)

**Solution :**
1. Cliquez sur le service
2. Allez dans **Logs**
3. Cherchez les erreurs (mots clés : ERROR, FATAL, Exception)
4. Redémarrez : `docker-compose restart nom-du-service`

### Problème : Aucun service visible

**Solution :**
1. Vérifiez que Docker Desktop est démarré
2. Exécutez : `docker-compose up -d`
3. Attendez 3-4 minutes

### Problème : Port déjà utilisé

**Erreur :** `Bind for 0.0.0.0:8080 failed: port is already allocated`

**Solution :**
1. Éditez `.env`
2. Changez le port :
   ```
   API_GATEWAY_PORT=8081
   ```
3. Redémarrez : `docker-compose up -d`

### Problème : Service bloqué en 🟡 Jaune (Starting)

**Solution :**
1. Attendez 2 minutes (les healthchecks prennent du temps)
2. Si toujours jaune, vérifiez les logs
3. Redémarrez : `docker-compose restart nom-du-service`

---

## 📱 Accès depuis un autre appareil

Si vous voulez accéder depuis votre téléphone ou une autre machine sur le même réseau :

1. Trouvez votre IP locale :
   ```cmd
   ipconfig
   ```
   Cherchez "IPv4 Address" (ex: 192.168.1.10)

2. Sur l'autre appareil, utilisez :
   ```
   http://192.168.1.10:3000  (Frontend)
   http://192.168.1.10:8080  (API Gateway)
   http://192.168.1.10:8761  (Eureka)
   ```

**Note :** Votre pare-feu Windows doit autoriser les connexions entrantes sur ces ports.

---

## 🎓 Comptes de test

Une fois sur http://localhost:3000, connectez-vous avec :

| Rôle | Email | Mot de passe |
|------|-------|--------------|
| Admin | admin@uasz.sn | admin123 |
| Enseignant | enseignant@uasz.sn | ens123 |
| Étudiant | etudiant@uasz.sn | etu123 |

---

## 💡 Conseils

1. **Toujours vérifier Eureka** (http://localhost:8761) en premier
   - Si les services ne sont pas enregistrés, ils ne peuvent pas communiquer

2. **Utilisez les logs** pour déboguer
   - Dans Docker Desktop : Cliquez sur le service → Logs
   - Ou en CMD : `docker-compose logs -f nom-du-service`

3. **Soyez patient** au premier démarrage
   - Cela prend 3-4 minutes
   - Les services démarrent dans un ordre spécifique

4. **Surveillez la mémoire**
   - 12 services = ~4 Go de RAM
   - Assurez-vous d'avoir assez de ressources allouées à Docker

---

**Bon déploiement ! 🚀**

Si vous voyez tous les services en 🟢 vert dans Docker Desktop, c'est gagné !
