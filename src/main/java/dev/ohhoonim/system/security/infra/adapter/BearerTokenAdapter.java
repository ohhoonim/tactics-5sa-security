package dev.ohhoonim.system.security.infra.adapter;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import dev.ohhoonim.component.model.unit.Adapter;
import dev.ohhoonim.system.security.activity.out.BearerTokenPort;
import dev.ohhoonim.system.security.model.BearerTokenErrorCode;
import dev.ohhoonim.system.security.model.SecurityAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;

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
            return claims;
        } catch (ExpiredJwtException e) {
            log.error(BearerTokenErrorCode.TOKEN_EXPIRED.getMessage());
            throw new SecurityAuthenticationException(BearerTokenErrorCode.TOKEN_EXPIRED, e);
        } catch (MalformedJwtException | UnsupportedJwtException e) {
            log.error(BearerTokenErrorCode.TOKEN_INVALID.getMessage());
            throw new SecurityAuthenticationException(BearerTokenErrorCode.TOKEN_INVALID, e);
        } catch (SecurityException e) {
            log.error(BearerTokenErrorCode.TOKEN_SIGNATURE_INVALID.getMessage());
            throw new SecurityAuthenticationException(BearerTokenErrorCode.TOKEN_SIGNATURE_INVALID, e);
        } catch(JwtException e) {
            log.error(BearerTokenErrorCode.TOKEN_EMPTY.getMessage());
            throw new SecurityAuthenticationException(BearerTokenErrorCode.TOKEN_EMPTY, e);
        }     
    }
}
