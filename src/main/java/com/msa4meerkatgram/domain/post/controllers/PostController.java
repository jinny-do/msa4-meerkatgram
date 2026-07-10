package com.msa4meerkatgram.domain.post.controllers;

import com.msa4meerkatgram.domain.post.requests.PostIndexReq;
import com.msa4meerkatgram.domain.post.responses.PostIndexRes;
import com.msa4meerkatgram.domain.post.responses.PostWithUserRes;
import com.msa4meerkatgram.domain.post.services.PostService;
import com.msa4meerkatgram.global.config.openapi.CustomApiResponse;
import com.msa4meerkatgram.global.responses.GlobalRes;
import com.msa4meerkatgram.global.responses.constant.CustomResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "게시글 API", description = "게시글 관련")
// 필드만 정의해도 해당하는 생성자를 생성해주는 것
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PostController {
    private final PostService postService;

    // 페이지네이션
    @Operation(summary = "게시글 목록 획득 처리")
    @CustomApiResponse(value = {
            CustomResponseCode.INVALID_PARAMETER_ERROR
            ,CustomResponseCode.DB_ERROR
            ,CustomResponseCode.SYSTEM_ERROR
    })
    @GetMapping("/posts")
    public ResponseEntity<GlobalRes<PostIndexRes>> index(PostIndexReq postIndexReq) {

        return ResponseEntity.ok(GlobalRes.success(postService.index(postIndexReq)));

    }

    // 게시물 상세 조회
    @Operation(summary = "게시글 상세 조회 처리")
    @CustomApiResponse(value = {
            CustomResponseCode.INVALID_PARAMETER_ERROR
            ,CustomResponseCode.UNAUTHENTICATED_ERROR
            ,CustomResponseCode.INVALID_TOKEN_ERROR
            ,CustomResponseCode.DB_ERROR
            ,CustomResponseCode.SYSTEM_ERROR
    })
    @GetMapping("/posts/{id}")
    public ResponseEntity<GlobalRes<PostWithUserRes>> show(
           @Parameter(description = "게시글 번호", example = "1") @Min(value=1, message = "1이상 숫자만 허용합니다.") @PathVariable long id
    ) {

        return ResponseEntity.ok(GlobalRes.success(postService.show(id)));

    }

    // 게시글 삭제
    // @DeleteMapping("/posts/{id}")
    // public ResponseEntity<GlobalRes<String>> delete(
    //         @PathVariable long id,
    //         @AuthenticationPrincipal Claims claims
    // ){
    //     long userId = Long.parseLong(claims.getSubject());
    //     postService.delete(id, userId); // service의 반환값 여부에 따라
    //
    //     return ResponseEntity.status(200).body(
    //             GlobalRes.<String>builder()
    //                     .code("00")
    //                     .message("게시글 삭제 완료")
    //                     .build()
    //     );
    //
    //
    // }
    //
    //
    // // 게시물 작성
    // @PostMapping("/posts/create")
    // public ResponseEntity<GlobalRes<PostMybatis>> create(
    //         @Valid @RequestBody PostCreateReq req,
    //         @AuthenticationPrincipal Claims claims
    // ) {
    //     long userId = Long.parseLong(claims.getSubject());
    //
    //     PostMybatis post = postService.create(req, userId);
    //
    //     return ResponseEntity.status(200).body(
    //             GlobalRes.<PostMybatis>builder()
    //                     .code("00")
    //                     .message("게시물 작성 완료")
    //                     .data(post)
    //                     .build()
    //     );
    // }
}
