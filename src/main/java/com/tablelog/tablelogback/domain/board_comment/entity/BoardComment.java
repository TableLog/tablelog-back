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
    private String  board_id;
    @Column
    private String user;
    @Column
    private String content;

    @Builder
    public BoardComment(final String board_id, final String user, final String content) {
        this.board_id = board_id;
        this.user = user;
        this.content = content;
    }
}
