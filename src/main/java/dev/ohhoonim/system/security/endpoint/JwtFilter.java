package dev.ohhoonim.system.security.endpoint;

import java.io.IOException;
import java.util.Optional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import dev.ohhoonim.component.model.unit.Endpoint;
import dev.ohhoonim.system.security.model.JwtToken;
import dev.ohhoonim.system.security.model.SecurityAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtFilter extends OncePerRequestFilter implements Endpoint {
    private final AuthenticationManager authenticationManager;

    public JwtFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        extractBearerToken(request).ifPresent(token -> {
            var unAuthToken = new JwtToken(token);
            var authResult = authenticationManager.authenticate(unAuthToken);
            if (authResult.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authResult);
            }
        });

        filterChain.doFilter(request, response);
    }

    private Optional<String> extractBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return Optional.of(bearerToken.substring(7));
        }
        return Optional.empty();
    }

}
