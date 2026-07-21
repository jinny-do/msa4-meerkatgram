# 레이어 분석

## 1. 전체 레이어 흐름

현재 프로젝트는 일반적인 Spring 백엔드 계층 구조를 따릅니다.

```text
Controller
 -> Service
 -> Repository
 -> Entity / Database
```

그리고 요청과 응답 데이터는 DTO로 분리합니다.

```text
Request DTO -> Service 처리 -> Entity 조회/저장 -> Response DTO
```

## 2. Controller 레이어

Controller는 외부 HTTP 요청을 받는 입구입니다. URL, HTTP 메서드, 요청 데이터 검증, 응답 포맷 생성을 담당합니다.

대표 클래스:

- `AuthController`
- `PostController`
- `FileController`
- `UserController`

예시 흐름:

```text
POST /api/login
 -> AuthController.login()
 -> AuthService.login()
 -> GlobalRes.success(...)
```

Controller의 특징은 다음과 같습니다.

- `@RestController`를 사용합니다.
- 공통 경로는 대체로 `@RequestMapping("/api")`입니다.
- 성공 응답은 `ResponseEntity.ok(GlobalRes.success(...))` 형태로 반환합니다.
- 요청 검증은 `@Valid`, `@Min`, `@NotBlank`, `@Pattern` 등을 사용합니다.

Controller는 비즈니스 로직을 직접 처리하지 않고 Service로 넘기는 역할에 집중하고 있습니다.

## 3. Service 레이어

Service는 실제 업무 규칙을 처리하는 계층입니다.

대표 클래스:

- `AuthService`
- `PostService`
- `FileService`
- `UserService`

### AuthService

회원 인증 관련 핵심 로직을 담당합니다.

- 로그인 시 이메일로 사용자 조회
- 비밀번호 BCrypt 검증
- Access Token, Refresh Token 생성
- Refresh Token DB 저장
- Refresh Token 쿠키 저장
- 토큰 재발급 시 쿠키 토큰과 DB 토큰 비교
- 로그아웃 시 Refresh Token 삭제
- 회원가입 시 이메일 중복 확인과 비밀번호 암호화

### PostService

게시글 조회 로직을 담당합니다.

- 페이지 번호와 limit으로 offset 계산
- QueryDSL 저장소로 게시글 목록 조회
- 전체 게시글 수 조회
- 마지막 페이지 여부 계산
- 게시글 상세 조회

### FileService

파일 업로드 흐름을 담당합니다.

- 파일 저장 경로 생성
- 로컬 디스크에 파일 저장
- 클라이언트가 접근할 수 있는 URL 반환

## 4. Repository 레이어

Repository는 데이터베이스 접근을 담당합니다.

대표 클래스:

- `AuthRepository`
- `UserRepository`
- `PostRepository`
- `PostQueryRepository`

### Spring Data JPA Repository

`AuthRepository`, `UserRepository`, `PostRepository`는 `JpaRepository`를 상속합니다. 기본 CRUD를 자동으로 사용할 수 있고, 메서드 이름으로 쿼리를 만들 수 있습니다.

예:

```java
Optional<User> findByEmail(String email);
boolean existsByEmail(String email);
long countByUser(User user);
```

### QueryDSL Repository

`PostQueryRepository`는 복잡한 조회를 담당합니다. 게시글 목록 조회에서 사용자 정보를 함께 가져오기 위해 `fetchJoin()`을 사용합니다.

```text
posts 조회
 -> user join
 -> createdAt desc, id desc 정렬
 -> limit, offset 적용
```

## 5. Entity 레이어

Entity는 데이터베이스 테이블과 연결되는 객체입니다.

대표 클래스:

- `User`
- `Post`

### User

`users` 테이블에 대응합니다.

주요 필드:

- `id`
- `email`
- `password`
- `nick`
- `provider`
- `role`
- `profile`
- `refreshToken`
- `createdAt`
- `updatedAt`
- `deletedAt`

### Post

`posts` 테이블에 대응합니다.

주요 필드:

- `id`
- `user`
- `content`
- `image`
- `createdAt`
- `updatedAt`
- `deletedAt`

`Post`는 `User`와 `ManyToOne` 관계입니다. 즉, 게시글 하나는 작성자 한 명과 연결됩니다.

## 6. DTO 레이어

DTO는 API 요청/응답에 사용하는 데이터 모양입니다.

### Request DTO

대표 클래스:

- `LoginReq`
- `RegistrationReq`
- `PostIndexReq`
- `PostCreateReq`

Request DTO에는 검증 규칙이 붙어 있습니다.

- 이메일 형식
- 비밀번호 형식
- 닉네임 형식
- 페이지 번호 최소값

### Response DTO

대표 클래스:

- `AuthRes`
- `FileRes`
- `PostIndexRes`
- `PostWithUserRes`
- `UserRes`

Entity를 그대로 응답하지 않고 Response DTO로 변환합니다. 이 방식은 비밀번호나 Refresh Token 같은 민감한 값이 API 응답에 노출되는 일을 막는 데 도움이 됩니다.

## 7. Global 레이어

`global`은 특정 도메인 하나에만 속하지 않는 공통 기능입니다.

### security

Spring Security와 JWT 인증을 담당합니다.

- `SecurityConfiguration`: 보안 정책 설정
- `TokenAuthenticationFilter`: 요청마다 Access Token 검사
- `JwtProvider`: 토큰 생성/추출/검증
- `CookieManager`: Refresh Token 쿠키 처리

### errors

예외를 공통 응답으로 바꿉니다.

- `GlobalExceptionHandler`
- `NotRegisteredException`
- `InvalidTokenException`
- `DuplicatedRecordException`
- `FileManagedException`

### responses

API 응답 포맷과 응답 코드를 관리합니다.

- `GlobalRes`
- `CustomResponseCode`

### config

애플리케이션 설정을 담당합니다.

- CORS 설정
- 파일 정적 리소스 매핑
- QueryDSL 설정
- OpenAPI 설정

## 8. 요청별 레이어 흐름 예시

### 로그인

```text
AuthController.login()
 -> AuthService.login()
 -> AuthRepository.findByEmail()
 -> PasswordEncoder.matches()
 -> JwtProvider.generateAccessToken()
 -> JwtProvider.generateRefreshToken()
 -> AuthRepository.save()
 -> CookieManager.setCookie()
 -> AuthRes.from()
 -> GlobalRes.success()
```

### 게시글 목록 조회

```text
PostController.index()
 -> PostService.index()
 -> PostQueryRepository.pagination()
 -> PostRepository.count()
 -> PostIndexRes.from()
 -> GlobalRes.success()
```

### 파일 업로드

```text
FileController.storePosts()
 -> FileService.storePosts()
 -> LocalFileManager.generatePostPath()
 -> LocalFileManager.saveFile()
 -> FileRes
 -> GlobalRes.success()
```

