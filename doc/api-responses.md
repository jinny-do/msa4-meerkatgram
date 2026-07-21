# Meerkatgram API Response Specification

Meerkatgram 프로젝트의 모든 API 응답 규격을 대분류(API), 중분류(HttpStatus), 소분류(응답 및 에러코드) 순으로 정리한 문서입니다.

모든 응답은 글로벌 공통 응답 DTO인 `GlobalRes` 규격을 따릅니다.
```json
{
  "code": "응답 코드 (성공: '00', 에러: 'E01'~'E99')",
  "message": "응답 메시지",
  "data": "응답 데이터 (성공 시 반환 객체, 에러 시 에러 상세 내용)"
}
```

---

## 1. 인증 API (AuthController)

### POST `/api/login` (로그인 처리)
* **200 OK**
  * **`00`** (로그인 완료): 로그인 성공 후 Access Token 및 유저 정보를 반환합니다.
    * Response Body 예시:
      ```json
      {
        "code": "00",
        "message": "로그인 완료",
        "data": {
          "id": 1,
          "email": "user@example.com",
          "nick": "meerkat",
          "profile": "http://localhost:8080/storage/profiles/...",
          "accessToken": "eyJhbGciOi...",
          "countPosts": 5
        }
      }
      ```
* **400 Bad Request**
  * **`E21`** (요청 파라미터에 이상이 있습니다): 이메일 형식이 잘못되었거나 비밀번호가 누락되는 등 validation 에러가 발생한 경우입니다.
    * Response Body 예시:
      ```json
      {
        "code": "E21",
        "message": "요청 파라미터에 이상이 있습니다.",
        "data": {
          "email": "올바른 이메일 형식이 아닙니다.",
          "password": "비밀번호는 필수 입력 항목입니다."
        }
      }
      ```
* **401 Unauthorized**
  * **`E01`** (로그인 에러): 이메일이 등록되어 있지 않거나 비밀번호가 일치하지 않는 경우입니다.
    * Response Body 예시:
      ```json
      {
        "code": "E01",
        "message": "로그인 에러",
        "data": "아이디와 비밀번호를 확인해주세요."
      }
      ```
* **500 Internal Server Error**
  * **`E80`** (DB 에러): 데이터베이스 조회 또는 저장 시 에러가 발생한 경우입니다.
  * **`E99`** (시스템 에러): 기타 예측 불가능한 서버 내부 에러가 발생한 경우입니다.

---

### POST `/api/reissue-token` (토큰 재발급)
* **200 OK**
  * **`00`** (토큰 재발급 완료): Refresh Token을 확인하고 새로운 Access Token을 생성해 반환합니다.
* **401 Unauthorized**
  * **`E04`** (토큰 이상): 쿠키에서 Refresh Token을 가져올 수 없거나, 유효하지 않은 회원의 토큰이거나, DB에 저장된 토큰과 일치하지 않는 경우입니다.
    * Response Body 예시:
      ```json
      {
        "code": "E04",
        "message": "토큰 이상",
        "data": "토큰이 없습니다." // 또는 "유효하지 않은 회원의 토큰입니다.", "토큰이 일치하지 않습니다."
      }
      ```
* **500 Internal Server Error**
  * **`E80`** (DB 에러)
  * **`E99`** (시스템 에러)

---

### POST `/api/logout` (로그아웃 처리)
* **200 OK**
  * **`00`** (로그아웃 완료): 유저의 Refresh Token을 DB에서 파기하고, 쿠키의 Refresh Token도 만료시킵니다.
* **401 Unauthorized**
  * **`E02`** (UNAUTHENTICATED_ERROR): 로그인 상태가 아니거나 인증 토큰(Access Token)이 만료/비정상인 상태로 요청을 시도한 경우 발생합니다.
    * Response Body 예시:
      ```json
      {
        "code": "E02",
        "message": "UNAUTHENTICATED_ERROR",
        "data": "로그인이 필요한 서비스입니다."
      }
      ```
  * **`E04`** (토큰 이상): 로그아웃 시 인증 토큰이 존재하지만 회원을 식별할 수 없는 경우 발생합니다.
* **500 Internal Server Error**
  * **`E80`** (DB 에러)
  * **`E99`** (시스템 에러)

---

### POST `/api/registration` (회원가입)
* **200 OK**
  * **`00`** (회원가입 완료): 회원가입 처리가 완료되었습니다.
* **400 Bad Request**
  * **`E21`** (요청 파라미터에 이상이 있습니다): 이메일, 비밀번호, 닉네임 등의 validation 검증을 통과하지 못한 경우 발생합니다.
* **409 Conflict**
  * **`E11`** (DUPLICATED_RECORD_ERROR): 이미 존재하는 이메일로 가입을 시도한 경우 발생합니다.
    * Response Body 예시:
      ```json
      {
        "code": "E11",
        "message": "DUPLICATED_RECORD_ERROR",
        "data": "이미 가입된 회원입니다."
      }
      ```
