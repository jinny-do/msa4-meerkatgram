package com.msa4meerkatgram.domain.post.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PostCreateReq(
        @Schema(description = "게시글 내용", example = "내용입니다.",nullable = false, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "게시물 작성은 필수입니다.")
        @Pattern(regexp = "^.{1,1000}$", message = "글자 수는 1자 이상, 1000자 이하로 작성해주세요.")
        String content,

        @Schema(description = "게시글 이미지", example = "http://localhost:8080/files/posts/4ewerd",nullable = false, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @NotBlank(message = "게시물 사진은 필수입니다.")
        String image
) {
}
