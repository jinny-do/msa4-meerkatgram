# 프로젝트 아키텍처 분석

## 1. 프로젝트 성격

Meerkatgram은 이미지 게시글을 중심으로 사용자들이 소통하는 커뮤니티형 백엔드 API 서버입니다. 프론트엔드와 백엔드를 분리해서 운영하는 구조이며, 이 저장소는 Spring Boot 기반 백엔드 애플리케이션입니다.

클라이언트는 `/api`로 시작하는 HTTP API를 호출하고, 서버는 데이터베이스 조회/저장, 인증 토큰 발급, 파일 저장, 공통 응답 변환을 처리합니다.

## 2. 사용 기술

현재 `build.gradle` 기준 주요 기술은 다음과 같습니다.

| 영역 | 기술 |
| --- | --- |
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 3.5.x |
| 웹 API | Spring Web |
| 보안 | Spring Security, JWT |
| 데이터 접근 | Spring Data JPA, QueryDSL |
| 데이터베이스 | MySQL |
| 검증 | Jakarta Validation |
| 문서화 | SpringDoc OpenAPI |
| 빌드 | Gradle |
| 보조 | Lombok |

## 3. 큰 구조

소스 코드는 크게 `domain`과 `global`로 나뉩니다.

```text
com.msa4meerkatgram
├── domain
│   ├── auth
│   ├── file
│   ├── post
│   └── user
└── global
    ├── annotations
    ├── config
    ├── errors
    ├── responses
    ├── security
    └── util
```

`domain`은 실제 비즈니스 기능을 담당합니다. 예를 들어 로그인은 `auth`, 게시글 조회는 `post`, 파일 업로드는 `file`에 있습니다.

`global`은 여러 기능에서 공통으로 쓰는 설정과 도구를 담당합니다. 예를 들어 JWT 인증 필터, 공통 응답 형식, 전역 예외 처리, 파일 저장 유틸리티가 이곳에 있습니다.

## 4. 아키텍처 특징

### 도메인 중심 패키지 구조

기능별로 패키지를 먼저 나누고, 각 기능 안에 `controllers`, `services`, `repositories`, `entities`, `requests`, `responses`를 배치하는 방식입니다.

예를 들어 게시글 기능은 다음처럼 구성됩니다.

```text
domain/post
├── controllers
├── entities
├── repositories
├── requests
├── responses
└── services
```

이 구조는 기능 단위로 코드를 찾기 쉽다는 장점이 있습니다.

### 계층형 흐름

요청은 대체로 다음 순서로 이동합니다.

```text
Client
 -> Controller
 -> Service
 -> Repository
 -> Database
```

응답은 반대로 돌아옵니다.

```text
Database
 -> Entity
 -> Response DTO
 -> GlobalRes
 -> Client
```

### 공통 응답 포맷

모든 API 응답은 `GlobalRes<T>`로 감싸는 구조입니다.

```json
{
  "code": "00",
  "message": "SUCCESS",
  "data": {}
}
```

성공/실패 코드는 `CustomResponseCode` enum에서 관리합니다.

### JWT 기반 인증

로그인 시 Access Token과 Refresh Token을 발급합니다.

- Access Token: API 요청 시 `Authorization` 헤더로 전달
- Refresh Token: 쿠키와 DB에 저장
- 재발급 시 쿠키의 Refresh Token과 DB의 Refresh Token을 비교

서버는 세션을 사용하지 않는 `STATELESS` 방식으로 설정되어 있습니다.

### 소프트 삭제

`User`, `Post` 엔티티는 `deleted_at` 컬럼을 사용합니다.

삭제 시 실제 행을 바로 지우는 대신 `deleted_at`에 시간이 기록되도록 `@SQLDelete`가 설정되어 있습니다. 조회 시에는 `@SQLRestriction("deleted_at IS NULL")` 때문에 삭제되지 않은 데이터만 기본 조회됩니다.

## 5. 주요 모듈별 책임

| 모듈 | 책임 |
| --- | --- |
| `domain.auth` | 회원가입, 로그인, 토큰 재발급, 로그아웃 |
| `domain.post` | 게시글 목록 조회, 게시글 상세 조회 |
| `domain.file` | 프로필/게시글 이미지 저장 |
| `domain.user` | 사용자 도메인 기반 클래스 존재, 현재 API 기능은 미구현 |
| `global.security` | 보안 설정, JWT 생성/검증, 인증 필터 |
| `global.errors` | 전역 예외 처리 |
| `global.responses` | 공통 응답 구조 |
| `global.config` | CORS, 정적 파일 경로, QueryDSL 설정 |
| `global.util.file` | 로컬 파일 저장 처리 |

## 6. 현재 구조상 주의할 점

- README와 일부 주석/Swagger 문구는 인코딩이 깨져 보입니다. 협업 문서와 API 설명을 다시 저장하거나 인코딩을 통일할 필요가 있습니다.
- `PostController`에는 작성/삭제 코드가 주석으로 남아 있습니다. 현재 실제 API로 동작하는 것은 목록/상세 조회입니다.
- `UserController`, `UserService`는 아직 실질 기능이 거의 없습니다.
- `GlobalExceptionHandler`의 검증 오류 처리에서 필드별 상세 오류를 만들지만, 현재 응답에는 상세 오류 map이 담기지 않습니다.
- 파일 업로드는 확장자를 기준으로 허용 여부를 판단합니다. 실제 MIME 검증이나 파일 크기 제한은 Spring multipart 설정에 의존합니다.

