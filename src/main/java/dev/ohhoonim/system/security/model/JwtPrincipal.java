package dev.ohhoonim.system.security.model;

import java.security.Principal;
import dev.ohhoonim.component.model.unit.EntityId;
import dev.ohhoonim.component.model.unit.ValueObject;
import io.micrometer.common.util.StringUtils;

@ValueObject
public record JwtPrincipal(String username) implements Principal, EntityId<String> {

    public JwtPrincipal {
        if (StringUtils.isEmpty(username)) {
            throw new SecurityAuthenticationException("username은 필수입니다.");
        }
    }

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public String getRawValue() {
        return this.username;
    }

    @Override
    public String getPublicValue() {
        return this.username;
    }


}
