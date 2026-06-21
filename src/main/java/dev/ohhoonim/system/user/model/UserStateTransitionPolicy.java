package dev.ohhoonim.system.user.model;

import dev.ohhoonim.component.model.state.StateTransitionPolicy;

public interface UserStateTransitionPolicy extends StateTransitionPolicy<UserStatus, UserTransitionEvent, User>{

}
