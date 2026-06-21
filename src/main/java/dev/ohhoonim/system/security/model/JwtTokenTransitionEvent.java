package dev.ohhoonim.system.security.model;

import java.util.List;
import dev.ohhoonim.component.model.state.TransitionEvent;
import dev.ohhoonim.system.security.model.JwtTokenStatus.UnVerified;
import dev.ohhoonim.system.security.model.JwtTokenStatus.Verified;
import dev.ohhoonim.system.security.model.JwtTokenTransitionEvent.VerifyFail;
import dev.ohhoonim.system.security.model.JwtTokenTransitionEvent.VerifySuccess;

public sealed interface JwtTokenTransitionEvent extends TransitionEvent<JwtToken>
    permits VerifySuccess, VerifyFail {

    public record VerifySuccess() implements JwtTokenTransitionEvent {

        @Override
        public List<JwtTokenPostAction> actions() {
            return List.of(
                token -> token.setJwtTokenStatus(new Verified())
            );
        }

    }
    public record VerifyFail() implements JwtTokenTransitionEvent {

        @Override
        public List<JwtTokenPostAction> actions() {
            return List.of(
                token -> token.setJwtTokenStatus(new UnVerified())
            );
        }

    }
}
