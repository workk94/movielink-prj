package com.acorn.movielink.comunity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentDTO {

    private Integer commentId;
    private Integer postId;
    private Integer memId;
    private Integer parentId;  // 대댓글일 경우 부모 댓글의 ID
    private String content;
    private Date commentRegAt;
    private Date commentUpdatedAt;
    private Date commentDeletedAt;
    private int commentLikeCnt;

    // 조회 시 표시할 최종 시간 (등록/수정/삭제 중 하나)
    private Date displayTime;

    // 작성자 닉네임 (조회 전용)
    private String memNn;

    private List<CommentDTO> replies;  // 대댓글 목록을 저장할 필드

}
