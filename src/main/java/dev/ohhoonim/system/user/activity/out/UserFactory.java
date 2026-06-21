package dev.ohhoonim.system.user.activity.out;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;
import dev.ohhoonim.component.model.factory.ArFactory;
import dev.ohhoonim.system.user.model.User;
import dev.ohhoonim.system.user.model.UserComponent;
import dev.ohhoonim.system.user.model.UserComponent.LoginInfo;
import dev.ohhoonim.system.user.model.UserComponent.UserProfile;
import dev.ohhoonim.system.user.model.UserException;
import dev.ohhoonim.system.user.model.UserId;

public interface UserFactory extends ArFactory<User, UserId, UserComponent>{
    default List<Class<? extends UserComponent>> forLogin() {
        return List.of(UserProfile.class, LoginInfo.class);
    }

    default Function<ResultSet, ? extends UserComponent> wrap(UserArMapper mapper) {
        return rs -> {
            try {
                return mapper.map(rs);
            } catch (SQLException e) {
                throw new UserException("처리할 수 없는 컬럼이 존재합니다.", e);
            }
        };
    } 
   
}
