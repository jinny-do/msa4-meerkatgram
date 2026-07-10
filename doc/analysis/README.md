# Meerkatgram 프로젝트 분석

이 폴더는 Meerkatgram 백엔드 프로젝트를 이해하기 위한 분석 문서 모음입니다.

현재 프로젝트는 Spring Boot 기반의 이미지 커뮤니티 API 서버입니다. 사용자는 회원가입과 로그인을 할 수 있고, JWT 토큰으로 인증을 유지하며, 게시글 목록/상세 조회와 이미지 파일 업로드 기능을 제공합니다.

## 문서 목록

| 문서 | 설명 |
| --- | --- |
| [01-architecture.md](./01-architecture.md) | 프로젝트 전체 아키텍처와 주요 기술 구성 |
| [02-layer-analysis.md](./02-layer-analysis.md) | Controller, Service, Repository, Entity, DTO 레이어 분석 |
| [03-code-convention.md](./03-code-convention.md) | 현재 코드에서 보이는 네이밍, 패키지, 응답, 예외 처리 컨벤션 |
| [04-non-developer-flow.md](./04-non-developer-flow.md) | 비전공자도 이해할 수 있는 서비스 동작 흐름 |

## 현재 구현 범위 요약

현재 코드 기준으로 확인되는 주요 기능은 다음과 같습니다.

- 인증: 회원가입, 로그인, 토큰 재발급, 로그아웃
- 게시글: 게시글 목록 조회, 게시글 상세 조회
- 파일: 프로필 이미지 업로드, 게시글 이미지 업로드
- 공통 처리: JWT 인증 필터, Spring Security 설정, CORS 설정, 공통 응답 포맷, 전역 예외 처리

`user` 도메인은 패키지와 기본 클래스는 존재하지만, 현재 별도의 사용자 조회 API는 구현되어 있지 않습니다.

