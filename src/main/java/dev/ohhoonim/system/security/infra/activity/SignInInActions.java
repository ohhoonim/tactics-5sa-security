package dev.ohhoonim.system.security.infra.activity;

import org.springframework.security.core.context.SecurityContextHolder;
import dev.ohhoonim.component.model.unit.Activity;
import dev.ohhoonim.system.security.activity.SignInActivity;
import dev.ohhoonim.system.security.activity.out.JwtBuildPort;
import dev.ohhoonim.system.security.application.SignedTokenResponse;
import dev.ohhoonim.system.security.model.JwtPrincipal;
import dev.ohhoonim.system.security.model.JwtToken;
import dev.ohhoonim.system.security.model.JwtTransitionEventPolicy;
import dev.ohhoonim.system.security.model.JwtTokenTransitionEvent.VerifySuccess;
import dev.ohhoonim.system.user.application.UserIdDto;
import jakarta.annotation.Nullable;

@Activity
public class SignInInActions implements SignInActivity{

    private final JwtTransitionEventPolicy policy;
    private final JwtBuildPort jwtBuildPort;

    public SignInInActions(JwtTransitionEventPolicy policy, JwtBuildPort jwtBuildPort) {
        this.policy = policy;
        this.jwtBuildPort = jwtBuildPort;
    }

    @Override
    public @Nullable SignedTokenResponse generateToken(UserIdDto userId) {
        var username = new JwtPrincipal(userId.externalId()); // externalId를 jwt username으로 사용 internalId 사용하면 안됨
        JwtToken token = JwtToken.reconstitute(username, null, null); // cridential과 authority 는 비워줌.
        token.tokenStateTransition(new VerifySuccess(), policy);
        if (token.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(token);
            String access = jwtBuildPort.accessToken(username);
            String refresh = jwtBuildPort.refreshToken(username); 
            return new SignedTokenResponse(access, refresh);
        }
        return null; 
    }
    
}
