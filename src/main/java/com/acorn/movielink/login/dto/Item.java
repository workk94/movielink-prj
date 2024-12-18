package com.acorn.movielink.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    private Integer itemId;
    private String itemName;
    private Integer itemPoint;
    private String itemImg;
}
