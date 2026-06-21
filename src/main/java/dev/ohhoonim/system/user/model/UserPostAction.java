package dev.ohhoonim.system.user.model;

import dev.ohhoonim.component.model.state.PostAction;

public interface UserPostAction extends PostAction<User> {
    void fowloowUp(User context);
}
