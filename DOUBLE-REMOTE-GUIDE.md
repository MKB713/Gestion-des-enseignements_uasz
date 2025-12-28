# Guide : Utiliser GitHub + GitLab Ensemble

## 🎯 Configuration Actuelle

Votre projet utilise maintenant **les deux plateformes** :
- **GitHub** : Pour le code, la collaboration, et GitHub Actions
- **GitLab** : Pour le CI/CD GitLab (pipeline `.gitlab-ci.yml`)

---

## 🔧 Configuration Initiale (À faire UNE FOIS)

### 1. Créer un projet sur GitLab

Allez sur https://gitlab.com et créez un nouveau projet :
- Nom : `gestion-enseignements-uasz`
- Visibilité : Private ou Public

### 2. Ajouter GitLab comme remote

```bash
cd C:\Users\Abdou\Documents\Gestion-des-enseignements_uasz

# Ajouter GitLab
git remote add gitlab https://gitlab.com/mkb713/gestion-enseignements-uasz.git

# Vérifier
git remote -v
```

Vous devriez voir :
```
origin  https://github.com/MKB713/Gestion-des-enseignements_uasz.git
gitlab  https://gitlab.com/mkb713/gestion-enseignements-uasz.git
```

### 3. Push initial vers GitLab

```bash
git push gitlab feature/GDEP2-1-Infrastructure-Technique
```

---

## 🚀 Utilisation Quotidienne

### Méthode 1 : Pousser vers les DEUX plateformes

```bash
# Après vos commits
git push origin feature/ma-branche    # → GitHub
git push gitlab feature/ma-branche    # → GitLab
```

### Méthode 2 : Script Automatique (Recommandé)

Utilisez le script `git-push-all.bat` :

```cmd
git-push-all.bat feature/ma-branche
```

Ce script pousse automatiquement vers GitHub ET GitLab !

---

## 📊 Pipelines CI/CD des Deux Plateformes

### GitHub Actions (Nouveau)

**Où le voir :**
```
https://github.com/MKB713/Gestion-des-enseignements_uasz
→ Actions (onglet)
```

**Se déclenche quand :**
- Vous push sur GitHub (`git push origin`)
- Vous créez une Pull Request

**Fichier de config :**
- `.github/workflows/ci-cd.yml`

**Avantages :**
- ✅ Intégré directement dans GitHub
- ✅ Interface visuelle claire
- ✅ 2000 minutes/mois gratuites
- ✅ Parfait pour la collaboration

---

### GitLab CI/CD (Existant)

**Où le voir :**
```
https://gitlab.com/mkb713/gestion-enseignements-uasz
→ CI/CD → Pipelines
```

**Se déclenche quand :**
- Vous push sur GitLab (`git push gitlab`)
- Vous créez une Merge Request

**Fichier de config :**
- `.gitlab-ci.yml`

**Avantages :**
- ✅ Runners puissants
- ✅ Excellente intégration Docker
- ✅ 400 minutes/mois gratuites
- ✅ CI/CD très mature

---

## 🎯 Workflow Recommandé

### Pour le Développement Quotidien

1. **Développez localement**
   ```bash
   git checkout -b feature/nouvelle-fonctionnalite
   # Codez...
   git add .
   git commit -m "feat: Add new feature"
   ```

2. **Poussez vers GitHub** (pour la collaboration)
   ```bash
   git push origin feature/nouvelle-fonctionnalite
   ```
   → GitHub Actions vérifie le code ✅

3. **Créez une Pull Request sur GitHub**
   - L'équipe review le code
   - GitHub Actions exécute les tests
   - Merge quand OK

4. **Après merge dans develop, poussez vers GitLab**
   ```bash
   git checkout develop
   git pull origin develop
   git push gitlab develop
   ```
   → GitLab CI/CD build et déploie ✅

---

## 🔄 Synchronisation

### Garder les deux plateformes à jour

```bash
# Après un merge sur GitHub
git checkout main
git pull origin main
git push gitlab main

# Idem pour develop
git checkout develop
git pull origin develop
git push gitlab develop
```

### Script Automatique

```cmd
# sync-remotes.bat
git push origin main
git push gitlab main
git push origin develop
git push gitlab develop
```

---

## 📋 Commandes Utiles

### Voir tous les remotes
```bash
git remote -v
```

### Supprimer un remote
```bash
git remote remove gitlab
```

### Renommer un remote
```bash
git remote rename gitlab gl
```

### Push TOUTES les branches vers GitLab
```bash
git push gitlab --all
```

