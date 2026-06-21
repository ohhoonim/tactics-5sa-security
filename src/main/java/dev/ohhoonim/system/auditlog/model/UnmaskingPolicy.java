package dev.ohhoonim.system.auditlog.model;

import dev.ohhoonim.system.auditlog.application.UserRequester;

public interface UnmaskingPolicy {
    /**
     * @param data 마스킹/암호화된 데이터
     * @param requester 열람 요청자 정보 (권한 포함)
     * @return 권한에 따라 가공된 데이터
     */
    String process(String beforeData, UserRequester requester);

}
