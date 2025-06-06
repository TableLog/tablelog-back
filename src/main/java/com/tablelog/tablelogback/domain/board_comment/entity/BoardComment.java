package com.tablelog.tablelogback.domain.board_comment.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_BOARDCOMMENT")
@Entity
public class BoardComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String  boardId;
    @Column
    private String user;
    @Column
    private String content;
    @Column
    private Long comment_id;

    @Builder
    public BoardComment(final String boardId, final String user, final String content,final Long comment_id) {
        this.boardId = boardId;
        this.user = user;
        this.content = content;
        this.comment_id = comment_id;
    }

    public void update(String content){
        this.content = content;
    }
}
