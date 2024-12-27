package com.acorn.movielink.comunity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentDTO {

    private int commentId;
    private int postId;
    private int memId;
    private Integer parentId;  // 대댓글일 경우 부모 댓글의 ID
    private String content;
    private LocalDateTime commentRegAt;
    private LocalDateTime commentUpdatedAt;
    private LocalDateTime commentDeletedAt;
    private int commentLikeCnt;

    private List<CommentDTO> replies;  // 대댓글 목록을 저장할 필드

}
