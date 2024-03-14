package com.example.gatewayserver.config.filter;


import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
//@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

    Environment env;

    public JwtFilter(Environment env){
        this.env = env;
    }
    public static class Config{}


    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest(); //
            // 헤더에 토큰이 있는지 확인 -> 없으면 쿠키에 refresh token 확인
            if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION) &&
            !request.getCookies().containsKey("refreshToken")){
                return onError(exchange,"no authorization header", HttpStatus.UNAUTHORIZED);
            }

//            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
//            // login을 하면 token을 줌 -> 그러면 사용자는 이 token 을 헤더에 실어서 보냄. -> 그럼 서버는 해당 token이 제대로 된건지 검증.
//            String jwt = authorizationHeader.replace("Bearer","");
//
//            if(!isJwtValid(jwt)) {
//                // todo refresh token 확인
//            }


            return chain.filter(exchange);
        });
    }

    private Mono<Void> onError(ServerWebExchange exchange, String no_authorization_header, HttpStatus unauthorized) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(unauthorized);

        log.error("토큰 인증 실패 ={}",no_authorization_header);
        return response.setComplete();
    }

//    private boolean isJwtValid(String jwt) {
//        boolean returnValue = true;
//
//        String subject = null;
//        Jwts.parser().verifyWith(env.getProperty("token.secret"))
//
//
//        return returnValue;
    //
//    }


}
