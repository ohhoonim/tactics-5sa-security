package dev.ohhoonim.system.security.activity;

import dev.ohhoonim.system.security.application.SignedTokenResponse;
import dev.ohhoonim.system.user.application.UserIdDto;
import jakarta.annotation.Nullable;

public interface SignInActivity {

    @Nullable SignedTokenResponse generateToken(UserIdDto userId);

}