### Push tous les tags
```bash
git push gitlab --tags
```

---

## 🎨 Les Deux Pipelines en Parallèle

### Exemple : Vous poussez du code

```
Vous faites :
  git push origin feature/test
  git push gitlab feature/test
          │
          ├──────────────────┬──────────────────┐
          ▼                  ▼                  ▼
    ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
    │   GITHUB    │   │   GITLAB    │   │  VOTRE PC   │
    │   ACTIONS   │   │    CI/CD    │   │             │
    └─────────────┘   └─────────────┘   └─────────────┘
          │                  │
          ▼                  ▼
    Build + Test       Build + Test
    GitHub Runners     GitLab Runners
          │                  │
          ▼                  ▼
    ✅ Passed           ✅ Passed
    Notifications      Notifications
```

**Résultat :** Double vérification = Double sécurité ! 🛡️

---

## ⚙️ Configuration Secrets

### GitHub Secrets (pour GitHub Actions)

Allez sur GitHub :
```
Settings → Secrets and variables → Actions → New repository secret
```

Ajoutez :
- `DOCKER_USERNAME` : votre nom Docker Hub
- `DOCKER_PASSWORD` : votre token Docker Hub
- `SONAR_TOKEN` : token SonarCloud (optionnel)

### GitLab CI/CD Variables

Allez sur GitLab :
```
Settings → CI/CD → Variables → Add Variable
```

Ajoutez :
- `DOCKER_USERNAME`
- `DOCKER_PASSWORD`
- `SONAR_TOKEN`
- `SONAR_HOST_URL`

---

## 🎯 Stratégie de Branches

### Recommandation

```
main (production)
  ↑
  └── develop (staging)
        ↑
        ├── feature/auth
        ├── feature/emploi-temps
        └── hotfix/bug-critique
```

**Workflow :**
1. Feature branches → PR/MR → develop
2. develop testé → PR/MR → main
3. main déployé en production

**Push strategy :**
- Features : GitHub seulement (pour review)
- develop/main : GitHub + GitLab (pour déploiement)

---

## 📊 Comparaison des Deux Pipelines

| Feature | GitHub Actions | GitLab CI/CD |
|---------|---------------|--------------|
| **Interface** | ⭐⭐⭐⭐⭐ Excellente | ⭐⭐⭐⭐ Très bonne |
| **Minutes gratuites** | 2000/mois | 400/mois |
| **Intégration Docker** | ⭐⭐⭐⭐ Bonne | ⭐⭐⭐⭐⭐ Excellente |
| **Marketplace** | Énorme | Moyen |
| **Configuration** | YAML simple | YAML puissant |
| **Artifacts** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Registry** | Ghcr.io | Container Registry |

**Conclusion :** Utilisez les deux ! 🎉
- GitHub pour collaboration
- GitLab pour CI/CD avancé

---

## 🚨 Attention !

### Évitez les Conflits

❌ **NE PAS faire :**
```bash
# Modifier directement sur GitLab web
# puis push depuis GitHub
→ Conflit! ⚠️
```

✅ **À FAIRE :**
```bash
# Toujours pull avant push
git pull origin main
git pull gitlab main
# Puis push
```

### Webhooks

Si vous voulez synchroniser automatiquement GitHub → GitLab :
- Configurez un webhook GitHub
- Ou utilisez GitLab CI/CD mirror

---

## 🎓 FAQ

**Q : Pourquoi deux plateformes ?**
A : GitHub pour la collaboration, GitLab pour le CI/CD puissant !

**Q : Lequel utiliser pour les Pull Requests ?**
A : GitHub (meilleure interface de review)

**Q : Lequel utiliser pour le déploiement ?**
A : GitLab (CI/CD plus mature pour Docker)

**Q : Ça coûte cher ?**
A : Non ! Les deux sont gratuits pour projets open source/personnels

**Q : C'est compliqué ?**
A : Non, utilisez le script `git-push-all.bat` !

---

## 📝 Checklist

Avant de push :
- [ ] Tests locaux passent
- [ ] Code formaté
- [ ] Commit message clair
- [ ] Pull des dernières modifications
- [ ] Push sur GitHub
- [ ] (Optionnel) Push sur GitLab

Après push :
- [ ] Vérifier GitHub Actions (onglet Actions)
- [ ] Vérifier GitLab CI/CD (si poussé sur GitLab)
- [ ] Lire les notifications
- [ ] Corriger si pipeline échoue

---

**Bon développement avec double CI/CD ! 🚀**
