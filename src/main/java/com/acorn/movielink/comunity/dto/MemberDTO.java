package com.acorn.movielink.comunity.dto;

import com.acorn.movielink.login.dto.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {

    private Integer memId;
    private String memEmail;
    private String memPw;
    private String memTel;
    private String memNn;
    private String memProfileOriginFileNm;
    private String memProfileConvertedFileNm;
    private String memProfileFilePath;
    private Integer memPoint = 0;
    private LocalDateTime memCreatedAt;
    private LocalDateTime memUpdatedAt;
    private Boolean memIsBanned = false;
    private Byte memType = 0; // 0: 일반 유저, 1: 관리자
    private String memSnsId;

    private List<Integer> genreIds;

    private List<Person> likedPersons;

    // 추가된 필드
    private MultipartFile profileImage;
}
