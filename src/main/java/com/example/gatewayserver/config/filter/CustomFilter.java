package com.example.gatewayserver.config.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    public CustomFilter(){
        super(Config.class);
    }

    public static class Config{
        // Put the configuration properties
    }
    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter. 여기서 우리는 JWT값을 추출하고 인증 작업을 진행 할 수 있다.
        return ((exchange, chain) -> { // 람다식으로 객체 두개를 받음
            ServerHttpRequest request = exchange.getRequest(); // exchange 객체에서 request와 response 정보를 얻을 수 있음.
           ServerHttpResponse response = exchange.getResponse();

            log.info("request URI : {}",request.getURI());

            log.info("Custom PRE filter : request id -> {}",request.getId());

            // Custom Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(()->{ // post filter를 추가하기 위해 chain 객체에 filter를 추가.
                // Mono 라는 WebFlux 객체를 반환 (비동기 방식에서 단일값을 전달할때 사용)

                log.info("Custom POST filter : response code -> {}",response.getStatusCode());
            }));

        });
    }


}
