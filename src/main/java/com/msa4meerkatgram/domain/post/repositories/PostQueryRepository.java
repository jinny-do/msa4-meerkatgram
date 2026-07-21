package com.msa4meerkatgram.domain.post.repositories;

import com.msa4meerkatgram.domain.post.entities.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.msa4meerkatgram.domain.post.entities.QPost.post;
import static com.msa4meerkatgram.domain.user.entities.QUser.user;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    // pagination
    // SELECT *
    // FROM posts
    //      JOIN users
    //          ON posts.user_id = users.id
    // WHERE deleted_at IS NULL
    // ORDER BY created_at DESC, id DESC
    // limit ?
    // offset ?;

    public List<Post> pagination(int offset, int limit){
        return jpaQueryFactory
                .selectFrom(post)
                .join(post.user, user).fetchJoin()
                .orderBy(post.createdAt.desc(), post.id.desc())
                .limit(limit)
                .offset(offset)
                .fetch(); // 쿼리를 만들어서 db로 보냄
    }
}
