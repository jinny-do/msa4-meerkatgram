package com.msa4meerkatgram.global.responses;

import com.msa4meerkatgram.global.responses.constant.CustomResponseCode;

public record GlobalRes<T> (
        String code
        , String message
        , T data
){
    public static <T> GlobalRes<T> from(CustomResponseCode customResponseCode, T data){
        return new GlobalRes<T>(customResponseCode.getCode(), customResponseCode.name(), data);
    }

    // data가 null인 경우
    public static GlobalRes<Void> from( CustomResponseCode customResponseCode){
        return new GlobalRes<Void>(customResponseCode.getCode(), customResponseCode.name(), null);
    }

    // SUCCESS
    public static <T> GlobalRes<T> success(T data){
        return GlobalRes.<T>from(CustomResponseCode.SUCCESS, data);
        // return new GlobalRes<T>(CustomResponseCode.SUCCESS.getCode(), CustomResponseCode.SUCCESS.name(), data);
    }

    // data가 없는 success 패턴
    public static GlobalRes<Void> success(){
        return GlobalRes.<Void>from(CustomResponseCode.SUCCESS);
        // return new GlobalRes<Void>(CustomResponseCode.SUCCESS.getCode(), CustomResponseCode.SUCCESS.name(), null);
    }
}
