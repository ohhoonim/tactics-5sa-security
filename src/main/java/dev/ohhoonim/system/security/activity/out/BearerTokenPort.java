package dev.ohhoonim.system.security.activity.out;

import io.jsonwebtoken.Claims;

public interface BearerTokenPort {

    Claims getClaims(String token);
    

}
