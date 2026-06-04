package com.msa4meerkatgram.global.errors.custom;

// 중복된 데이터가 들어가면 안 됨
public class DuplicatedRecordException extends RuntimeException {
    // 커스텀 에러는 주로 RuntimeException 상속받음
    public DuplicatedRecordException(String message) {
        super(message);
    }
}
