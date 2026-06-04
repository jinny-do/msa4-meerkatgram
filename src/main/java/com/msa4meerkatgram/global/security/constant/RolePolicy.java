package com.msa4meerkatgram.global.security.constant;

import lombok.Getter;

// 상수로써 다룰 것이기 때문에 enum으로 만듦
@Getter
public enum RolePolicy {
    NORMAL("NORMAL")
    ,SUPER("SUPER");

    // provider : 문자열 저장할 필드
    private final String role;

    RolePolicy(String role) {
        this.role = role;
    }

}
