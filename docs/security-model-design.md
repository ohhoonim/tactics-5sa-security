# Security domain 모듈 (feat. Spring Security)

### Spring Security를 제대로 사용하려면

- Spring Security는 로그인할 때의 Endpoint일 뿐이다. User 도메인은 거들뿐이다.
- SecurityFilterChain은 로그인과 별개로 동작한다. authentication, authrization, sigin 
- 인증시 사용되는 보안로직(JWT, OAuth2, MFA 등)을 공유할 뿐이다.
- 기존의 아키텍쳐는 잊자. 쪼개야 산다

## 인증인가 도메인과 User도메인의 로그인

```mermaid
sequenceDiagram
    autonumber
    actor Client as 클라이언트
    participant Auth as 인증/인가 도메인
    participant User as User 도메인

    Client->>Auth: 로그인 요청 (ID, PW)
    Auth->>User: 사용자 검증 요청 (ID, PW)
    User->>User: DB 조회 및 PW 검증
    User-->>Auth: 검증 결과 반환 (User ID 또는 성공 여부)
    Auth->>Auth: 토큰 생성
    Auth-->>Client: 토큰 전달 (Access Token 등)
```

## DDD 구성요소

### 요약 목록

* 도메인 모델링 구성요소: Entity, Value Object, Aggregate, Factory, Repository
* 도메인 로직 및 제어 구성요소: Domain Service, Application Service
* 컨텍스트 및 경계 구성요소: Bounded Context, Context Map

## Spring Security 구성요소 분석 

```mermaid
classDiagram
    class SecurityFilterChain {
        <<interface>>
        +matches(HttpServletRequest) bool
        +getFilters() List~Filter~
    }

    class DelegatingFilterProxy {
        +doFilter(ServletRequest, ServletResponse, FilterChain)
    }

    class FilterChainProxy {
        -filterChains List~SecurityFilterChain~
        +doFilter(ServletRequest, ServletResponse, FilterChain)
    }

    class SecurityContextHolder {
        <<static>>
        +getContext() SecurityContext
        +setContext(SecurityContext)
    }

    class SecurityContext {
        <<interface>>
        +getAuthentication() Authentication
        +setAuthentication(Authentication)
    }

    class Authentication {
        <<interface>>
        +getPrincipal() Object
        +getCredentials() Object
        +getAuthorities() Collection~GrantedAuthority~
        +isAuthenticated() bool
    }

    class AuthenticationManager {
        <<interface>>
        +authenticate(Authentication) Authentication
    }

    class ProviderManager {
        -providers List~AuthenticationProvider~
        +authenticate(Authentication) Authentication
    }

    class AuthenticationProvider {
        <<interface>>
        +authenticate(Authentication) Authentication
        +supports(Class) bool
    }

    class UserDetailsService {
        <<interface>>
        +loadUserByUsername(String) UserDetails
    }

    DelegatingFilterProxy --> FilterChainProxy : delegates to
    FilterChainProxy --> SecurityFilterChain : contains
    SecurityContextHolder --> SecurityContext : holds
    SecurityContext --> Authentication : contains
    ProviderManager ..|> AuthenticationManager : implements
    ProviderManager --> AuthenticationProvider : delegates to
    AuthenticationProvider --> UserDetailsService : uses
```

### DDD 관점에서의 분류

* Aggregate Root (애그리거트 루트)
    * `SecurityFilterChain`: 보안 필터 체인의 진입점이자 전체 필터 목록을 관리하는 경계 역할을 합니다.
    * `SecurityContextHolder`: 애플리케이션 내에서 보안 컨텍스트에 접근하는 최상위 전역 진입점입니다.
    * `AuthenticationManager` / `ProviderManager`: 인증 프로세스 전체를 총괄하고 조율하는 중심 객체입니다.


* Entity (엔티티)
    * `SecurityContext`: 고유한 인증 상태(`Authentication`)를 유지하고 관리하며, 스레드마다 독립적인 식별성을 가집니다.


* Value Object (값 객체)
    * `Authentication`: 사용자의 인증 정보, 권한 목록(`GrantedAuthority`), 인증 여부 등을 담고 있는 불변 성격의 데이터 객체입니다.
    * `GrantedAuthority`: 사용자가 가진 개별 권한을 나타내는 불변 객체입니다.
    * `UserDetails`: 사용자의 핵심 정보(아이디, 비밀번호, 상태 등)를 캡슐화한 데이터 객체입니다.


* Domain Service (도메인 서비스)
    * `AuthenticationProvider`: 실제 인증 로직(비밀번호 비교, 사용자 검증 등)을 수행하는 비즈니스 서비스입니다.
    * `UserDetailsService`: 저장소에서 사용자 정보를 조회하는 특정 도메인 비즈니스 로직을 처리하는 서비스입니다.


* Infrastructure / Factory (인프라스트럭처 / 팩토리)
    * `DelegatingFilterProxy`: 서블릿 컨테이너와 스프링 컨텍스트를 연결하는 인프라스트럭처 필터입니다.
    * `FilterChainProxy`: 스프링 시큐리티의 인프라적 필터 처리를 위임받아 수행하는 핵심 엔진입니다.


## 5-Step architecture 관점에서의 재 분류

- Aggregate Root
  - Authentication: 사용자 인증 정보를 관리하는 토큰 형태로 구현
- Service
  - AuthenticationProvider: 인증 로직을 구현
- Endpoint
  - Filter
- Infra
  - SecurityFilterChain