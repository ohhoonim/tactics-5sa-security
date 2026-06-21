package dev.ohhoonim.system.security.application;

import dev.ohhoonim.component.model.payload.Dto;

@Dto
public record SignedTokenResponse(String access, String refresh) {
}