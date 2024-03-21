package com.example.gatewayserver.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenUtil {

    private final SecretKey secretKey;

    @Autowired
    public TokenUtil(@Value("${jwt.secret}") String key) {
        this.secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // uuid 추출
    public String getUserId(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("userId", String.class);
    }

    // 토큰 종류 추출
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("category", String.class);
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        boolean result;
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            log.info("유효한 JWT 토큰입니다.");
            result = true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            result = false;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            result = false;
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            result = false;
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            result = false;
        }
        return result;

    }

}
