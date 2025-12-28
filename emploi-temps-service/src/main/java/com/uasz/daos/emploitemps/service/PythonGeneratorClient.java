package com.uasz.daos.emploitemps.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uasz.daos.emploitemps.dto.generation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client Java pour communiquer avec le service Python de génération d'emploi du temps
 * Permet d'appeler l'algorithme d'optimisation Python depuis Java
 */
@Service
public class PythonGeneratorClient {

    private static final Logger logger = LoggerFactory.getLogger(PythonGeneratorClient.class);

    @Value("${python.generator.url:http://localhost:8000}")
    private String pythonServiceUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public PythonGeneratorClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Appelle le service Python pour générer un emploi du temps optimal
     *
     * @param request Requête contenant toutes les données nécessaires
     * @return Réponse avec les séances générées
     * @throws RuntimeException si l'appel échoue
     */
    public GenerationResponse genererEmploiDuTemps(GenerationRequest request) {
        try {
            logger.info("📞 Appel du service Python de génération d'emploi du temps...");
            logger.debug("URL: {}/api/generer-emploi-du-temps", pythonServiceUrl);

            // Préparer les headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Créer l'entité HTTP
            HttpEntity<GenerationRequest> entity = new HttpEntity<>(request, headers);

            // Appeler le service Python
            ResponseEntity<GenerationResponse> response = restTemplate.exchange(
                    pythonServiceUrl + "/api/generer-emploi-du-temps",
                    HttpMethod.POST,
                    entity,
                    GenerationResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                GenerationResponse result = response.getBody();
                logger.info("✅ Génération réussie: {} séances générées, {} conflits",
                        result.getSeances().size(), result.getConflits().size());
                return result;
            } else {
                throw new RuntimeException("Réponse invalide du service Python");
            }

        } catch (Exception e) {
            logger.error("❌ Erreur lors de l'appel au service Python: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération automatique: " + e.getMessage(), e);
        }
    }

    /**
     * Vérifie si le service Python est accessible
     *
     * @return true si le service répond, false sinon
     */
    public boolean isServiceAvailable() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    pythonServiceUrl + "/health",
                    String.class
            );
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.warn("⚠️ Service Python non accessible: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Vérifie les conflits d'une liste de séances
     *
     * @param seances Liste des séances à vérifier
     * @return Résultat de la vérification
     */
    public ConflitsResponse verifierConflits(java.util.List<SeanceGenereeDTO> seances) {
        try {
            logger.info("🔍 Vérification des conflits pour {} séances", seances.size());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<java.util.List<SeanceGenereeDTO>> entity = new HttpEntity<>(seances, headers);

            ResponseEntity<ConflitsResponse> response = restTemplate.exchange(
                    pythonServiceUrl + "/api/verifier-conflits",
                    HttpMethod.POST,
                    entity,
                    ConflitsResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ConflitsResponse result = response.getBody();
                logger.info("✅ Vérification terminée: {} conflits détectés", result.getNbConflits());
                return result;
            } else {
                throw new RuntimeException("Réponse invalide du service Python");
            }

        } catch (Exception e) {
            logger.error("❌ Erreur lors de la vérification des conflits: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la vérification: " + e.getMessage(), e);
        }
    }

    /**
     * Teste la connexion avec le service Python
     */
    public void testConnection() {
        logger.info("🧪 Test de connexion au service Python...");
        if (isServiceAvailable()) {
            logger.info("✅ Service Python accessible à {}", pythonServiceUrl);
        } else {
            logger.error("❌ Service Python non accessible à {}", pythonServiceUrl);
            throw new RuntimeException("Service Python non disponible");
        }
    }
}