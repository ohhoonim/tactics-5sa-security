package dev.ohhoonim.system.security.infra.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import dev.ohhoonim.system.security.model.BearerTokenErrorCode;
import dev.ohhoonim.system.security.model.SecurityAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class BearerTokenAdapterTest {

    private static final String SECRET_KEY_STRING = "your-very-long-secret-key-for-jwt-validation-32-bytes";
    private BearerTokenAdapter bearerTokenAdapter;
    private SecretKey key;

    @BeforeEach
    void setUp() {
        bearerTokenAdapter = new BearerTokenAdapter(SECRET_KEY_STRING);
        key = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("유효한 토큰인 경우 Claims를 정상적으로 반환한다")
    void getClaims_Success() {
        String token = Jwts.builder()
                .subject("12345")
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(key)
                .compact();

        Claims claims = bearerTokenAdapter.getClaims(token);

        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo("12345");
    }

    @Test
    @DisplayName("만료된 토큰인 경우 SecurityAuthenticationException(TOKEN_EXPIRED)이 발생한다")
    void getClaims_Expired() {
        String token = Jwts.builder()
                .subject("12345")
                .expiration(new Date(System.currentTimeMillis() - 60000))
                .signWith(key)
                .compact();

        assertThatThrownBy(() -> bearerTokenAdapter.getClaims(token))
                .isInstanceOf(SecurityAuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", BearerTokenErrorCode.TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("형식이 잘못된 토큰인 경우 SecurityAuthenticationException(TOKEN_INVALID)이 발생한다")
    void getClaims_Malformed() {
        String invalidToken = "invalid.token.structure";

        assertThatThrownBy(() -> bearerTokenAdapter.getClaims(invalidToken))
                .isInstanceOf(SecurityAuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", BearerTokenErrorCode.TOKEN_INVALID);
    }

    @Test
    @DisplayName("서명이 일치하지 않는 토큰인 경우 SecurityAuthenticationException(TOKEN_SIGNATURE_INVALID)이 발생한다")
    void getClaims_InvalidSignature() {
        SecretKey wrongKey = Keys.hmacShaKeyFor("another-different-secret-key-for-jwt-validation-32-bytes".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("12345")
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(wrongKey)
                .compact();

        assertThatThrownBy(() -> bearerTokenAdapter.getClaims(token))
                .isInstanceOf(SecurityAuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", BearerTokenErrorCode.TOKEN_SIGNATURE_INVALID);
    }
}