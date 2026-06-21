package dev.ohhoonim.system.auditlog;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.modulith.moments.DayHasPassed;
import org.springframework.stereotype.Component;
import dev.ohhoonim.system.auditlog.activity.AuditLogQueryActivity;
import dev.ohhoonim.system.auditlog.application.AuditLogIntegrityService;
import dev.ohhoonim.system.auditlog.model.AuditLogId;

@Component
public class AuditLogIntegrityBatchListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AuditLogIntegrityService integrityService;
    private final AuditLogQueryActivity queryActivity; // 검증 대상 조회를 위해 필요

    public AuditLogIntegrityBatchListener(AuditLogIntegrityService integrityService,
            AuditLogQueryActivity queryActivity) {
        this.integrityService = integrityService;
        this.queryActivity = queryActivity;
    }

    /**
     * Spring Modulith Moments API: 하루가 종료될 때 발행되는 이벤트를 수신합니다.
     * @param dayEnded 종료된 날짜 정보를 담은 이벤트
     */
    @ApplicationModuleListener
    public void onDayEnded(DayHasPassed dayEnded) {
        LocalDate endedDate = dayEnded.getDate();
        log.info("[Batch] {} 날짜의 감사 로그 무결성 전수 검사를 시작합니다.", endedDate);

        // 1. 해당 날짜에 발생한 모든 로그 ID 조회
        // (Activity에 날짜 기반 조회 메서드가 있다고 가정)
        List<AuditLogId> targetIds = queryActivity.findIdsByDate(endedDate);

        int totalCount = targetIds.size();
        long tamperedCount = targetIds.stream()
            .map(id -> {
                try {
                    return integrityService.verify(id);
                } catch (Exception e) {
                    log.error("[Batch] 로그 검증 중 오류 발생. ID: {}", id.value(), e);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .filter(result -> !result.isIntact()) // 변조된 로그만 필터링
            .peek(result -> log.error("[ALERT] 위변조 탐지! ID: {}, Status: {}", 
                result.logId().getRawValue(), result.status()))
            .count();

        // 2. 결과 리포트 출력 및 알림
        if (tamperedCount > 0) {
            sendEmergencyAlert(endedDate, totalCount, tamperedCount);
        } else {
            log.info("[Batch] {} 무결성 검사 완료. 총 {}건 모두 이상 없음.", endedDate, totalCount);
        }
    }

    private void sendEmergencyAlert(LocalDate date, int total, long tampered) {
        // 실제 운영 환경에서는 여기서 메일 발송, 슬랙 알림 등을 수행합니다.
        log.error("!!! [보안 비상] {} 감사 로그 중 {}건(총 {}건)에서 위변조가 확인되었습니다. !!!", 
            date, tampered, total);
    }
}
