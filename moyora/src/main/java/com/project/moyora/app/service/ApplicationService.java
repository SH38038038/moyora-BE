package com.project.moyora.app.service;

import com.project.moyora.app.Dto.ApplicationDto;
import com.project.moyora.app.Dto.ApplicationRequestDto;
import com.project.moyora.app.Dto.ApplicationResponseDto;
import com.project.moyora.app.domain.*;
import com.project.moyora.app.repository.ApplicationRepository;
import com.project.moyora.app.repository.BoardRepository;
import com.project.moyora.app.repository.UserRepository;
import com.project.moyora.global.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final BoardRepository boardRepository;

    // 1. 신청 처리 (WAITING 상태로)
    public ApplicationDto applyForBoard(Long boardId, CustomUserDetails currentUser) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 본인이 작성한 게시글에는 신청 불가
        if (board.getWriter().getId().equals(currentUser.getUser().getId())) {
            throw new AccessDeniedException("본인이 작성한 글에는 신청할 수 없습니다.");
        }

        // 모집 인원 초과 체크
        long acceptedCount = applicationRepository.findByBoard(board).stream()
                .filter(app -> app.getStatus() == ApplicationStatus.ACCEPTED)
                .count();

        if (acceptedCount >= board.getHowMany()) {
            throw new IllegalStateException("모집 인원이 다 차서 신청할 수 없습니다.");
        }

        // 중복 신청 방지
        boolean alreadyApplied = applicationRepository.findByBoard(board).stream()
                .anyMatch(app -> app.getApplicant().getId().equals(currentUser.getUser().getId()));
        if (alreadyApplied) {
            throw new IllegalStateException("이미 신청한 사용자입니다.");
        }

        // 신청 처리 (대기 상태로)
        BoardApplication application = BoardApplication.builder()
                .board(board)
                .applicant(currentUser.getUser())
                .status(ApplicationStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .build();

        applicationRepository.save(application);

        return new ApplicationDto(board.getId(), currentUser.getName(), ApplicationStatus.WAITING);
    }

    // 2. 게시글 별 신청 상태 변경
    public void updateApplicationStatus(Long boardId, Long applicationId, ApplicationStatus status, CustomUserDetails requester) {
        // 게시글 조회
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 신청서 조회
        BoardApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("신청을 찾을 수 없습니다."));

        // 해당 신청서가 게시글에 속하는지 확인
        if (!application.getBoard().getId().equals(board.getId())) {
            throw new IllegalArgumentException("해당 게시글의 신청이 아닙니다.");
        }

        Long requesterId = requester.getUser().getId();

        // 작성자만 수락/거절 상태 변경 가능
        if (board.getWriter().getId().equals(requesterId)) {
            // 작성자는 수락 또는 거절만 가능
            if (status != ApplicationStatus.ACCEPTED && status != ApplicationStatus.REJECTED) {
                throw new IllegalArgumentException("작성자는 신청을 수락 또는 거절만 할 수 있습니다.");
            }

            // 이미 CANCELED 상태인 경우 ACCEPTED 또는 REJECTED로 변경 불가
            if (application.getStatus() == ApplicationStatus.CANCELED) {
                throw new IllegalStateException("CANCELED 상태인 신청은 수락 또는 거절할 수 없습니다.");
            }

            // 수락 상태일 경우, 모집 인원 체크
            if (status == ApplicationStatus.ACCEPTED) {
                // 모집 인원 초과 여부 먼저 확인
                if (board.getParticipation() >= board.getHowMany()) {
                    throw new IllegalStateException("모집 인원을 초과할 수 없습니다.");
                }

                // 참여 인원 증가
                board.setParticipation(board.getParticipation() + 1);
                boardRepository.save(board);
            }

        }
        // 신청자는 대기 또는 취소 상태 변경 가능
        else if (application.getApplicant().getId().equals(requesterId)) {
            if (status != ApplicationStatus.WAITING && status != ApplicationStatus.CANCELED) {
                throw new IllegalArgumentException("신청자는 대기 상태로 변경하거나 취소만 할 수 있습니다.");
            }

            // 이미 CANCELED 상태인 경우 상태 변경 불가
            if (application.getStatus() == ApplicationStatus.CANCELED && status != ApplicationStatus.CANCELED) {
                throw new IllegalStateException("CANCELED 상태인 신청은 다시 변경할 수 없습니다.");
            }
        }
        // 작성자도 신청자도 아닌 경우
        else {
            throw new AccessDeniedException("본인만 상태를 변경할 수 있습니다.");
        }

        // 신청 상태 변경
        application.setStatus(status);

        // 수정 시각을 APPLIED_AT에 저장
        application.setAppliedAt(LocalDateTime.now());

        // 기존 신청서를 업데이트
        applicationRepository.save(application);  // JPA는 이미 로딩된 엔티티를 업데이트 처리함
    }


    // 3. 게시글 신청 현황 조회
    public List<ApplicationResponseDto> getApplicationsForBoardByOwner(Long boardId, CustomUserDetails requester) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (!board.getWriter().getId().equals(requester.getUser().getId())) {
            throw new AccessDeniedException("신청 현황은 게시글 작성자만 조회할 수 있습니다.");
        }

        List<BoardApplication> applications = applicationRepository.findByBoardIdWithApplicants(boardId);

        return applications.stream()
                .map(ApplicationResponseDto::from)
                .collect(Collectors.toList());
    }
}
