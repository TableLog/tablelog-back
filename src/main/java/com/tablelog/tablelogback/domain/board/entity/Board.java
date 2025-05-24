package com.tablelog.tablelogback.domain.board.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    @Column(length = 500)
    private String content;
    @Column
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> image_urls;
    @Column
    private String category;
    @Column
    private String user;
    @Builder
    public Board(final String title, final String content,final List<String> image_urls, final String category, final String user) {
        this.title = title;
        this.content = content;
        this.image_urls = image_urls;
        this.category = category;
        this.user = user;
    }

    public void updateBoard(String title, String content, List<String> image_urls, String category) {
        this.title = title;
        this.content = content;
        if (this.image_urls == null) {
            this.image_urls = new ArrayList<>();
        }
        this.image_urls.clear();
        if (image_urls != null) {
            this.image_urls.addAll(image_urls);
        }
        this.category = category;
    }
}
