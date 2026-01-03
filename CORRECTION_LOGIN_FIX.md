# Correction du Problème de Connexion - DAOS UASZ

## Date
2026-01-03

## Problème Identifié

Impossible de se connecter sur la page d'authentification avec l'erreur **"Identifiants invalides"**.

### Diagnostic Effectué

1. ✅ **Services Backend** : Tous démarrés correctement
   - MySQL (3306) : UP
   - Config Server (8888) : UP
   - Eureka Server (8761) : UP
   - API Gateway (8080) : UP
   - Auth Service (8081) : UP et enregistré dans Eureka

2. ✅ **Utilisateurs** : Présents dans la base de données `daos_auth_db`
   - Email: responsable.master@uasz.sn
   - Mot de passe: password123 (hash BCrypt valide)

3. ✅ **Endpoint Login** : Fonctionne (`POST /api/auth/login`)

### Cause Racine

**Le AuthService ne renvoyait PAS les informations utilisateur dans la réponse de login.**

Le frontend s'attend à recevoir :
```json
{
  "access_token": "...",
  "refresh_token": "...",
  "expires_in": 86400,
  "user": {
    "id": 17,
    "email": "responsable.master@uasz.sn",
    "nom": "Ndiaye",
    "prenom": "Ousmane",
    "role": "RESPONSABLE_MASTER"
  }
}
```

Mais AuthService renvoyait seulement :
```json
{
  "access_token": "...",
  "refresh_token": "...",
  "expires_in": 86400
}
```

**Résultat** : Le frontend ne pouvait pas extraire `data.user.role` pour naviguer vers le bon dashboard.

---

## Solution Appliquée

### Fichier Modifié
`auth-service/src/main/java/com/uasz/daos/auth/services/AuthService.java`

### Modifications

#### 1. Méthode `authenticate()` (ligne 125-142)

**Avant** :
```java
return AuthenticationResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .expiresIn(86400L)
        .build();
```

**Après** :
```java
// Créer les informations utilisateur
AuthenticationResponse.UserInfo userInfo = AuthenticationResponse.UserInfo.builder()
        .id(utilisateur.getId())
        .email(utilisateur.getEmail())
        .nom(utilisateur.getNom())
        .prenom(utilisateur.getPrenom())
        .role(utilisateur.getRole())
        .build();

return AuthenticationResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .expiresIn(86400L)
        .user(userInfo)  // ✅ AJOUTÉ
        .build();
```

#### 2. Méthode `register()` (ligne 82-99)

Même correction appliquée pour cohérence.

---

## Instructions pour Appliquer la Correction

### Étape 1 : Le code a déjà été corrigé ✅

Le fichier `AuthService.java` a été modifié et recompilé avec succès.

### Étape 2 : Redémarrer Auth Service

**IMPORTANT** : Vous devez redémarrer auth-service pour que les changements prennent effet.

#### Option A : Redémarrer depuis votre terminal actuel

1. **Arrêter** auth-service (Ctrl+C dans le terminal où il tourne)

2. **Redémarrer** :
```bash
cd C:\Users\Abdou\Documents\Gestion-des-enseignements_uasz\auth-service
"C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.4\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run
```

#### Option B : Utiliser IntelliJ IDEA

1. Arrêter auth-service (bouton Stop)
2. Clic droit sur `AuthServiceApplication`
3. "Run AuthServiceApplication"

### Étape 3 : Vérifier que auth-service a redémarré

Attendez ce message dans les logs :
```
Tomcat started on port 8081
```

Vérifiez Eureka : http://localhost:8761
- AUTH-SERVICE doit être UP

### Étape 4 : Tester la connexion

#### 4.1 Test avec curl

```bash
curl -X POST http://localhost:8080/auth-service/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"responsable.master@uasz.sn\",\"password\":\"password123\"}"
```

**Réponse attendue** :
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiJ9...",
  "token_type": "Bearer",
  "expires_in": 86400,
  "user": {
    "id": 17,
    "email": "responsable.master@uasz.sn",
    "nom": "Ndiaye",
    "prenom": "Ousmane",
    "role": "RESPONSABLE_MASTER"
  }
}
```

#### 4.2 Test via le Frontend

1. **Démarrer le frontend** (si pas déjà fait) :
```bash
cd C:\Users\Abdou\Documents\Gestion-des-enseignements_uasz\front-end-General
npm run dev
```

2. **Ouvrir** : http://localhost:5173/login

3. **Se connecter avec** :
   - Email: `responsable.master@uasz.sn`
   - Mot de passe: `password123`

4. **Résultat attendu** :
   - ✅ Connexion réussie
   - ✅ Redirection vers `/master/dashboard`
   - ✅ Affichage du dashboard Master

---

## Vérification Post-Correction

### Checklist

- [ ] Auth Service a été redémarré
- [ ] Le test curl renvoie les informations `user`
- [ ] Le frontend permet de se connecter
- [ ] La redirection vers le bon dashboard fonctionne
- [ ] Le sidebar affiche le nom de l'utilisateur

### Si le problème persiste

1. **Vérifier les logs** du terminal auth-service
2. **Ouvrir la console** du navigateur (F12)
3. **Vérifier** que la réponse contient bien `user`:
   - Onglet "Network" → Cliquer sur "login" → Onglet "Response"

---

## Identifiants de Test

| Email | Mot de passe | Rôle |
|-------|--------------|------|
| chef.departement@uasz.sn | password123 | CHEF_DE_DEPARTEMENT |
| coordinateur@uasz.sn | password123 | COORDONATEUR_DES_LICENCES |
| **responsable.master@uasz.sn** | **password123** | **RESPONSABLE_MASTER** ⭐ |
| enseignant@uasz.sn | password123 | ENSEIGNANT |
| etudiant@uasz.sn | password123 | ETUDIANT |

---

## Résumé

✅ **Problème** : AuthService ne renvoyait pas les infos utilisateur
✅ **Correction** : Ajout du champ `user` dans AuthenticationResponse
✅ **Build** : Réussi
⏳ **Action requise** : Redémarrer auth-service

**Après redémarrage, la connexion devrait fonctionner parfaitement !**
