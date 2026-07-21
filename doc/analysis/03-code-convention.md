# 코드 컨벤션 분석

이 문서는 현재 프로젝트 코드에서 관찰되는 컨벤션을 정리합니다. 일부는 이미 잘 지켜지고 있고, 일부는 앞으로 팀 컨벤션으로 더 명확히 정하면 좋은 부분입니다.

## 1. 패키지 컨벤션

기본 패키지는 `com.msa4meerkatgram`입니다.

도메인 기능은 `domain.{기능명}` 아래에 위치합니다.

```text
domain/auth
domain/file
domain/post
domain/user
```

각 도메인은 역할별 하위 패키지를 사용합니다.

```text
controllers
services
repositories
entities
requests
responses
```

공통 기능은 `global` 아래에 둡니다.

```text
global/config
global/errors
global/responses
global/security
global/util
```

## 2. 클래스 네이밍

현재 네이밍은 역할이 드러나는 접미사를 사용합니다.

| 역할 | 접미사 | 예시 |
| --- | --- | --- |
| Controller | `Controller` | `AuthController` |
| Service | `Service` | `PostService` |
| Repository | `Repository` | `PostRepository` |
| Entity | 없음 또는 도메인명 | `User`, `Post` |
| Request DTO | `Req` | `LoginReq` |
| Response DTO | `Res` | `AuthRes` |
| 설정 | `Config`, `Configuration` | `JwtConfig`, `SecurityConfiguration` |
| 예외 | `Exception` | `InvalidTokenException` |

DTO 접미사로 `Req`, `Res`를 사용하고 있어 파일명이 짧고 일관됩니다.

## 3. 메서드 네이밍

현재 코드에서 자주 보이는 메서드 이름은 다음과 같습니다.

| 메서드 | 의미 |
| --- | --- |
| `login` | 로그인 처리 |
| `reissue` | 토큰 재발급 |
| `logout` | 로그아웃 |
| `registration` | 회원가입 |
| `index` | 목록 조회 |
| `show` | 단건 상세 조회 |
| `storeProfile` | 프로필 이미지 저장 |
| `storePosts` | 게시글 이미지 저장 |
| `from` | Entity를 Response DTO로 변환 |
| `success` | 성공 응답 생성 |

`index`, `show`는 REST API에서 자주 쓰는 표현입니다. 다만 팀원이 익숙하지 않다면 `getPosts`, `getPost`처럼 더 직관적인 이름도 고려할 수 있습니다.

## 4. 의존성 주입 컨벤션

대부분 생성자 주입을 사용합니다.

```java
@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
}
```

Lombok의 `@RequiredArgsConstructor`와 `final` 필드를 조합하는 방식입니다. Spring에서 권장되는 안정적인 패턴입니다.

## 5. 응답 컨벤션

모든 성공 응답은 `GlobalRes.success(...)`로 감싸는 흐름입니다.

```java
return ResponseEntity.ok(GlobalRes.success(data));
```

공통 응답 구조는 다음과 같습니다.

```json
{
  "code": "00",
  "message": "SUCCESS",
  "data": {}
}
```

실패 응답도 `CustomResponseCode`와 `GlobalExceptionHandler`를 통해 같은 모양으로 내려가도록 설계되어 있습니다.

## 6. 예외 처리 컨벤션

비즈니스 오류는 직접 정의한 커스텀 예외로 표현합니다.

예:

- `NotRegisteredException`
- `InvalidTokenException`
- `DuplicatedRecordException`
- `DeletedRecordException`
- `FileManagedException`

Controller나 Service에서 예외를 던지면 `GlobalExceptionHandler`가 잡아서 공통 응답으로 변환합니다.

이 방식의 장점은 각 Controller에서 반복적으로 try-catch를 작성하지 않아도 된다는 점입니다.

## 7. 검증 컨벤션

입력값 검증은 Request DTO에 선언합니다.

예:

- `@NotBlank`
- `@Pattern`
- `@AssertTrue`
- `@Min`

Controller에서는 `@Valid`를 사용해서 DTO 검증을 실행합니다.

```java
public ResponseEntity<GlobalRes<AuthRes>> login(
        @Valid @RequestBody LoginReq loginReq
)
```

## 8. Entity와 DTO 변환 컨벤션

Response DTO는 정적 팩토리 메서드 `from()`을 사용합니다.

```java
public static PostWithUserRes from(Post post) {
    return new PostWithUserRes(...);
}
```

이 컨벤션은 변환 로직을 DTO 내부에 모아두기 때문에 Controller와 Service가 비교적 깔끔해집니다.

## 9. 트랜잭션 컨벤션

데이터를 변경하는 인증 관련 서비스 메서드에는 `@Transactional(rollbackFor = Exception.class)`가 사용됩니다.

예:

- 로그인: Refresh Token 저장
- 토큰 재발급: Refresh Token 갱신
- 로그아웃: Refresh Token 제거
- 회원가입: 사용자 저장

게시글 조회 메서드에는 현재 별도 `@Transactional(readOnly = true)`가 없습니다. 조회 성능과 의도를 명확히 하려면 조회 서비스에 read-only 트랜잭션을 붙이는 것도 고려할 수 있습니다.

## 10. 현재 코드에서 정리하면 좋은 부분

### 인코딩 깨짐

README, 문서, 주석, Swagger 설명, validation message 일부가 깨져 보입니다. 팀 전체에서 UTF-8로 통일하는 것이 좋습니다.

### 주석 처리된 이전 코드

`AuthService`, `PostController`, `PostService` 등에 과거 MyBatis 또는 이전 구현으로 보이는 주석 코드가 남아 있습니다. 학습 목적이면 유지할 수 있지만, 운영 코드라면 별도 문서나 커밋 기록에 맡기고 정리하는 편이 좋습니다.

### 검증 오류 상세 응답

`GlobalExceptionHandler`에서 validation 오류 map을 만들지만 실제 응답 `data`에는 담지 않습니다. 프론트엔드에서 어떤 필드가 왜 틀렸는지 보여주려면 상세 오류를 `data`에 포함하는 방향을 검토할 수 있습니다.

### 메서드 이름 통일

파일 업로드에서 `storeProfile`, `storePosts`가 사용됩니다. `storePost`처럼 단수/복수 기준을 맞추면 더 일관적입니다.

### 도메인 책임 분리

`AuthRepository`가 `User` 엔티티를 직접 다룹니다. 인증 도메인에서 사용자 저장소를 별도로 쓰는 방식도 가능하지만, 장기적으로는 `UserRepository`로 사용자 조회/저장을 통합하고 `AuthService`가 이를 사용하는 구조가 더 명확할 수 있습니다.

