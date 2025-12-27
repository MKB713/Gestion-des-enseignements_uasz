package com.uasz.daos.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Filtre global pour logger toutes les requêtes et réponses passant par l'API Gateway
 */
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingGlobalFilter.java);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Log de la requête entrante
        String timestamp = LocalDateTime.now().format(formatter);
        String method = request.getMethod().toString();
        String path = request.getPath().toString();
        String clientIp = getClientIP(request);

        logger.info("🔹 [{}] Incoming Request: {} {} from {} - Headers: {}",
                timestamp, method, path, clientIp, request.getHeaders().toSingleValueMap());

        // Enregistrer le temps de début
        long startTime = System.currentTimeMillis();

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long duration = System.currentTimeMillis() - startTime;

            // Log de la réponse sortante
            logger.info("🔸 [{}] Outgoing Response: {} {} - Status: {} - Duration: {}ms",
                    LocalDateTime.now().format(formatter),
                    method, path,
                    response.getStatusCode(),
                    duration);
        }));
    }

    /**
     * Récupère l'adresse IP du client
     */
    private String getClientIP(ServerHttpRequest request) {
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeaders().getFirst("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddress() != null ?
                request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        }
        return ip;
    }

    @Override
    public int getOrder() {
        return -1; // Haute priorité pour logger en premier
    }
}
