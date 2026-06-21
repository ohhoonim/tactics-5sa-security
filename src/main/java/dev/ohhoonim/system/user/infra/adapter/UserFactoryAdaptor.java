package dev.ohhoonim.system.user.infra.adapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import dev.ohhoonim.component.model.unit.Adapter;
import dev.ohhoonim.system.user.activity.out.UserFactory;
import dev.ohhoonim.system.user.model.User;
import dev.ohhoonim.system.user.model.UserComponent;
import dev.ohhoonim.system.user.model.UserComponent.AccessSecurity;
import dev.ohhoonim.system.user.model.UserComponent.AllowedIpRange;
import dev.ohhoonim.system.user.model.UserComponent.AuthSourceCode;
import dev.ohhoonim.system.user.model.UserComponent.AuthorityDelegation;
import dev.ohhoonim.system.user.model.UserComponent.LoginInfo;
import dev.ohhoonim.system.user.model.UserComponent.MenuAction;
import dev.ohhoonim.system.user.model.UserComponent.PasswordCredentials;
import dev.ohhoonim.system.user.model.UserComponent.UserAuthorization;
import dev.ohhoonim.system.user.model.UserComponent.UserProfile;
import dev.ohhoonim.system.user.model.UserId;
import dev.ohhoonim.system.user.model.UserStatus;

@Adapter
public class UserFactoryAdaptor implements UserFactory {

    private final Map<Class<?>, Function<ResultSet, ? extends UserComponent>> registry =
            Map.of(
                UserProfile.class, wrap(rs -> 
                    new UserProfile(
                       rs.getString("username") ,
                       rs.getString("employee_no") ,
                       rs.getString("email") ,
                       rs.getObject("department_id", UUID.class) ,
                       rs.getString("position") ,
                       rs.getString("job_role") )
                ),
                LoginInfo.class, wrap(rs -> 
                    new LoginInfo(
                       rs.getString("password") ,
                       rs.getObject("last_login_at", Instant.class),
                       rs.getInt("failed_login_attemp"),
                       AuthSourceCode.fromString(rs.getString("auth_source"))
                )
            ));

    @Override
    public Map<Class<?>, Function<ResultSet, ? extends UserComponent>> registry() {
        return registry;
    }

    @Override
    public <T extends UserComponent> T narrow(UserComponent component, Class<T> targetType) {
        Object matched = switch(component) {
            case UserProfile u -> u;
            case LoginInfo u -> u;
            case PasswordCredentials u -> u;
            case UserAuthorization u -> u;
            case AccessSecurity u -> u;
            case AllowedIpRange u -> u;
            case AuthorityDelegation u -> u;
            case MenuAction u -> u;
            case AuthSourceCode u -> u;
            case null -> null;

        };
        return targetType.cast(matched);
    }

    @Override
    public User reconsitute(UserId id, List<Class<? extends UserComponent>> requiredVos,
            ResultSet rs) throws SQLException {

       Map<String, ? extends UserComponent> vos = composer(requiredVos, registry(), rs);

        return User.reconstitute(id, 
            narrow(vos.get("UserProfile"), UserProfile.class), 
            UserStatus.fromString(rs.getString("status").toUpperCase()) ,
            narrow(vos.get("LoginInfo"),LoginInfo.class), 
            null, 
            null, 
            null, 
            null, 
            null, 
            rs.getObject("created_at", Instant.class),
            rs.getString("created_by"),
            rs.getObject("modified_at", Instant.class),
            rs.getString("modified_by")
            );
    }

    @Override
    public String resolveRequiredColumns(List<Class<? extends UserComponent>> columnTypes) {
        List<String> defaultColumns = new ArrayList<>();
        defaultColumns.addAll(List.of("user_id", "external_id", "created_at", "created_by", "modified_at",
                "modified_by"));
       
        return Stream.concat(defaultColumns.stream(), dynamicColumns(columnTypes).stream())
            .distinct().collect(Collectors.joining(", "));
    }

   


}
