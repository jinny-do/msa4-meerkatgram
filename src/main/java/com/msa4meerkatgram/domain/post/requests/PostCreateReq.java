package com.msa4meerkatgram.domain.post.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PostCreateReq(
        @NotBlank(message = "게시물 작성은 필수입니다.")
        @Pattern(regexp = "^.{10,1000}$", message = "글자 수는 10자 이상, 1000자 이하로 작성해주세요.")
        String content,
        @NotBlank(message = "게시물 사진은 필수입니다.")
        String image
) {
}
