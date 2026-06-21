package dev.ohhoonim.system.user.infra.adapter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import dev.ohhoonim.component.model.unit.Adapter;
import dev.ohhoonim.system.user.activity.out.UserFactory;
import dev.ohhoonim.system.user.activity.out.UserPersistencePort;
import dev.ohhoonim.system.user.model.User;
import dev.ohhoonim.system.user.model.UserComponent;
import dev.ohhoonim.system.user.model.UserId;

@Adapter
public class UserPostgresRepository implements UserPersistencePort {

    private final JdbcClient jdbcClient;
    private final UserFactory userFactory;


    public UserPostgresRepository(JdbcClient jdbcClient, UserFactory userFactory) {
        this.jdbcClient = jdbcClient;
        this.userFactory = userFactory;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        var columns = userFactory.forLogin();
        String sql = """
                SELECT %s FROM system_users 
                WHERE username = :username
                """.formatted(userFactory.resolveRequiredColumns(columns));
        return jdbcClient.sql(sql).param("username", username)
            .query(userMapper.apply(userFactory, columns)).optional();
    }

    private BiFunction<UserFactory, List<Class<? extends UserComponent>>, RowMapper<User>> userMapper=
            (factory, columns) -> {
                return (rs, _) -> factory
                        .reconsitute(UserId.Creator.from(rs.getObject("user_id", UUID.class), rs.getObject("user_id", UUID.class))
                        , columns, rs);
            };

    @Override
    public void save(User user) {
        String sql = """
                    UPDATE system_users
                    SET last_login_at = :lastLoginAt,
                        failed_login_attempt = :failedAttempts,
                        modified_at = :modifiedAt,
                        modified_by = :modifiedBy
                    WHERE user_id = :id
                """;
        jdbcClient.sql(sql)
                .param("lastLoginAt", toOffsetDateTime(user.getLoginInfo().lastLoginAt()))
                .param("failedAttempts", user.getLoginInfo().failedLoginAttempt())
                .param("modifiedAt", toOffsetDateTime(user.getModifiedAt()))
                .param("modifiedBy", user.getModifiedBy()).param("id", user.getId().getRawValue())
                .update();
    }

    private OffsetDateTime toOffsetDateTime(java.time.Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }

}
