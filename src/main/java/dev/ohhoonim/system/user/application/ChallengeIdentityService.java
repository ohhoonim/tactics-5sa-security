package dev.ohhoonim.system.user.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.ohhoonim.system.user.activity.VerifyIdentityActivity;
import dev.ohhoonim.system.user.model.User;

@Service
public class ChallengeIdentityService {

    private final VerifyIdentityActivity verifyIdentityActivity;

    public ChallengeIdentityService(VerifyIdentityActivity verifyIdentityActivity) {
        this.verifyIdentityActivity = verifyIdentityActivity;
    }



    @Transactional(readOnly = true)
    public UserIdDto findByUsername(String username, String encodedPassword) {
        return verifyIdentityActivity.verifyUser(username, encodedPassword);
    }

}
