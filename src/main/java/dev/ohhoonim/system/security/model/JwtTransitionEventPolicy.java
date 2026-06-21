package dev.ohhoonim.system.security.model;

import dev.ohhoonim.component.model.state.StateTransitionPolicy;

public interface JwtTransitionEventPolicy
		extends StateTransitionPolicy<JwtTokenStatus, JwtTokenTransitionEvent, JwtToken> {
	public default JwtTokenTransitionResult transition(JwtTokenStatus status, JwtTokenTransitionEvent transitionEvent) {
		return (JwtTokenTransitionResult)status.trigger(transitionEvent);
	}
}
