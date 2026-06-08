package com.msa4meerkatgram.domain.post.services;

import com.msa4meerkatgram.domain.post.entities.Post;
import com.msa4meerkatgram.domain.post.mapper.PostMapper;
import com.msa4meerkatgram.domain.post.requests.PostCreateReq;
import com.msa4meerkatgram.domain.post.requests.PostIndexReq;
import com.msa4meerkatgram.domain.post.responses.PostIndexRes;
import com.msa4meerkatgram.domain.user.entities.User;
import com.msa4meerkatgram.domain.user.mapper.UserMapper;
import com.msa4meerkatgram.global.errors.custom.DeletedRecordException;
import com.msa4meerkatgram.global.errors.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final UserMapper userMapper;

    // 페이지 정보를 데이터베이스에서 가져와서 컨트롤러에 전달해주는 역할
    public PostIndexRes index(PostIndexReq postIndexReq) {
        // offset - 어디서 부터 보여줄 것인가
        int offset = (postIndexReq.page() -1) * postIndexReq.limit();

        // 특정 페이지 게시글 조회
        List<Post> posts = postMapper.getPagination(postIndexReq.limit(), offset);

        // 토탈 획득
        long total = postMapper.getTotal();
        boolean lastPage = offset + postIndexReq.limit() >= total;

        // 컨트롤러 전달
        return PostIndexRes.builder()
                .total(total)
                .lastPage(lastPage)
                .posts(posts)
                .build();
    }

    // 상세페이지
    public Post show(long id) {
        Post post = postMapper.findByPk(id);

        if(post == null) {
            throw new DeletedRecordException("이미 삭제된 게시글 입니다.");
        }

        return post;
    }

    // 게시물 작성
    public Post create(PostCreateReq postCreateReq, long id) {
        // 유저 정보 획득
        User user = userMapper.findByPk(id);

        if(user == null) {
            throw new UserNotFoundException("존재하지 않는 회원입니다.");
        }

        Post post = Post.builder()
                .content(postCreateReq.content())
                .image(postCreateReq.image())
                .userId(id)
                .build();

        postMapper.create(post);

        return post;
    }
}
