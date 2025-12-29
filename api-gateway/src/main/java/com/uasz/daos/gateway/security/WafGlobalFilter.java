package com.uasz.daos.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Web Application Firewall (WAF) pour l'API Gateway
 * Protège contre:
 * - Injection SQL
 * - XSS (Cross-Site Scripting)
 * - Path Traversal
 * - Requêtes malformées
 * - User-Agent suspects
 */
@Component
public class WafGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(WafGlobalFilter.class);

    // Patterns de détection d'attaques SQL Injection
    private static final List<Pattern> SQL_INJECTION_PATTERNS = List.of(
        Pattern.compile("('.+(--|\\/\\*))|('\\s*(OR|AND)\\s+')", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(\\bUNION\\b.*\\bSELECT\\b)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(\\bDROP\\b.*\\bTABLE\\b)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(\\bEXEC\\b.*\\()", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(\\bSELECT\\b.*\\bFROM\\b.*\\bWHERE\\b)", Pattern.CASE_INSENSITIVE)
    );

    // Patterns de détection XSS
    private static final List<Pattern> XSS_PATTERNS = List.of(
        Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<iframe[^>]*>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<embed[^>]*>", Pattern.CASE_INSENSITIVE)
    );

    // Patterns de détection Path Traversal
    private static final List<Pattern> PATH_TRAVERSAL_PATTERNS = List.of(
        Pattern.compile("\\.\\./"),
        Pattern.compile("\\.\\.\\\\"),
        Pattern.compile("%2e%2e/", Pattern.CASE_INSENSITIVE),
        Pattern.compile("%2e%2e\\\\", Pattern.CASE_INSENSITIVE)
    );

    // User-Agents suspects
    private static final List<String> SUSPICIOUS_USER_AGENTS = List.of(
        "sqlmap",
        "nikto",
        "nmap",
        "masscan",
        "burp",
        "owasp zap"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String queryString = request.getURI().getQuery();
        String userAgent = request.getHeaders().getFirst("User-Agent");

        // Vérifier User-Agent suspect
        if (userAgent != null && isSuspiciousUserAgent(userAgent)) {
            logger.warn("🚨 [WAF] Blocked suspicious User-Agent: {} from IP: {}",
                userAgent, request.getRemoteAddress());
            return blockRequest(exchange, "Suspicious User-Agent detected");
        }

        // Vérifier Path Traversal
        if (containsPathTraversal(path)) {
            logger.warn("🚨 [WAF] Blocked Path Traversal attempt on: {} from IP: {}",
                path, request.getRemoteAddress());
            return blockRequest(exchange, "Path Traversal attempt detected");
        }

        // Vérifier SQL Injection dans path et query
        if (containsSqlInjection(path) || (queryString != null && containsSqlInjection(queryString))) {
            logger.warn("🚨 [WAF] Blocked SQL Injection attempt on: {} from IP: {}",
                path, request.getRemoteAddress());
            return blockRequest(exchange, "SQL Injection attempt detected");
        }

        // Vérifier XSS dans path et query
        if (containsXss(path) || (queryString != null && containsXss(queryString))) {
            logger.warn("🚨 [WAF] Blocked XSS attempt on: {} from IP: {}",
                path, request.getRemoteAddress());
            return blockRequest(exchange, "XSS attempt detected");
        }

        // Log de la requête légitime
        logger.debug("✓ [WAF] Request passed security checks: {} from IP: {}",
            path, request.getRemoteAddress());

        return chain.filter(exchange);
    }

    private boolean isSuspiciousUserAgent(String userAgent) {
        String lowerUserAgent = userAgent.toLowerCase();
        return SUSPICIOUS_USER_AGENTS.stream()
            .anyMatch(lowerUserAgent::contains);
    }

    private boolean containsSqlInjection(String input) {
        return SQL_INJECTION_PATTERNS.stream()
            .anyMatch(pattern -> pattern.matcher(input).find());
    }

    private boolean containsXss(String input) {
        return XSS_PATTERNS.stream()
            .anyMatch(pattern -> pattern.matcher(input).find());
    }

    private boolean containsPathTraversal(String input) {
        return PATH_TRAVERSAL_PATTERNS.stream()
            .anyMatch(pattern -> pattern.matcher(input).find());
    }

    private Mono<Void> blockRequest(ServerWebExchange exchange, String reason) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().add("X-WAF-Block-Reason", reason);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -100; // Exécuter avant les autres filtres
    }
}
