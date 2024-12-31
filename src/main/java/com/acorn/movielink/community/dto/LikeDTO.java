package com.acorn.movielink.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LikeDTO {
    private Integer likeId;
    private String targetType;
    private Integer targetId;
    private int memId;
    private LocalDateTime createdAt;


    private int likeCount;
    // 생성자 추가: targetType, targetId, mem_id, notified 값 초기화
    public LikeDTO(String targetType, Integer targetId, Integer memId) {
        this.targetType = targetType;
        this.targetId = targetId;
    }

}
