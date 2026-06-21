package dev.ohhoonim.system.security.model;

import java.util.Collection;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import dev.ohhoonim.component.model.unit.Entity;
import dev.ohhoonim.system.security.model.JwtTokenStatus.UnVerified;

public class JwtToken implements Authentication, Entity<JwtPrincipal> {

    private @Nullable JwtPrincipal principal;
    private @Nullable String token;
    private Collection<? extends GrantedAuthority> authorites;
    private JwtTokenStatus authenticated;

    public JwtToken(String token) {
        this.token = token;
        this.authenticated = new UnVerified();
    }

    public void tokenStateTransition(JwtTokenTransitionEvent event,
            JwtTransitionEventPolicy policy) {
        JwtTokenTransitionResult result = policy.transition(authenticated, event);
        result.actions().forEach(action -> action.fowloowUp(this));
    }

    public static JwtToken reconstitute(JwtPrincipal principal, String token,
            Collection<? extends GrantedAuthority> authorities) {
        return new JwtToken(principal, token, authorities);
    }

    private JwtToken(@Nullable JwtPrincipal principal, @Nullable String token,
            Collection<? extends GrantedAuthority> authorites) {
        this.principal = principal;
        this.token = token;
        this.authorites = authorites;
    }

    @Override
    public String getName() {
        return this.principal.getName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorites;
    }

    @Override
    public @Nullable Object getCredentials() {
        return this.token;
    }

    @Override
    public @Nullable Object getDetails() {
        return null;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return this.principal;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated.getClass().equals(UnVerified.class) ? false : true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new SecurityAuthenticationException("use JwtTokenStatus");
    }

    public void setJwtTokenStatus(JwtTokenStatus status) {
        this.authenticated = status;
    }

    @Override
    public JwtPrincipal getId() {
        return this.principal;
    }

}
