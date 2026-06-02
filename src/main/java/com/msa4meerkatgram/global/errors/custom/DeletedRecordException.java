package com.msa4meerkatgram.global.errors.custom;

public class DeletedRecordException extends RuntimeException {
    // 커스텀 에러는 주로 RuntimeException 상속받음
    public DeletedRecordException(String message) {
        super(message);
    }
}
