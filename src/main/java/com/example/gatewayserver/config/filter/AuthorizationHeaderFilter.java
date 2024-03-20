package com.example.gatewayserver.config.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    Environment env;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info(">>gateway AuthorizationHeaderFilter<<");
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey("access")) {
                return onError(exchange, "no access token", HttpStatus.UNAUTHORIZED);
            }

            String accessToken = request.getHeaders().get("access").get(0);
            log.info("accessToken : {}", accessToken);

            if (!isValidToken(accessToken)) {
                return onError(exchange, "invalid token", HttpStatus.UNAUTHORIZED);
            }



            return chain.filter(exchange);
        });
    }

    private boolean isValidToken(String token) {
        boolean result;
        String key = env.getProperty("jwt.secret");
        SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());

        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            result = true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
            result = false;
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
            result = false;
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
            result = false;
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
            result = false;
        }

        return result;

    }




    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus code) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(code);

        log.error(message);
        return response.setComplete();
    }

    public static class Config { }
}
