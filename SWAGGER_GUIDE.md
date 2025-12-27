# Guide Swagger/OpenAPI - Documentation API DAOS

## Vue d'ensemble

Swagger/OpenAPI est intégré dans tous les microservices DAOS pour fournir :
- **Documentation API automatique** et interactive
- **Interface de test** pour toutes les APIs
- **Contrat d'API standardisé** (OpenAPI 3.0)
- **Point d'accès centralisé** via l'API Gateway

## Architecture Swagger

```
┌────────────────────────────────────────────────────────────────┐
│                    API Gateway (:8080)                          │
│                 Swagger UI Agrégé (Central)                     │
│              http://localhost:8080/swagger-ui.html              │
└────────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              │               │               │
         ┌────▼────┐    ┌─────▼──────┐  ┌────▼────┐
         │  Auth   │    │ Enseignant │  │Maquette │
         │ :8081   │    │   :8082    │  │ :8083   │
         │Swagger  │    │  Swagger   │  │ Swagger │
         └─────────┘    └────────────┘  └─────────┘
              │               │               │
        ┌─────▼──────┐  ┌────▼────┐    ┌─────▼─────┐
        │   Choix    │  │ Emploi  │    │Deroulement│
        │   :8084    │  │ :8085   │    │  :8086    │
        │  Swagger   │  │ Swagger │    │  Swagger  │
        └────────────┘  └─────────┘    └───────────┘
```

## URLs d'accès

### Interface centralisée (Recommandé)

**Swagger UI Agrégé** (tous les services en un seul endroit)
```
http://localhost:8080/swagger-ui.html
```

### Interfaces individuelles par service

| Service | Swagger UI | API Docs JSON |
|---------|------------|---------------|
| **Auth Service** | http://localhost:8081/swagger-ui.html | http://localhost:8081/api-docs |
| **Enseignant Service** | http://localhost:8082/swagger-ui.html | http://localhost:8082/api-docs |
| **Maquette Service** | http://localhost:8083/swagger-ui.html | http://localhost:8083/api-docs |
| **Choix Enseignement** | http://localhost:8084/swagger-ui.html | http://localhost:8084/api-docs |
| **Emploi Temps** | http://localhost:8085/swagger-ui.html | http://localhost:8085/api-docs |
| **Déroulement** | http://localhost:8086/swagger-ui.html | http://localhost:8086/api-docs |

### Via API Gateway

| Service | Swagger UI via Gateway | API Docs via Gateway |
|---------|------------------------|----------------------|
| **Auth Service** | http://localhost:8080/api/auth/swagger-ui.html | http://localhost:8080/api/auth/api-docs |
| **Enseignant Service** | http://localhost:8080/api/enseignants/swagger-ui.html | http://localhost:8080/api/enseignants/api-docs |
| **Maquette Service** | http://localhost:8080/api/maquettes/swagger-ui.html | http://localhost:8080/api/maquettes/api-docs |
| **Choix Enseignement** | http://localhost:8080/api/choix-enseignements/swagger-ui.html | http://localhost:8080/api/choix-enseignements/api-docs |
| **Emploi Temps** | http://localhost:8080/api/emploi-temps/swagger-ui.html | http://localhost:8080/api/emploi-temps/api-docs |
| **Déroulement** | http://localhost:8080/api/deroulement-enseignements/swagger-ui.html | http://localhost:8080/api/deroulement-enseignements/api-docs |

## Configuration par service

### Auth Service

**Dépendance Maven** (`auth-service/pom.xml`)
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

**Configuration** (`auth-service/src/main/resources/application.properties`)
```properties
# Swagger/OpenAPI Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.packages-to-scan=com.uasz.daos.auth.controller
```

**Classe OpenAPI** (`auth-service/src/main/java/com/uasz/daos/auth/config/OpenApiConfig.java`)
```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI authServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .description("API de gestion de l'authentification et des utilisateurs pour DAOS (UASZ)")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Équipe DAOS")
                                .email("daos@uasz.edu.sn")
                                .url("https://uasz.edu.sn"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Serveur de développement (Direct)"),
                        new Server()
                                .url("http://localhost:8080/api/auth")
                                .description("Serveur de développement (via API Gateway)")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token pour l'authentification")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
```

**Sécurité** (`auth-service/src/main/java/com/uasz/daos/auth/config/SecurityConfig.java`)
```java
.requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
```

### Autres services (Enseignant, Maquette, Choix, Emploi Temps, Déroulement)

Même structure que Auth Service :
- ✅ Dépendance `springdoc-openapi-starter-webmvc-ui`
- ✅ Configuration dans `application.properties`
- ✅ Classe `OpenApiConfig` dans le package `config`
- ⚠️ Pas de sécurité configurée (à ajouter si Spring Security est activé)

### API Gateway

**Dépendance Maven** (utilise WebFlux, pas WebMVC)
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

