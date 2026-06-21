package dev.ohhoonim.system.security.infra.adapter;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import dev.ohhoonim.component.model.unit.Adapter;
import dev.ohhoonim.system.security.activity.out.BearerTokenPort;
import dev.ohhoonim.system.security.model.SecurityAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Adapter
public class BearerTokenAdapter implements BearerTokenPort {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final SecretKey key;

    public BearerTokenAdapter(@Value("${jwt.key}") String keyString) {
        this.key = Keys.hmacShaKeyFor(keyString.getBytes(StandardCharsets.UTF_8));
    }

    public Claims getClaims(String token) {
        try {
            var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            claims.getExpiration();
            return claims;
        } catch(JwtException | IllegalArgumentException e) {
            throw new SecurityAuthenticationException("토큰을 확인해주세요", e);
        }     
    }


}