* **500 Internal Server Error**
  * **`E80`** (DB 에러)
  * **`E99`** (시스템 에러)

---

## 2. 파일 API (FileController)

### POST `/api/files/profiles` (프로필 이미지 업로드)
* **200 OK**
  * **`00`** (파일 저장 성공): 프로필 이미지를 성공적으로 업로드하고 URL을 반환합니다.
    * Response Body 예시:
      ```json
      {
        "code": "00",
        "message": "파일 저장 성공",
        "data": {
          "fileUri": "http://localhost:8080/storage/profiles/20260709_uuid.jpg"
        }
      }
      ```
* **500 Internal Server Error**
  * **`E40`** (파일 업로드 실패): 파일이 비어있거나, 파일명에 확장자가 없거나, 허용되지 않는 이미지 확장자(image/* 가 아님)이거나, 디렉토리 생성 실패 또는 파일 쓰기 에러가 발생한 경우입니다.
    * Response Body 예시:
      ```json
      {
        "code": "E40",
        "message": "파일 업로드 실패",
        "data": "파일 저장 실패: 허용하지 않는 파일 확장자"
      }
      ```
  * **`E99`** (시스템 에러)

---

### POST `/api/files/posts` (게시글 이미지 업로드)
* **200 OK**
  * **`00`** (파일 저장 성공): 게시글 이미지를 성공적으로 업로드하고 URL을 반환합니다.
    * Response Body 예시:
      ```json
      {
        "code": "00",
        "message": "파일 저장 성공",
        "data": {
          "fileUri": "http://localhost:8080/storage/posts/20260709_uuid.jpg"
        }
      }
      ```
* **500 Internal Server Error**
  * **`E40`** (파일 업로드 실패): (동일하게 파일 업로드 중 문제가 발생한 경우)
  * **`E99`** (시스템 에러)

---

## 3. 게시글 API (PostController)

### GET `/api/posts` (게시글 목록 조회 / 페이지네이션)
* **200 OK**
  * **`00`** (정상처리): 페이지네이션된 게시글 목록을 반환합니다.
    * Response Body 예시:
      ```json
      {
        "code": "00",
        "message": "정상처리",
        "data": {
          "total": 12,
          "lastPage": true,
          "posts": [
            {
              "id": 1,
              "content": "귀여운 미어캣입니다.",
              "image": "http://localhost:8080/storage/posts/meerkat.jpg",
              "user": {
                "id": 1,
                "email": "user@example.com",
                "nick": "meerkat",
                "profile": "http://localhost:8080/storage/profiles/profile.jpg"
              },
              "createdAt": "2026-07-09T14:00:00",
              "updatedAt": "2026-07-09T14:00:00"
            }
          ]
        }
      }
      ```
* **400 Bad Request**
  * **`E21`** (요청 파라미터에 이상이 있습니다): `page` 또는 `limit` 등 쿼리 파라미터의 타입이 맞지 않거나 검증 조건에 걸리는 경우 발생합니다.
* **500 Internal Server Error**
  * **`E80`** (DB 에러)
  * **`E99`** (시스템 에러)

---

### GET `/api/posts/{id}` (게시글 상세 조회)
* **200 OK**
  * **`00`** (게시글 상세 정상 처리): 특정 ID의 게시글 상세 정보와 작성자 정보를 반환합니다.
* **400 Bad Request**
  * **`E21`** (요청 파라미터에 이상이 있습니다): 경로 변수(PathVariable `{id}`)가 1보다 작거나(Min=1), 숫자가 아닌 형식이 입력될 때 발생합니다.
* **404 Not Found**
  * **`E10`** (DELETED_RECORD_ERROR): 조회하고자 하는 게시글이 삭제되었거나(deleted_at이 null이 아님) 존재하지 않는 경우 발생합니다.
    * Response Body 예시:
      ```json
      {
        "code": "E10",
        "message": "DELETED_RECORD_ERROR",
        "data": "이미 삭제된 게시글 입니다."
      }
      ```
* **500 Internal Server Error**
  * **`E80`** (DB 에러)
  * **`E99`** (시스템 에러)

---

## 4. 공통 인가 에러 (Spring Security Filter Level)
Spring Security Filter Chain에서 API 컨트롤러 호출 전에 발생하는 전역 인가 에러 응답 규격입니다.

* **403 Forbidden**
  * **`E03`** (UNAUTHORIZED_ERROR): 요청한 API에 접근하기 위한 리소스 권한이 부족할 경우 발생합니다.
    * Response Body 예시:
      ```json
      {
        "code": "E03",
        "message": "UNAUTHORIZED_ERROR",
        "data": "권한이 부족합니다."
      }
      ```
