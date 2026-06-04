package com.msa4meerkatgram.global.security.constant;

import lombok.Getter;

// 상수로써 다룰 것이기 때문에 enum으로 만듦
@Getter
public enum ProviderPolicy {
    NONE("NONE")
    ,KAKAO("KAKAO")
    ,GOOGLE("GOOGLE");

    // provider : 문자열 저장할 필드
    private final String provider;

    ProviderPolicy(String provider) {
        this.provider = provider;
    }

}
