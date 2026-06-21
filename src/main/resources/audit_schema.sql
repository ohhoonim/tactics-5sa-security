-- =============== audit

-- 1. 감사로그 테이블 생성
CREATE TABLE if not exists system_audit_logs (
    -- ULID는 26자이므로 CHAR(26) 또는 VARCHAR(26)을 사용합니다.
    log_id              CHAR(26)         PRIMARY KEY,
    
    -- 주체 및 대상 (Actors)
    actor_id            VARCHAR(50)      NOT NULL,
    target_id           VARCHAR(50)      NOT NULL,
    target_type         VARCHAR(30)      NOT NULL,
    
    -- 행위 상세 (Action Details)
    action_category     VARCHAR(30)      NOT NULL,
    action_type         VARCHAR(30)      NOT NULL,
    result_status       VARCHAR(15)      NOT NULL,
    
    -- 일시 및 환경 (Context) - TIMESTAMPTZ 권장
    occurred_at         TIMESTAMPTZ      NOT NULL,
    client_ip           INET,            -- PostgreSQL 전용 IP 타입 사용 가능
    user_agent          TEXT,
    
    -- 데이터 스냅샷 (Data Snapshot) - JSONB 사용 (이진 저장 및 인덱싱 최적화)
    before_data         JSONB,
    after_data          JSONB,
    reason              TEXT,
    
    -- 보안 무결성 및 마스킹 정보
    integrity_hash      TEXT             NOT NULL,
    masked_fields       TEXT,            -- CSV 형태 저장
    
    -- Auditing 정보 (BaseEntity 기반)
    created_at          TIMESTAMPTZ      NOT NULL,
    created_by          VARCHAR(50)      NOT NULL,
    modified_at         TIMESTAMPTZ      NOT NULL,
    modified_by         VARCHAR(50)      NOT NULL
);

-- 2. 성능 최적화를 위한 인덱스 설계
-- ULID(log_id)가 PK이므로 시간순 정렬 삽입 성능은 기본적으로 확보됩니다.

-- 특정 시점 로그 검색 최적화
CREATE INDEX if not exists idx_system_audit_logs_occurred_at ON system_audit_logs (occurred_at DESC);

-- 특정 행위자 또는 대상별 감사 추적 최적화
CREATE INDEX if not exists idx_system_audit_logs_actor ON system_audit_logs (actor_id);
CREATE INDEX if not exists idx_system_audit_logs_target ON system_audit_logs (target_id, target_type);

-- (선택) JSONB 내부 특정 필드 검색이 잦을 경우 GIN 인덱스 추가
CREATE INDEX if not exists idx_system_audit_logs_after_data_gin ON system_audit_logs USING GIN (after_data);

