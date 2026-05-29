package com.msa4meerkatgram.global.security.jwt;

import com.msa4meerkatgram.domain.user.entities.User;
import com.msa4meerkatgram.global.errors.custom.InvalidTokenException;
import com.msa4meerkatgram.global.security.cookie.CookieManager;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

// 자바가 알아서 인스턴스화
@Component
public class JwtProvider {
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    private final CookieManager cookieManager;

    public JwtProvider(JwtConfig jwtConfig, CookieManager cookieManager) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.secret()));
        this.cookieManager = cookieManager;
    }

    private String generateToken(User user, long ttl) {
        Date now = new Date();

        return Jwts.builder()
                .header() // 헤더 셋팅하겠다
                .type(jwtConfig.type())// 토큰 유형 설정
                .and() // 추가 연결
                .subject(String.valueOf(user.getId())) // subject: 유저를 특정하는 id 셋팅에 주로 사용
                .issuer(jwtConfig.issuer()) // 토큰 발급자
                .issuedAt(now) // 토큰 발급 시간
                .expiration(new Date(now.getTime() + ttl)) // 만료 시간
                .claim("role",user.getRole()) // private claim 설정
                .signWith(secretKey) // 시그니처 작성
                .compact();
    }

    // 외부에서 엑세스 토큰 필요시 이것을 호출
    public String generateAccessToken(User user) {
        return this.generateToken(user, jwtConfig.accessTokenExpiry());
    }

    // 외부에서 리프레시 토큰 필요시 이것을 호출
    public String generateRefreshToken(User user) {
        return this.generateToken(user, jwtConfig.refreshTokenExpiry());
    }




    // 쿠키에서 리프레시 토큰 추출
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return cookieManager.getCookie(request, jwtConfig.refreshTokenCookieName())
                .map(Cookie::getValue);
        // 쿠키에서 특정 리프레시 이름으로 추출
    }

    //토큰 검증 및 클레임 추출
    public Claims extractClaims(String token) {
        try {
            // 토큰의 유효성 체크와 함께 토큰 분해
            return Jwts.parser()
                    .verifyWith(this.secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // 토큰 만료 에러
            throw new InvalidTokenException("토큰이 만료됐습니다.");
        } catch (UnsupportedJwtException e) {
            // 시그니처가 잘못됨
            throw new InvalidTokenException("서명이 위조된 토큰입니다.");
        } catch (MalformedJwtException e) {
            // jwt 포맷이 이상하다 (토큰 형식이 올바르지 않을 때)
            throw new InvalidTokenException("토큰 형식이 올바르지 않습니다.");
        } catch (JwtException | IllegalArgumentException e) {
            // 위 에러 이외 에러 받겠다. | 내부 처리 할 때 이상 발생시 처리
            // 나머지 토큰 인정 관련 에러 다 받겠다.
            throw new InvalidTokenException("토큰 검증에 실패했습니다.");
        }
    }


}
