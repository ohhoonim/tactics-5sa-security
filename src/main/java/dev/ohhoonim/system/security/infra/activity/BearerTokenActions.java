package dev.ohhoonim.system.security.infra.activity;

import dev.ohhoonim.component.model.unit.Activity;
import dev.ohhoonim.system.security.activity.BearerTokenActivity;
import dev.ohhoonim.system.security.activity.out.BearerTokenPort;
import dev.ohhoonim.system.security.model.JwtPrincipal;
import dev.ohhoonim.system.security.model.JwtToken;
import dev.ohhoonim.system.security.model.JwtTransitionEventPolicy;
import dev.ohhoonim.system.security.model.JwtTokenTransitionEvent.VerifySuccess;
import io.jsonwebtoken.Claims;

@Activity
public class BearerTokenActions implements BearerTokenActivity {

    private final BearerTokenPort tokenPort;
    private final JwtTransitionEventPolicy policy;

    public BearerTokenActions(BearerTokenPort tokenPort, JwtTransitionEventPolicy policy) {
        this.tokenPort = tokenPort;
        this.policy = policy;
    }

    @Override
    public JwtToken verifyWith(String token) {
        Claims claims = tokenPort.getClaims(token);

        var principal = JwtPrincipal.Creator.fromPublic(claims.getSubject());
        // credential 정보 유지하지 않음. authority 정보 사용하지 않음.
        var jwtToken = JwtToken.reconstitute(principal, null, null);
        jwtToken.tokenStateTransition(new VerifySuccess(), policy);

        return jwtToken;
    }


}
