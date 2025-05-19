package com.project.moyora.app.service;

import com.project.moyora.app.Dto.NoticeDto;
import com.project.moyora.app.Dto.NoticeRequest;
import com.project.moyora.app.domain.User;

import java.util.List;

public interface NoticeService {
    NoticeDto createNotice(NoticeRequest request, User user);
    List<NoticeDto> getAllNotices();
    NoticeDto getNotice(Long id);
    void addComment(Long noticeId, String content, User user);
    List<NoticeDto> getNoticesByBoard(Long boardId);
    NoticeDto updateNotice(Long noticeId, NoticeRequest noticeRequest, User user);
    void deleteNotice(Long noticeId, User user);
}
