package dev.ohhoonim.system.auditlog;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import dev.ohhoonim.system.auditlog.application.AuditLogRecordCommand;
import dev.ohhoonim.system.auditlog.application.AuditLogService;

@Component
public class AuditLogEventListener {

    private final AuditLogService auditLogService;

    public AuditLogEventListener(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /**
     * AuditLogEvent를 구독하여 시스템 감사 로그를 생성합니다.
     * @ApplicationModuleListener는 트랜잭션 성공 후 비동기 실행을 보장합니다.
     */
    @ApplicationModuleListener
    public void onAuditLogRequested(AuditLogEvent event) {
        // 1. 이벤트 데이터를 서비스 커맨드로 매핑
        // (필드 구조가 유사하므로 직접 변환하거나 팩토리 메서드 활용)
        var command = new AuditLogRecordCommand(
            event.occurredAt(),
            event.clientIp(),
            event.userAgent(),
            event.actorId(),
            event.targetId(),
            event.targetType(),
            event.actionCategory(),
            event.actionType(),
            event.resultStatus(),
            event.beforeData(),
            event.afterData(),
            event.reason()
        );

        // 2. 비즈니스 로직(마스킹, 무결성 봉인) 수행 후 저장
        auditLogService.record(command);
    }
}
