package com.msa4meerkatgram.domain.post.entities;

import com.msa4meerkatgram.domain.user.entities.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "posts")
@SQLDelete(sql = "UPDATE posts SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 연관 관계
    @JoinColumn(name = "user_id"
            , insertable = true // INSERT할 때, user 객체에 어떤 값을 넣더라도, INSERT문에 `user_id` 컬럼을 포함 하겠다.
            , updatable = false // UPDATE할 때 ~ 포함하지 않겠다.
            , nullable = false
            ,foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT) // 물리적 fk 생성하고 싶지 않을 때
    )// name = fk를 부여할 컬럼명
    private User user;

    @Column(name = "content", nullable = false, length = 200)
    private String content;

    @Column(name = "image", nullable = false, length = 100)
    private String image;

    @CreatedDate // 생성 시 자동으로 시간 입력
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // 수정 시 자동으로 시간 업데이트
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;
}
