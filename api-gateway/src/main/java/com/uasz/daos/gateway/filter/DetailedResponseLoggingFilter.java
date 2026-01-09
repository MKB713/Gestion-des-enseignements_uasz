package com.uasz.daos.gateway.filter;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Filtre de débogage détaillé pour capturer le contenu des réponses
 */
@Component
@SuppressWarnings("null")
public class DetailedResponseLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(DetailedResponseLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        // Seulement pour les endpoints auth
        if (!path.contains("/auth/")) {
            return chain.filter(exchange);
        }

        logger.info("╔════════════════════════════════════════════════════════════════");
        logger.info("║ DÉBUT DU TRAITEMENT GATEWAY - {}", path);
        logger.info("╚════════════════════════════════════════════════════════════════");

        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();

        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;

                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        // Combiner tous les buffers
                        DataBuffer joinedBuffer = bufferFactory.join(dataBuffers);
                        byte[] content = new byte[joinedBuffer.readableByteCount()];
                        joinedBuffer.read(content);
                        DataBufferUtils.release(joinedBuffer);

                        String responseBody = new String(content, StandardCharsets.UTF_8);

                        logger.info("╔════════════════════════════════════════════════════════════════");
                        logger.info("║ RÉPONSE DU SERVICE BACKEND");
                        logger.info("╠════════════════════════════════════════════════════════════════");
                        logger.info("║ Path: {}", path);
                        logger.info("║ Status: {}", getDelegate().getStatusCode());
                        logger.info("║ Content-Type: {}", getDelegate().getHeaders().getContentType());
                        logger.info("║ Content-Length: {} bytes", content.length);
                        logger.info("╠════════════════════════════════════════════════════════════════");
                        logger.info("║ BODY:");
                        logger.info("╠════════════════════════════════════════════════════════════════");

                        // Logger le body (max 5000 caractères)
                        if (responseBody.length() > 5000) {
                            logger.info("║ {}", responseBody.substring(0, 5000));
                            logger.info("║ ... (body tronqué, {} caractères au total)", responseBody.length());
                        } else {
                            logger.info("║ {}", responseBody);
                        }

                        logger.info("╠════════════════════════════════════════════════════════════════");
                        logger.info("║ HEADERS DE RÉPONSE:");
                        logger.info("╠════════════════════════════════════════════════════════════════");
                        getDelegate().getHeaders().forEach((name, values) -> {
                            logger.info("║ {}: {}", name, String.join(", ", values));
                        });
                        logger.info("╚════════════════════════════════════════════════════════════════");

                        // Recréer le buffer pour l'envoyer au client
                        return bufferFactory.wrap(content);
                    }));
                }

                return super.writeWith(body);
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build())
                .doOnError(error -> {
                    logger.error("╔════════════════════════════════════════════════════════════════");
                    logger.error("║ ERREUR DANS LE GATEWAY");
                    logger.error("╠════════════════════════════════════════════════════════════════");
                    logger.error("║ Path: {}", path);
                    logger.error("║ Error: {}", error.getMessage());
                    logger.error("║ Type: {}", error.getClass().getName());
                    logger.error("╚════════════════════════════════════════════════════════════════");
                    error.printStackTrace();
                });
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE; // Exécuter en dernier pour voir la réponse finale
    }
}