**Configuration agrégée** (`api-gateway/src/main/resources/application.properties`)
```properties
# Swagger/OpenAPI Aggregation Configuration
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.urls[0].name=Auth Service
springdoc.swagger-ui.urls[0].url=/api/auth/api-docs
springdoc.swagger-ui.urls[1].name=Enseignant Service
springdoc.swagger-ui.urls[1].url=/api/enseignants/api-docs
springdoc.swagger-ui.urls[2].name=Maquette Service
springdoc.swagger-ui.urls[2].url=/api/maquettes/api-docs
springdoc.swagger-ui.urls[3].name=Choix Enseignement Service
springdoc.swagger-ui.urls[3].url=/api/choix-enseignements/api-docs
springdoc.swagger-ui.urls[4].name=Emploi Temps Service
springdoc.swagger-ui.urls[4].url=/api/emploi-temps/api-docs
springdoc.swagger-ui.urls[5].name=Deroulement Enseignement Service
springdoc.swagger-ui.urls[5].url=/api/deroulement-enseignements/api-docs
```

## Utilisation

### 1. Accéder à Swagger UI

**Option A : Interface centralisée (Recommandé)**

1. Démarrer tous les services
2. Ouvrir http://localhost:8080/swagger-ui.html
3. Sélectionner le service dans le menu déroulant (en haut à droite)
4. Explorer les endpoints disponibles

**Option B : Service individuel**

1. Ouvrir http://localhost:8081/swagger-ui.html (exemple: Auth Service)
2. Explorer les endpoints du service

### 2. Tester une API

#### Exemple : Tester un endpoint GET

1. Aller sur http://localhost:8080/swagger-ui.html
2. Sélectionner "Auth Service" dans le menu
3. Cliquer sur un endpoint (ex: `GET /api/utilisateurs`)
4. Cliquer sur "Try it out"
5. Remplir les paramètres si nécessaire
6. Cliquer sur "Execute"
7. Voir la réponse dans la section "Server response"

#### Exemple : Tester un endpoint POST avec authentification

1. **Obtenir un token JWT** (si l'endpoint nécessite l'authentification)
   - Utiliser l'endpoint de login pour obtenir un token
   - Copier le token JWT

2. **Authentifier dans Swagger**
   - Cliquer sur le bouton "Authorize" (cadenas en haut à droite)
   - Entrer `Bearer <votre-token-jwt>` dans le champ "Value"
   - Cliquer sur "Authorize"
   - Fermer la fenêtre

3. **Tester l'endpoint**
   - Cliquer sur l'endpoint POST (ex: `POST /api/utilisateurs`)
   - Cliquer sur "Try it out"
   - Modifier le JSON dans "Request body"
   - Cliquer sur "Execute"

### 3. Télécharger la spécification OpenAPI

**Format JSON :**
```
http://localhost:8081/api-docs
```

**Utilisation :**
- Générer des clients API (TypeScript, Java, Python, etc.)
- Importer dans Postman
- Validation de contrat
- Documentation externe

## Annotations Swagger

### Documenter un contrôleur

```java
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/utilisateurs")
@Tag(name = "Utilisateurs", description = "API de gestion des utilisateurs")
public class UtilisateurController {

    @GetMapping("/{id}")
    @Operation(
        summary = "Récupérer un utilisateur par ID",
        description = "Retourne les détails complets d'un utilisateur en fonction de son identifiant"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<UtilisateurDTO> getUtilisateur(
        @Parameter(description = "ID de l'utilisateur") @PathVariable Long id
    ) {
        // ...
    }
}
```

### Documenter un DTO

```java
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Données d'un utilisateur")
public class UtilisateurDTO {

    @Schema(description = "Identifiant unique de l'utilisateur", example = "1")
    private Long id;

    @Schema(description = "Email de l'utilisateur", example = "jean.dupont@uasz.edu.sn")
    private String email;

    @Schema(description = "Rôle de l'utilisateur", example = "ENSEIGNANT",
            allowableValues = {"COORDINATEUR", "ENSEIGNANT", "ETUDIANT", "RESPONSABLE"})
    private String role;
}
```

### Documenter la sécurité

```java
@Operation(
    summary = "Créer un utilisateur",
    security = @SecurityRequirement(name = "bearer-jwt")
)
@PostMapping
public ResponseEntity<UtilisateurDTO> createUtilisateur(@RequestBody UtilisateurDTO dto) {
    // ...
}
```

## Tests

### Test 1 : Vérifier l'accessibilité Swagger

```bash
# API Gateway (centralisé)
curl http://localhost:8080/swagger-ui.html

# Auth Service
curl http://localhost:8081/swagger-ui.html

# Enseignant Service
curl http://localhost:8082/swagger-ui.html
```

**Résultat attendu :** Code HTTP 200

### Test 2 : Vérifier les API Docs JSON

```bash
# Auth Service
curl http://localhost:8081/api-docs | jq

# Via Gateway
curl http://localhost:8080/api/auth/api-docs | jq
```

**Résultat attendu :** JSON OpenAPI 3.0 valide

### Test 3 : Test d'un endpoint via Swagger

