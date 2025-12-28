package com.uasz.daos.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * Filtre personnalisé pour ajouter des headers aux requêtes
 */
@Component
public class RequestHeaderFilter extends AbstractGatewayFilterFactory<RequestHeaderFilter.Config> {

    public RequestHeaderFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var request = exchange.getRequest().mutate()
                    .header("X-Gateway-Request-Id", java.util.UUID.randomUUID().toString())
                    .header("X-Gateway-Request-Time", String.valueOf(System.currentTimeMillis()))
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    public static class Config {
        // Configuration properties if needed
    }
}
