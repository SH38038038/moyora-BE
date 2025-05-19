package com.project.moyora.app.service;

import com.project.moyora.app.Dto.NoticeDto;
import com.project.moyora.app.Dto.NoticeRequest;
import com.project.moyora.app.domain.Board;
import com.project.moyora.app.domain.Notice;
import com.project.moyora.app.domain.NoticeComment;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.repository.BoardRepository;
import com.project.moyora.app.repository.NoticeCommentRepository;
import com.project.moyora.app.repository.NoticeRepository;
import com.project.moyora.global.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeCommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Override
    public NoticeDto createNotice(NoticeRequest request, User user) {
        Board board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writer(user)
                .board(board)
                .build();
        return NoticeDto.fromEntity(noticeRepository.save(notice));
    }

    @Override
    public List<NoticeDto> getAllNotices() {
        return noticeRepository.findAll().stream()
                .map(NoticeDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public NoticeDto getNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공지글을 찾을 수 없습니다."));
        return NoticeDto.fromEntity(notice);
    }

    @Override
    public void addComment(Long noticeId, String content, User user) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new EntityNotFoundException("공지글을 찾을 수 없습니다."));

        NoticeComment comment = NoticeComment.builder()
                .notice(notice)
                .writer(user)
                .content(content)
                .build();
        commentRepository.save(comment);
    }

    @Override
    public List<NoticeDto> getNoticesByBoard(Long boardId) {
        List<Notice> notices = noticeRepository.findByBoardId(boardId);  // 해당 게시판에 속하는 공지 조회
        return notices.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Notice 엔티티를 NoticeDto로 변환하는 메서드
    private NoticeDto convertToDto(Notice notice) {
        return new NoticeDto(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedTime()
        );
    }

    @Override
    public NoticeDto updateNotice(Long noticeId, NoticeRequest noticeRequest, User user) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("공지사항을 찾을 수 없습니다."));

        if (!notice.getWriter().equals(user)) {
            throw new AccessDeniedException("공지사항 수정 권한이 없습니다.");
        }

        notice.setTitle(noticeRequest.getTitle());
        notice.setContent(noticeRequest.getContent());
        Notice updatedNotice = noticeRepository.save(notice);

        return new NoticeDto(updatedNotice.getId(), updatedNotice.getTitle(), updatedNotice.getContent(), updatedNotice.getCreatedTime());
    }

    @Transactional
    @Override
    public void deleteNotice(Long noticeId, User user) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("공지사항을 찾을 수 없습니다."));

        if (!notice.getWriter().equals(user)) {
            throw new AccessDeniedException("공지사항 삭제 권한이 없습니다.");
        }

        noticeRepository.delete(notice);
    }
}
