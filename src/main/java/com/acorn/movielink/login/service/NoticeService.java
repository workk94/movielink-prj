package com.acorn.movielink.login.service;

import com.acorn.movielink.login.dto.Notice;
import com.acorn.movielink.login.repository.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoticeService {

    private final NoticeMapper noticeMapper;

    @Autowired
    public NoticeService(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    public void createNotice(Notice notice) {
        noticeMapper.insertNotice(notice);
    }

    public Optional<Notice> getLatestNotice() {
        return Optional.ofNullable(noticeMapper.findLatestNotice());
    }

    public void updateNotice(Notice notice) {
        noticeMapper.updateNotice(notice);
    }

    public void deleteNotice(Integer id) {
        noticeMapper.deleteNotice(id);
    }

    public List<Notice> getAllNotices() {
        return noticeMapper.findAllNotices();
    }

    public Optional<Notice> getNoticeById(Integer id) {
        return Optional.ofNullable(noticeMapper.findNoticeById(id));
    }
}
