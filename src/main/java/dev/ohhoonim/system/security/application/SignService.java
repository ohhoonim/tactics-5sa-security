package dev.ohhoonim.system.security.application;

import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import dev.ohhoonim.system.security.activity.BearerTokenActivity;
import dev.ohhoonim.system.security.activity.SignInActivity;
import dev.ohhoonim.system.security.model.BearerTokenErrorCode;
import dev.ohhoonim.system.security.model.SecurityAuthenticationException;
import dev.ohhoonim.system.user.application.ChallengeIdentityService;
import dev.ohhoonim.system.user.application.UserIdDto;

@Service
public class SignService {

    private final SignInActivity signInActivity;
    private final ChallengeIdentityService challengeIdentityService;
    private final PasswordEncoder encoder;

    private final BearerTokenActivity bearerTokenActivity;

    public SignService(SignInActivity signInActivity,
            ChallengeIdentityService challengeIdentityService, PasswordEncoder encoder,
            BearerTokenActivity bearerTokenActivity) {
        this.signInActivity = signInActivity;
        this.challengeIdentityService = challengeIdentityService;
        this.encoder = encoder;
        this.bearerTokenActivity = bearerTokenActivity;
    }

    public SignedTokenResponse signIn(SignInRequest request) {
        var encodedPassword = encoder.encode(request.password());
        UserIdDto userId =
                challengeIdentityService.findByUsername(request.username(), encodedPassword);

        SignedTokenResponse signedToken = signInActivity.generateToken(userId);
        if (signedToken == null) {
            throw new SecurityAuthenticationException(BearerTokenErrorCode.INVALID_SIGNINFO);
        }

        return signedToken;
    }

    public SignedTokenResponse refresh(String refreshToken) {
        var jwtToken = bearerTokenActivity.verifyWith(refreshToken);

        SignedTokenResponse refreshed = signInActivity
                .generateToken(new UserIdDto(null, UUID.fromString(jwtToken.getName())));
        if (refreshed == null) {
            throw new SecurityAuthenticationException(BearerTokenErrorCode.TOKEN_REFRESH_FAIL);
        }
        return refreshed;
    }
}
