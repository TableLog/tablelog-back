package com.tablelog.tablelogback.domain.board.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_BOARD")
@Entity
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String title;
    @Column
    private String content;
    @Column
    private  String image_url;
    @Column
    private String category;
    @Column
    private String user;
    @Builder
    public Board(final String title, final String content,final String image_url, final String category, final String user) {
        this.title = title;
        this.content = content;
        this.image_url = image_url;
        this.category = category;
        this.user = user;
    }

    public void updateBoard(String title,String content,String image_url,String category ){
        this.title = title;
        this.content = content;
        this.image_url = image_url;
    }
}
