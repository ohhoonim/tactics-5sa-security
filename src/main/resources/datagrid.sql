-- component-_grid-_schemas
-- component-_grid-_layouts
-- component-_grid-_audit_logs
-- =============== Datagrid

-- 1. 그리드 스키마 메타데이터
CREATE TABLE if not exists datagrid_schemas (
    grid_id      CHAR(26)  NOT NULL,
    columns_json JSONB        NOT NULL, 
    created_at   TIMESTAMPZ NOT NULL,
    created_by   VARCHAR(50)  NOT NULL,
    modified_at  TIMESTAMPZ NOT NULL,
    modified_by  VARCHAR(50)  NOT NULL,
    PRIMARY KEY (grid_id)
);

COMMENT ON TABLE datagrid_schemas IS '그리드 스키마 정보 테이블';
COMMENT ON COLUMN datagrid_schemas.grid_id IS '그리드 식별자';
COMMENT ON COLUMN datagrid_schemas.columns_json IS '컬럼 정의 메타데이터 (ID, 타입, 마스킹여부 등)';
COMMENT ON COLUMN datagrid_schemas.created_at IS '생성일시';
COMMENT ON COLUMN datagrid_schemas.created_by IS '생성자';
COMMENT ON COLUMN datagrid_schemas.modified_at IS '수정일시';
COMMENT ON COLUMN datagrid_schemas.modified_by IS '수정자';


-- 2. 개인화 레이아웃
CREATE TABLE if not exists datagrid_layouts (
    grid_id     VARCHAR(50)  NOT NULL,
    user_id     VARCHAR(50)  NOT NULL,
    layout_json JSONB        NOT NULL,
    modified_at TIMESTAMPZ NOT NULL,
    PRIMARY KEY (grid_id, user_id),
    CONSTRAINT fk_layout_grid FOREIGN KEY (grid_id) REFERENCES datagrid_schemas (grid_id)
);

COMMENT ON TABLE datagrid_layouts IS '사용자별 그리드 레이아웃 설정';
COMMENT ON COLUMN datagrid_layouts.grid_id IS '그리드 식별자';
COMMENT ON COLUMN datagrid_layouts.user_id IS '사용자 식별자';
COMMENT ON COLUMN datagrid_layouts.layout_json IS '개인화 설정 (순서, 숨김 상태)';
COMMENT ON COLUMN datagrid_layouts.modified_at IS '최종 수정일시';


-- 3. 보안 감사 로그. datagrid 에서의 audit log는 별도로 관리함
CREATE TABLE if not exists datagrid_audit_logs (
    audit_id  BIGSERIAL    PRIMARY KEY, 
    user_id   VARCHAR(50)  NOT NULL,
    grid_id   VARCHAR(50)  NOT NULL,
    row_id    VARCHAR(100) NOT NULL,
    action    VARCHAR(20)  NOT NULL,
    access_at TIMESTAMPZ NOT NULL
);

-- 인덱스 생성
CREATE INDEX if not exists idx_grid_audit_logs_audit_idx ON datagrid_audit_logs (grid_id, access_at);
CREATE INDEX if not exists idx_grid_audit_logs_user_idx ON datagrid_audit_logs (user_id, access_at);

COMMENT ON TABLE datagrid_audit_logs IS '보안 감사 이력 로그';
COMMENT ON COLUMN datagrid_audit_logs.user_id IS '접근 사용자';
COMMENT ON COLUMN datagrid_audit_logs.grid_id IS '대상 그리드';
COMMENT ON COLUMN datagrid_audit_logs.row_id IS '대상 데이터 행 식별자';
COMMENT ON COLUMN datagrid_audit_logs.action IS '수행 작업 (예: UNMASK_REQUEST)';
COMMENT ON COLUMN datagrid_audit_logs.access_at IS '접근 일시';


