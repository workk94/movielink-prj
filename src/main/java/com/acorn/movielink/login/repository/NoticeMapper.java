package com.acorn.movielink.login.repository;

import com.acorn.movielink.login.dto.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {
    void insertNotice(Notice notice);

    Notice findLatestNotice();

    void updateNotice(Notice notice);

    void deleteNotice(Integer notificationId);

    List<Notice> findAllNotices();

    Notice findNoticeById(@Param("notificationId") Integer notificationId);
}
