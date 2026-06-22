package dev.ohhoonim.system.user.activity;

import dev.ohhoonim.system.user.application.UserIdDto;

public interface VerifyIdentityActivity {

    UserIdDto verifyUser(String username, String encodedPassword);

}
