package dev.ohhoonim.system.security.activity.out;

import dev.ohhoonim.system.security.model.JwtPrincipal;

public interface JwtBuildPort {

    String accessToken(JwtPrincipal username);

    String refreshToken(JwtPrincipal username);

}
