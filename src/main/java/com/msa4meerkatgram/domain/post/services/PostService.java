package com.msa4meerkatgram.domain.post.services;

import com.msa4meerkatgram.domain.post.entities.Post;
import com.msa4meerkatgram.domain.post.repositories.PostQueryRepository;
import com.msa4meerkatgram.domain.post.repositories.PostRepository;
import com.msa4meerkatgram.domain.post.requests.PostIndexReq;
import com.msa4meerkatgram.domain.post.responses.PostIndexRes;
import com.msa4meerkatgram.domain.post.responses.PostWithUserRes;
import com.msa4meerkatgram.global.errors.custom.DeletedRecordException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;

    // 페이지 정보를 데이터베이스에서 가져와서 컨트롤러에 전달해주는 역할
    public PostIndexRes index(PostIndexReq postIndexReq) {
        // offset - 어디서 부터 보여줄 것인가
        int offset = (postIndexReq.page() -1) * postIndexReq.limit();

        // 특정 페이지 게시글 조회
        // List<PostMybatis> posts = postMapper.getPagination(postIndexReq.limit(), offset);
        List<Post> result = postQueryRepository.pagination(offset, postIndexReq.limit());

        // 토탈 획득
        // long total = postMapper.getTotal();
        long total = postRepository.count();

        boolean lastPage = offset + postIndexReq.limit() >= total;

        // 컨트롤러 전달
        // return PostIndexRes.builder()
        //         .total(total)
        //         .lastPage(lastPage)
        //         .posts(result)
        //         .build();
        return PostIndexRes.from(total, lastPage, result);
    }

    // 상세페이지
    public PostWithUserRes show(long id) {
        Post result = postRepository.findById(id)
                .orElseThrow(()-> new DeletedRecordException("이미 삭제된 게시글 입니다."));


        return PostWithUserRes.from(result);
    }

    // 게시물 작성
    // public PostMybatis create(PostCreateReq postCreateReq, long id) {
    //     // 유저 정보 획득
    //     User user = userMapper.findByPk(id);
    //
    //     if(user == null) {
    //         throw new UserNotFoundException("존재하지 않는 회원입니다.");
    //     }
    //
    //     PostMybatis post = PostMybatis.builder()
    //             .content(postCreateReq.content())
    //             .image(postCreateReq.image())
    //             .userId(id)
    //             .build();
    //
    //     postMapper.create(post);
    //
    //     return post;
    // }
    //
    // // 게시글 삭제
    // public void delete(long postId, long userId) {
    //     User user = userMapper.findByPk(userId);
    //
    //     if(user == null) {
    //         throw new UserNotFoundException("존재하지 않는 회원입니다.");
    //     }
    //
    //     PostMybatis post = postMapper.findByPk(postId);
    //     if(post == null) {
    //         throw new PostNotFoundException("존재하지 않는 게시글입니다.");
    //     }
    //
    //     if(post.getUserId() != userId) {
    //         throw new ForbiddenException("게시글 삭제 권한이 없습니다.");
    //     }
    //
    //
    //     // 게시글 번호를 가지고 DB에 삭제 쿼리 날리는
    //     // 성공시 1, 실패시 0
    //     int result = postMapper.deleteByPk(postId);
    //
    //     if(result == 0) {
    //         throw new PostNotFoundException("게시글 삭제에 실패했습니다.");
    //     }
    //
    // }
}
