package dev.ohhoonim.system.security.model;

import dev.ohhoonim.component.model.state.Status;
import dev.ohhoonim.component.model.state.TransitionResult;
import dev.ohhoonim.system.security.model.JwtTokenStatus.UnVerified;
import dev.ohhoonim.system.security.model.JwtTokenStatus.Verified;

public sealed interface JwtTokenStatus extends
        Status<JwtTokenStatus, JwtTokenTransitionEvent, JwtToken>
        permits Verified, UnVerified {

    public record Verified() implements JwtTokenStatus {

        @Override
        public TransitionResult<JwtTokenStatus, JwtToken> trigger(
                JwtTokenTransitionEvent event) {
            throw new SecurityAuthenticationException("처리가능하지 않은 이벤트입니다.");
        }

    }
    public record UnVerified() implements JwtTokenStatus {

        @Override
        public JwtTokenTransitionResult trigger(
                JwtTokenTransitionEvent event) {
            return switch(event) {
                case JwtTokenTransitionEvent.VerifySuccess e -> new JwtTokenTransitionResult(new JwtTokenStatus.Verified(), e.actions());
                default -> throw new RuntimeException();
            };
        }

    }

}
