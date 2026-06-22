package dev.ohhoonim.system.user.infra.activity;

import dev.ohhoonim.component.model.unit.Activity;
import dev.ohhoonim.system.user.activity.VerifyIdentityActivity;
import dev.ohhoonim.system.user.activity.out.UserPersistencePort;
import dev.ohhoonim.system.user.application.UserIdDto;
import dev.ohhoonim.system.user.model.UserException;

@Activity
public class VerifyIdentityActions implements VerifyIdentityActivity {

    private final UserPersistencePort userPersistencePort;

    public VerifyIdentityActions(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    public UserIdDto verifyUser(String username, String encodedPassword) {
        var user = userPersistencePort.findByUsername(username).orElseThrow(() -> 
            new UserException("사용자가 존재하지 않습니다.")
        );
        if (encodedPassword.equals(user.getLoginInfo().password())) {
            new UserException("패스워드가 일치하지 않습니다.");
        }

        return new UserIdDto(user.getId().internalId(), user.getId().externalId());
    }

}
