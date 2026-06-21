package dev.ohhoonim.system.user.activity.out;

import java.util.Optional;
import dev.ohhoonim.system.user.model.User;

public interface UserPersistencePort {
    Optional<User> findByUsername(String username);
    void save(User user);
}
