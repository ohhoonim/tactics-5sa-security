package dev.ohhoonim.system.security.model;

import java.util.List;
import dev.ohhoonim.component.model.state.PostAction;
import dev.ohhoonim.component.model.state.TransitionResult;

public record JwtTokenTransitionResult(
    JwtTokenStatus status,
    List<JwtTokenPostAction> actions
) implements TransitionResult<JwtTokenStatus, JwtToken> {
}
