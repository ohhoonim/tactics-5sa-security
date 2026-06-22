package dev.ohhoonim.system.security.model;

import java.security.Principal;
import java.util.UUID;
import dev.ohhoonim.component.model.unit.EntityId;
import dev.ohhoonim.component.model.unit.ValueObject;

@ValueObject
public record JwtPrincipal(UUID username) implements Principal, EntityId<UUID> {

    public JwtPrincipal {
        if (username == null) {
            throw new SecurityAuthenticationException(BearerTokenErrorCode.INVALID_SUBJECT);
        }
    }

    public static final Creator<UUID, JwtPrincipal> Creator = new Creator<>() {

        @Override
        public JwtPrincipal from(UUID internalId, UUID externalId) {
            throw new UnsupportedOperationException("Unimplemented method 'from'");
        }

        @Override
        public JwtPrincipal fromPublic(String publicId) {
            return new JwtPrincipal(UUID.fromString(publicId));
        }

        @Override
        public JwtPrincipal generate() {
            throw new UnsupportedOperationException("Unimplemented method 'generate'");
        }

    };

    @Override
    public String getName() {
        return this.username.toString();
    }

    @Override
    public UUID getRawValue() {
        return this.username;
    }

    @Override
    public String getPublicValue() {
        return this.username.toString();
    }
}
