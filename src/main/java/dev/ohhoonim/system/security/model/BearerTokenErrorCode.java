package dev.ohhoonim.system.security.model;

public enum BearerTokenErrorCode {
    INVALID_SUBJECT("TOKEN_001", "유효하지않은 Subject 정보입니다."), 
    TOKEN_EXPIRED("TOKEN_002", "토큰이 만료되었습니다."), 
    TOKEN_INVALID("TOKEN_003", "유효하지 않은 토큰 형식입니다."), 
    TOKEN_SIGNATURE_INVALID("TOKEN_004", "토큰 서명이 일치하지 않습니다."), 
    TOKEN_EMPTY("TOKEN_005", "토큰이 존재하지 않습니다."),
    NOT_ALLOWED("TOKEN_006", "임의로 인증상태를 변경할 수 없습니다."),
    INVALID_SIGNINFO("SIGN_001", "아이디 패스워드를 확인해주세요"),
    TOKEN_REFRESH_FAIL("SIGN_002", "새로운 토큰 발행에 실패했습니다.");

    private final String code;
    private final String message;

    BearerTokenErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
