-- =============== user
create table if not exists users (
    -- 1. 식별자 (Internal & External)
    user_id uuid not null,
    external_id uuid not null,
    
    -- 2. 기본 정보
    status varchar(30),
    username text not null,
    employee_no text,
    email text not null,
    department_id uuid,
    position text,
    job_role text,
    
    -- 3. 계정 자격 증명
    password text not null,
    last_login_at timestamptz(6),
    failed_login_attempt smallint default 0,
    auth_source varchar(20),
    
    -- 4. Auditing
    created_at timestamptz(6) not null default now(),
    created_by uuid not null,
    modified_at timestamptz(6) not null default now(),
    modified_by uuid not null,

    -- 제약 조건 명명 (콤마 추가 및 중복 정리)
    constraint pk_users primary key (user_id),
    constraint uk_users_external_id unique (external_id),
    constraint uk_users_email unique (email),
    constraint uk_users_username unique (username) -- Login ID 중복 방지 추가
);

-- 인덱스 관련 참고:
-- UNIQUE 제약 조건(uk_users_external_id, uk_users_email)은 
-- PostgreSQL에서 자동으로 해당 컬럼에 인덱스를 생성합니다.
-- 따라서 별도의 CREATE INDEX 문은 자원 낭비이므로 작성하지 않아도 됩니다.

-- (선택) 조회가 빈번한 Auditing 컬럼에 대한 인덱스
create index idx_users_created_at on users(created_at);

-- insert into system_users  (user_id, status, username, email, password, last_login_at,
-- created_at, created_by, modified_at, modified_by )
-- values ('01KNJVDNP87QWTGN7BD7FMQ0YM', 'ACTIVE', 'tester', 'tester@tester.ko',
-- '{bcrypt}$2a$10$ZGcRo0wiUfDilBkJJvZbiu37D12OOeQ6IYt19Ybtcnld86/.hQ1g.', now(),
-- now(), 'system', now(), 'system');