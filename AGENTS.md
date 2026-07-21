
# Meerkatgram project documentation

## Project introduction

Meerkatgram is a **community-type web application** where users communicate through image posts and comments.
Front-end and back-end are managed separately for each project And Repositories.

## Project Structure (프로젝트 구조)

Meerkatgram 백엔드 애플리케이션은 Spring Boot 3.5 기반으로 작성되었으며, 도메인 중심의 계층형 아키텍처(Domain-Driven Layered Architecture) 구조로 설계되어 있습니다. 전체 소스 코드는 `com.msa4meerkatgram` 패키지 아래에 위치하며 크게 `domain`과 `global` 패키지로 분리되어 있습니다.

### 디렉토리 구조 (Directory Structure)

```text
src/main/java/com/msa4meerkatgram
├── domain                  # 비즈니스 도메인별 패키지
│   ├── auth                # 인증 및 로그인 관련 도메인
│   ├── file                # 파일 업로드 및 관리 도메인
│   ├── post                # 게시글 관련 도메인
│   └── user                # 회원 관리 도메인
└── global                  # 공통/전역 설정 및 유틸리티 패키지
    ├── config              # MVC, CORS, JPA/QueryDSL 등 애플리케이션 설정
    ├── errors              # 전역 예외 처리 및 에러 응답 정의
    ├── responses           # 표준 API 응답 DTO 정의
    ├── security            # Spring Security 및 JWT 필터/인증 관리
    └── util                # 공통 유틸리티 (파일 처리 등)
```

---

### 각 도메인 레이어의 역할 (Domain Layer Roles)

각 도메인 패키지(`auth`, `file`, `post`, `user`) 내부는 역할에 따라 다음과 같은 서브 패키지로 나뉘어 동작합니다:

1. **`controllers` (표현 계층 / Presentation Layer)**
   - **역할**: 클라이언트의 HTTP 요청(GET, POST, DELETE 등)을 수신하고, 서비스 레이어를 호출하여 요청을 처리한 뒤 HTTP 응답을 반환합니다.
   - **어노테이션/구성 요소**: 주로 `@RestController`, `@RequestMapping`, `@PostMapping`, `@GetMapping` 등을 사용합니다.

2. **`services` (비즈니스 로직 계층 / Service Layer)**
   - **역할**: 애플리케이션의 비즈니스 로직을 구현합니다. 레포지토리를 통해 데이터를 조회/저장하고 이를 가공하는 핵심 정책과 흐름을 조율합니다.
   - **어노테이션/구성 요소**: 주로 `@Service`, `@Transactional` 등을 사용합니다.

3. **`entities` (도메인 모델 계층 / Domain Model Layer)**
   - **역할**: 실제 데이터베이스 테이블과 매핑되는 JPA 엔티티 클래스들입니다. 데이터베이스 스키마 정의 및 영속성 상태 관리에 사용됩니다.
   - **어노테이션/구성 요소**: 주로 `@Entity`, `@Table`, `@Id`, `@Column` 등을 사용합니다.

4. **`repositories` (데이터 액세스 계층 / Data Access Layer)**
   - **역할**: 데이터베이스와 상호작용하여 데이터를 CRUD하는 영속성 계층 인터페이스 및 구현체입니다. Spring Data JPA의 `JpaRepository`와 동적 쿼리를 위한 QueryDSL `JPAQueryFactory`를 활용합니다.
   - **어노테이션/구성 요소**: 주로 `@Repository` 등을 사용합니다. (예: `PostRepository`, `PostQueryRepository`)

5. **`requests` / `responses` (데이터 전송 객체 / DTO Layer)**
   - **역할**: 계층 간 데이터 교환 시 사용되는 객체들입니다.
     - **`requests`**: 클라이언트가 전송하는 HTTP 요청 본문(JSON) 또는 쿼리 파라미터를 담는 객체입니다.
     - **`responses`**: 서비스 레이어의 처리 결과를 클라이언트에게 전달하기 위해 알맞은 형태로 가공한 응답 객체입니다.

---

### 글로벌 패키지의 역할 (Global Layer Roles)

도메인에 종속되지 않고 애플리케이션 전반에 걸쳐 공통으로 사용되는 인프라 및 지원 성격의 패키지들입니다:

- **`global/config`**: 애플리케이션 전역 설정 클래스들이 위치합니다. CORS 설정(`CorsConfig`), Spring MVC 인터셉터/정적 리소스 설정(`WebConfig`), QueryDSL Bean 등록 및 JPA 설정(`jpa/QueryDSLConfig`) 등이 포함됩니다.
- **`global/errors`**: 예외 처리 로직이 위치합니다. `@RestControllerAdvice` 기반의 `GlobalExceptionHandler`를 통해 애플리케이션 전역에서 발생하는 예외를 처리하여 클라이언트에게 표준 규격의 에러 응답을 반환합니다.
- **`global/responses`**: 모든 API 응답의 규격을 일원화하기 위한 공통 응답 DTO(`GlobalRes` 등)가 위치합니다.
- **`global/security`**: Spring Security 설정 및 사용자 인증/인가 관련 로직이 위치합니다. JWT 발급, 검증, 쿠키 관리 및 Security Filter Chain 설정이 구현되어 있습니다.
- **`global/util`**: 특정 도메인에 국한되지 않는 헬퍼 유틸리티 모음입니다. 대표적으로 파일 업로드/삭제 처리를 돕는 파일 유틸리티(`file`) 등이 있습니다.

# Caution

Behavioral guidelines to reduce common LLM coding mistakes. Merge with project-specific instructions as needed.

**Tradeoff:** These guidelines bias toward caution over speed. For trivial tasks, use judgment.

## 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

## 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:
- Remove imports/variables/functions that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.

---

**These guidelines are working if:** fewer unnecessary changes in diffs, fewer rewrites due to overcomplication, and clarifying questions come before imp