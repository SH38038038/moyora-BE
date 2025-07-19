package com.project.moyora.app.service;

import com.project.moyora.app.dto.NoticeDto;
import com.project.moyora.app.dto.NoticeRequest;
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

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeCommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Transactional
    @Override
    public NoticeDto createNotice(Long boardId, NoticeRequest request, User user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        // 게시판에 이미 공지가 있으면 생성 불가
        if (noticeRepository.existsByBoard(board)) {
            throw new IllegalStateException("이미 해당 게시판에 공지사항이 존재합니다.");
        }

        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writer(user)
                .board(board)
                .build();

        noticeRepository.save(notice);

        Notice saved = noticeRepository.findByIdWithWriterAndComments(notice.getId())
                .orElseThrow(() -> new IllegalStateException("공지 저장 실패"));

        return NoticeDto.fromEntity(saved);
    }

    @Transactional
    @Override
    public NoticeDto getNoticeByBoard(Long boardId) {
        Notice notice = noticeRepository.findByBoardIdWithWriterAndComments(boardId)
                .orElseThrow(() -> new EntityNotFoundException("공지사항이 존재하지 않습니다."));
        return NoticeDto.fromEntity(notice);
    }

    @Transactional
    @Override
    public NoticeDto updateNotice(Long noticeId, NoticeRequest noticeRequest, User user) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("공지사항을 찾을 수 없습니다."));

        if (!notice.getWriter().equals(user)) {
            throw new AccessDeniedException("공지사항 수정 권한이 없습니다.");
        }

        notice.setTitle(noticeRequest.getTitle());
        notice.setContent(noticeRequest.getContent());
        return NoticeDto.fromEntity(notice);
    }

    @Override
    public void deleteNotice(Long noticeId, User user) {
        throw new UnsupportedOperationException("공지사항은 삭제할 수 없습니다.");
    }

    @Transactional
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
}
