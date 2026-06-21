package dev.ohhoonim.system.security.model;

import dev.ohhoonim.component.model.state.PostAction;

public interface JwtTokenPostAction extends PostAction<JwtToken> {

    public void fowloowUp(JwtToken context) ;
    
}
