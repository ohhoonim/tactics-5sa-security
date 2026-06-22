package dev.ohhoonim.system.security.endpoint;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import dev.ohhoonim.component.model.unit.Endpoint;
import dev.ohhoonim.system.security.application.SignInRequest;
import dev.ohhoonim.system.security.application.SignService;
import dev.ohhoonim.system.security.application.SignedTokenResponse;
import jakarta.servlet.ServletException;

@Component
public class SignHandler implements Endpoint {

    private final SignService signService;

    @Value("${app.cookie.secure:false}")
    private Boolean isSecure;

    public SignHandler(SignService signService) {
        this.signService = signService;
    }

    public ServerResponse signIn(ServerRequest request) throws ServletException, IOException {
        var signInRequest = request.body(new ParameterizedTypeReference<SignInRequest>() {});
        var result = signService.signIn(signInRequest);

        return ServerResponse.ok()
                .header(HttpHeaders.SET_COOKIE,
                        createRefreshTokenCookie(result.refresh()).toString())
                .body(Map.of("access", result.access()));
    }

    public ServerResponse refresh(ServerRequest request) {
        String refreshToken = request.cookies().getFirst("refresh-token") != null
                ? request.cookies().getFirst("refresh-token").getValue()
                : request.headers().firstHeader("Refresh-Token");

        if (refreshToken == null || refreshToken.isBlank()) {
            return ServerResponse.badRequest().build();
        }
        SignedTokenResponse token = signService.refresh(refreshToken);

        return ServerResponse.ok()
                .header(HttpHeaders.SET_COOKIE,
                        createRefreshTokenCookie(token.refresh()).toString())
                .body(Map.of("access", token.access()));
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh-token", refreshToken).httpOnly(true).secure(isSecure)
                .path("/").maxAge(Duration.ofDays(7)).sameSite("Strict").build();
    }

}
