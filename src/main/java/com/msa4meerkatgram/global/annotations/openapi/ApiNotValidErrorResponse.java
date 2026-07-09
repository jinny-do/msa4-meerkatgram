package com.msa4meerkatgram.global.annotations.openapi;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 커스텀 어노테이션 만들 때 필수
// 어노테이션 레벨 설정 (메소드 레벨로 설정)
@Target(ElementType.METHOD)
// 어노테이션이 동작할 때 어느 시점에 동작하게 할 것이냐 (프로그램이 실행되고 있는 단계에서 실행되게)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "400"
        ,description = "유효성 검사 실패"
        ,content = @Content(
        mediaType = "application/json"
        , examples = {
            @ExampleObject(
                    name = "유효성 검사 실패 에러"
                    , value = "{\"code\":\"E21\", \"message\":\"Bad Request\"}"
            )
        }
    )
)
public @interface ApiNotValidErrorResponse {
}