1. Ouvrir http://localhost:8080/swagger-ui.html
2. Sélectionner "Auth Service"
3. Tester `GET /` (page d'accueil)
4. Vérifier que la réponse est 200 OK

## Structure des fichiers

```
projet/
├── auth-service/
│   ├── pom.xml (+ springdoc-openapi-starter-webmvc-ui)
│   └── src/main/
│       ├── java/com/uasz/daos/auth/
│       │   └── config/
│       │       ├── OpenApiConfig.java (nouveau)
│       │       └── SecurityConfig.java (modifié)
│       └── resources/
│           └── application.properties (+ config swagger)
│
├── enseignant-service/
│   ├── pom.xml (+ springdoc)
│   └── src/main/
│       ├── java/com/uasz/daos/enseignant/config/
│       │   └── OpenApiConfig.java (nouveau)
│       └── resources/
│           └── application.properties (+ config swagger)
│
├── [maquette, choix, emploi-temps, deroulement]-service/
│   └── (même structure qu'enseignant-service)
│
└── api-gateway/
    ├── pom.xml (+ springdoc-openapi-starter-webflux-ui)
    └── src/main/resources/
        └── application.properties (+ config agrégation)
```

## Bonnes pratiques

### 1. Documentation

- ✅ Utiliser `@Tag` pour regrouper les endpoints
- ✅ Utiliser `@Operation` pour décrire chaque endpoint
- ✅ Utiliser `@ApiResponses` pour documenter tous les codes de retour possibles
- ✅ Utiliser `@Schema` pour documenter les DTOs
- ✅ Fournir des exemples (`example = "..."`)

### 2. Sécurité

- ✅ Configurer Spring Security pour permettre l'accès à Swagger sans authentification
- ✅ Documenter la sécurité JWT dans OpenApiConfig
- ✅ Utiliser `@SecurityRequirement` pour les endpoints protégés
- ⚠️ **En production** : Désactiver Swagger ou le protéger par authentification

### 3. Maintenance

- ✅ Mettre à jour la version dans OpenApiConfig quand l'API change
- ✅ Documenter les breaking changes
- ✅ Utiliser les tags sémantiques (v1, v2, etc.)

### 4. Performance

- ✅ Swagger est activé uniquement en développement
- ⚠️ En production : Désactiver via `springdoc.swagger-ui.enabled=false`

## Désactivation en production

Pour désactiver Swagger en production, ajouter dans `application-prod.properties` :

```properties
springdoc.swagger-ui.enabled=false
springdoc.api-docs.enabled=false
```

Ou via variable d'environnement :

```bash
SPRINGDOC_SWAGGER_UI_ENABLED=false
SPRINGDOC_API_DOCS_ENABLED=false
```

## Dépannage

### Problème : Swagger UI ne s'affiche pas

**Solutions :**
1. Vérifier que le service est démarré : `curl http://localhost:8081/actuator/health`
2. Vérifier les logs : `docker logs auth-service`
3. Vérifier la dépendance springdoc dans pom.xml
4. Vérifier que Spring Security autorise `/swagger-ui/**`

### Problème : API Docs vide ou incomplet

**Solutions :**
1. Vérifier `springdoc.packages-to-scan` dans application.properties
2. S'assurer que les contrôleurs sont dans le bon package
3. Redémarrer le service

### Problème : Authentification JWT ne fonctionne pas

**Solutions :**
1. Cliquer sur "Authorize" dans Swagger UI
2. Entrer `Bearer <token>` (pas juste le token)
3. Vérifier que le token est valide

### Problème : L'agrégation Gateway ne montre pas tous les services

**Solutions :**
1. Vérifier que tous les services sont enregistrés dans Eureka
2. Vérifier les URLs dans `springdoc.swagger-ui.urls` du Gateway
3. Vérifier les routes Gateway (`spring.cloud.gateway.routes`)

## Génération de clients

### Utiliser OpenAPI Generator

```bash
# Installer OpenAPI Generator
npm install -g @openapitools/openapi-generator-cli

# Générer un client TypeScript
openapi-generator-cli generate \
  -i http://localhost:8081/api-docs \
  -g typescript-axios \
  -o ./client-typescript

# Générer un client Java
openapi-generator-cli generate \
  -i http://localhost:8081/api-docs \
  -g java \
  -o ./client-java

# Générer un client Python
openapi-generator-cli generate \
  -i http://localhost:8081/api-docs \
  -g python \
  -o ./client-python
```

## Intégration Postman

1. Dans Postman, cliquer sur "Import"
2. Sélectionner "Link"
3. Entrer l'URL : `http://localhost:8081/api-docs`
4. Cliquer sur "Import"
5. La collection Postman est générée automatiquement

## Ressources

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI Specification 3.0](https://swagger.io/specification/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)
- [OpenAPI Generator](https://openapi-generator.tech/)

---

**Date de création :** 2025-12-24
**Version :** 1.0
**Auteur :** Équipe DAOS - UASZ
