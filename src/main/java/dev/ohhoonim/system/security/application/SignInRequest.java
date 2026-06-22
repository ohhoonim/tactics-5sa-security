package dev.ohhoonim.system.security.application;

import org.springframework.util.StringUtils;
import dev.ohhoonim.system.security.model.BearerTokenErrorCode;
import dev.ohhoonim.system.security.model.SecurityAuthenticationException;

public record SignInRequest(String username, String password) {
    public SignInRequest {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new SecurityAuthenticationException(BearerTokenErrorCode.INVALID_SIGNINFO);
        }
    }
}