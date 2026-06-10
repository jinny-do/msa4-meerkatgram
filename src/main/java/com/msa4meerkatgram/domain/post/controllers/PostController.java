package com.msa4meerkatgram.domain.post.controllers;

import com.msa4meerkatgram.domain.post.entities.Post;
import com.msa4meerkatgram.domain.post.requests.PostCreateReq;
import com.msa4meerkatgram.domain.post.requests.PostIndexReq;
import com.msa4meerkatgram.domain.post.responses.PostIndexRes;
import com.msa4meerkatgram.domain.post.services.PostService;
import com.msa4meerkatgram.global.responses.GlobalRes;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// 필드만 정의해도 해당하는 생성자를 생성해주는 것
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PostController {
    private final PostService postService;


    @GetMapping("/posts")
    public ResponseEntity<GlobalRes<PostIndexRes>> index(PostIndexReq postIndexReq) {
        PostIndexRes postIndexRes = postService.index(postIndexReq);


        return ResponseEntity.status(200).body(
                GlobalRes.<PostIndexRes>builder()
                        .code("00")
                        .message("정상처리")
                        .data(postIndexRes)
                        .build()
        );
    }

    // 게시물 상세 조회
    @GetMapping("/posts/{id}")
    public ResponseEntity<GlobalRes<Post>> show(
            @Min(value=1, message = "1이상 숫자만 허용합니다.") @PathVariable long id
    ) {
        Post result = postService.show(id);

        return ResponseEntity.status(200).body(
                GlobalRes.<Post>builder()
                        .code("00")
                        .message("게시글 상세 정상 처리")
                        .data(result)
                        .build()
        );
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<GlobalRes<String>> delete(
            @PathVariable long id,
            @AuthenticationPrincipal Claims claims
    ){
        long userId = Long.parseLong(claims.getSubject());
        postService.delete(id, userId); // service의 반환값 여부에 따라

        return ResponseEntity.status(200).body(
                GlobalRes.<String>builder()
                        .code("00")
                        .message("게시글 삭제 완료")
                        .build()
        );


    }


    // 게시물 작성
    @PostMapping("/posts/create")
    public ResponseEntity<GlobalRes<Post>> create(
            @Valid @RequestBody PostCreateReq req,
            @AuthenticationPrincipal Claims claims
    ) {
        long userId = Long.parseLong(claims.getSubject());

        Post post = postService.create(req, userId);

        return ResponseEntity.status(200).body(
                GlobalRes.<Post>builder()
                        .code("00")
                        .message("게시물 작성 완료")
                        .data(post)
                        .build()
        );
    }
}
