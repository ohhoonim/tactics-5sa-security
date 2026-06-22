package dev.ohhoonim.system.security.infra.adapter;

import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import dev.ohhoonim.component.model.unit.Adapter;
import dev.ohhoonim.system.security.activity.out.JwtBuildPort;
import dev.ohhoonim.system.security.model.JwtPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Adapter
public class JwtBuildAdapter implements JwtBuildPort {

    private final SecretKey key;

    public JwtBuildAdapter(@Value("${jwt.key}") String keyString) {
        this.key = Keys.hmacShaKeyFor(keyString.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String accessToken(JwtPrincipal username) {
        return generateToken(username.getPublicValue(), TokenType.ACCESS);
    }

    @Override
    public String refreshToken(JwtPrincipal username) {
        return generateToken(username.getPublicValue(), TokenType.REFRESH);
    }

    private String generateToken(String username, TokenType tokenType) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime expiryZdt = now.plus(tokenType.duration, tokenType.unit);
        Date expiryDate = Date.from(expiryZdt.toInstant());

        return Jwts.builder().subject(username).expiration(expiryDate)
                .issuedAt(Date.from(now.toInstant())).signWith(key).compact();
    }

    public enum TokenType {
        ACCESS(1, ChronoUnit.HOURS), REFRESH(7, ChronoUnit.DAYS), DENY(-7, ChronoUnit.DAYS); // deny token for a blacklist check

        private final long duration;
        private final ChronoUnit unit;

        TokenType(long duration, ChronoUnit unit) {
            this.duration = duration;
            this.unit = unit;
        }
    }
}


