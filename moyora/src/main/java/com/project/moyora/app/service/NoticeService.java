package com.project.moyora.app.service;

import com.project.moyora.app.dto.NoticeDto;
import com.project.moyora.app.dto.NoticeRequest;
import com.project.moyora.app.domain.User;

public interface NoticeService {
    NoticeDto createNotice(Long boardId, NoticeRequest request, User user);
    NoticeDto getNoticeByBoard(Long boardId);
    void addComment(Long noticeId, String content, User user);
    NoticeDto updateNotice(Long noticeId, NoticeRequest noticeRequest, User user);
    void deleteNotice(Long noticeId, User user);
}

