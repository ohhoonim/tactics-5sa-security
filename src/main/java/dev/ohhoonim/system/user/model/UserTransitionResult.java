package dev.ohhoonim.system.user.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import dev.ohhoonim.component.model.state.TransitionResult;

public record UserTransitionResult(
    UserStatus status,
    List<UserPostAction> actions
) implements TransitionResult<UserStatus, User>{
    
    public UserTransitionResult {
        actions = List.copyOf(Objects.requireNonNullElse(actions, Collections.emptyList()));
    }
}
