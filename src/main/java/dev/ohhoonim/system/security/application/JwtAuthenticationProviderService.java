package dev.ohhoonim.system.security.application;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import dev.ohhoonim.system.security.activity.BearerTokenActivity;
import dev.ohhoonim.system.security.model.JwtToken;

@Service
public class JwtAuthenticationProviderService implements AuthenticationProvider {

    private final BearerTokenActivity tokenActivity;

    public JwtAuthenticationProviderService(BearerTokenActivity tokenActivity) {
        this.tokenActivity = tokenActivity;
    }

    @Override
    public @Nullable Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String token = (String) authentication.getCredentials();
        return tokenActivity.verifyWith(token);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtToken.class.isAssignableFrom(authentication);
    }

}
