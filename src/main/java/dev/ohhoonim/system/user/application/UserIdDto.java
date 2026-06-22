package dev.ohhoonim.system.user.application;

import java.util.UUID;
import dev.ohhoonim.component.model.payload.Dto;

@Dto
public record UserIdDto (UUID internalId, UUID externalId){
}
