# Configuration Repository - Config Server DAOS

Ce répertoire contient les configurations centralisées pour tous les microservices DAOS.

## Structure

```
config-repo/
├── application.properties                      # Configuration commune à tous les services
├── auth-service.properties                     # Configuration Auth Service
├── enseignant-service.properties               # Configuration Enseignant Service
├── maquette-service.properties                 # Configuration Maquette Service
├── choix-enseignement-service.properties       # Configuration Choix Enseignement Service
├── emploi-temps-service.properties             # Configuration Emploi Temps Service
├── deroulement-enseignement-service.properties # Configuration Déroulement Enseignement Service
├── api-gateway.properties                      # Configuration API Gateway
└── README.md                                   # Ce fichier
```

## Convention de nommage

Les fichiers de configuration suivent la convention Spring Cloud Config :

- `{application}.properties` : Configuration par défaut pour l'application
- `{application}-{profile}.properties` : Configuration spécifique à un profil
- `application.properties` : Configuration commune à toutes les applications

## Priorité de chargement

Spring Cloud Config charge les configurations dans cet ordre (du moins prioritaire au plus prioritaire) :

1. `application.properties` (config commune)
2. `{application}.properties` (config spécifique au service)
3. `application-{profile}.properties` (config commune avec profil)
4. `{application}-{profile}.properties` (config spécifique au service avec profil)

Les propriétés des fichiers plus prioritaires écrasent celles des fichiers moins prioritaires.

## Accès via HTTP

Les configurations peuvent être récupérées via HTTP :

**Format :**
```
http://localhost:8888/{application}/{profile}[/{label}]
```

**Exemples :**
```bash
# Configuration par défaut d'auth-service
curl http://localhost:8888/auth-service/default

# Configuration de enseignant-service
curl http://localhost:8888/enseignant-service/default

# Configuration de l'api-gateway
curl http://localhost:8888/api-gateway/default

# Toutes les configurations en JSON
curl http://localhost:8888/auth-service/default | jq
```

## Test du Config Server

### 1. Vérifier que Config Server est démarré

```bash
curl http://localhost:8888/actuator/health
```

**Résultat attendu :**
```json
{
  "status": "UP"
}
```

### 2. Récupérer une configuration

```bash
curl http://localhost:8888/auth-service/default
```

**Résultat attendu :** JSON avec toutes les propriétés de auth-service + application.properties

### 3. Vérifier les propriétés disponibles

```bash
curl http://localhost:8888/actuator/configprops
```

## Migration des services vers Config Server (Optionnel)

Pour qu'un microservice utilise Config Server, vous devez :

### Option 1 : Bootstrap Properties (Spring Boot 2.3 et antérieur)

Créer `bootstrap.properties` dans le service :

```properties
spring.application.name=auth-service
spring.cloud.config.uri=http://localhost:8888
spring.cloud.config.fail-fast=true
```

### Option 2 : Application Properties (Spring Boot 2.4+)

Dans `application.properties` :

```properties
spring.application.name=auth-service
spring.config.import=optional:configserver:http://localhost:8888
```

### Ajouter la dépendance

Dans le `pom.xml` du service :

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

**Note :** Pour l'instant, les services utilisent leur propre `application.properties`. La migration est optionnelle.

## Utilisation actuelle

**Mode hybride :**
- Config Server est opérationnel et expose les configurations via HTTP
- Les microservices utilisent encore leurs propres `application.properties` locaux
- Vous pouvez consulter et tester les configurations via Config Server sans impacter les services

**Avantages :**
- Config Server valide et fonctionnel (critères d'acceptation remplis)
- Pas de risque de casser les services existants
- Migration possible plus tard si nécessaire

## Bonnes pratiques

1. ✅ Ne jamais commiter de mots de passe en clair (utiliser des variables d'environnement)
2. ✅ Garder la configuration commune dans `application.properties`
3. ✅ Utiliser des profils pour différencier les environnements
4. ✅ Documenter toutes les propriétés personnalisées
5. ⚠️ En production, considérer un backend Git au lieu de native pour versionner les configs

## Ajout d'un nouveau service

Pour ajouter la configuration d'un nouveau service :

1. Créer `{service-name}.properties` dans config-repo
2. Redémarrer Config Server (ou attendre le rechargement automatique)
3. Tester : `curl http://localhost:8888/{service-name}/default`

## Sécurité

**Développement :**
- Mot de passe vide acceptable
- Configuration en clair

**Production :**
- Utiliser des variables d'environnement : `${DB_PASSWORD:default}`
- Chiffrer les données sensibles avec `{cipher}`
- Protéger l'accès au Config Server avec Spring Security

---

**Date de création :** 2025-12-24
**Version :** 1.0
**Auteur :** Équipe DAOS - UASZ
