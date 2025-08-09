package com.project.moyora.app.service;

import com.project.moyora.app.dto.ApplicationDto;
import com.project.moyora.app.dto.ApplicationResponseDto;
import com.project.moyora.app.domain.*;
import com.project.moyora.app.repository.BoardApplicationRepository;
import com.project.moyora.app.repository.BoardRepository;
import com.project.moyora.app.repository.UserSubTagRepository;
import com.project.moyora.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final BoardApplicationRepository applicationRepository;
    private final BoardRepository boardRepository;
    private final UserSubTagRepository userSubTagRepository;  // 추가 필요


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

        // OFFLINE 모임일 경우 인증된 사용자만 신청 가능
        if (board.getMeetType() == MeetType.OFFLINE) {
            Boolean verification = currentUser.getUser().getVerified();
            if (verification == null || currentUser.getUser().getVerificationStatus() != VerificationStatus.ACCEPTED) {
                throw new AccessDeniedException("오프라인 모임은 인증된 사용자만 신청할 수 있습니다.");
            }
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
    @Transactional
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
        ApplicationStatus currentStatus = application.getStatus();

        // 1. 상태가 LOCKED이면 절대 변경 불가
        if (currentStatus == ApplicationStatus.LOCKED) {
            throw new IllegalStateException("LOCKED 상태인 신청은 상태를 변경할 수 없습니다.");
        }

        // 작성자 요청
        if (board.getWriter().getId().equals(requesterId)) {
            if (status != ApplicationStatus.ACCEPTED && status != ApplicationStatus.REJECTED) {
                throw new IllegalArgumentException("작성자는 신청을 수락 또는 거절만 할 수 있습니다.");
            }

            // 2. 상태를 ACCEPTED로 바꾸려면 모집 인원 확인
            if (status == ApplicationStatus.ACCEPTED && currentStatus != ApplicationStatus.ACCEPTED) {
                if (board.getParticipation() >= board.getHowMany()) {
                    throw new IllegalStateException("모집 인원을 초과할 수 없습니다.");
                }

                // 참여 인원 증가
                board.setParticipation(board.getParticipation() + 1);
                boardRepository.save(board);
            }

        } else if (application.getApplicant().getId().equals(requesterId)) {
            // 신청자 요청
            if (status != ApplicationStatus.WAITING && status != ApplicationStatus.CANCELED) {
                throw new IllegalArgumentException("신청자는 대기 상태로 변경하거나 취소만 할 수 있습니다.");
            }

            if (currentStatus == ApplicationStatus.CANCELED && status != ApplicationStatus.CANCELED) {
                throw new IllegalStateException("CANCELED 상태인 신청은 다시 변경할 수 없습니다.");
            }

        } else {
            // 작성자도 신청자도 아닌 경우
            throw new AccessDeniedException("본인만 상태를 변경할 수 있습니다.");
        }

        // 3. 기존 상태가 ACCEPTED → REJECTED/CANCELED 로 바뀌는 경우, 참여 인원 감소
        if (currentStatus == ApplicationStatus.ACCEPTED &&
                (status == ApplicationStatus.REJECTED || status == ApplicationStatus.CANCELED)) {
            int newParticipation = board.getParticipation() - 1;
            board.setParticipation(Math.max(0, newParticipation));
            boardRepository.save(board);
        }

        // 상태 및 변경일 갱신
        application.setStatus(status);
        application.setAppliedAt(LocalDateTime.now());
        applicationRepository.save(application);

        // ------------------------------
        // 확정 상태로 변경되었을 때 (ACCEPTED 또는 LOCKED), 이전 상태가 확정 상태가 아니면 UserSubTag 저장
        if ((status == ApplicationStatus.ACCEPTED || status == ApplicationStatus.LOCKED) &&
                (currentStatus != ApplicationStatus.ACCEPTED && currentStatus != ApplicationStatus.LOCKED)) {

            List<SubTag> subTags = board.getSubTags();

            for (SubTag subTag : subTags) {
                boolean exists = userSubTagRepository.existsByUserAndSubTag(requester.getUser(), subTag);
                if (!exists) {
                    UserSubTag ust = UserSubTag.builder()
                            .user(requester.getUser())
                            .subTag(subTag)
                            .category(Category.PARTICIPATING)  // 예: 참여중인 태그 구분용 enum
                            .build();
                    userSubTagRepository.save(ust);
                }
            }
        }

        // 상태가 확정(ACCEPTED, LOCKED)에서 취소 또는 거절(REJECTED, CANCELED)로 변경되면 UserSubTag 삭제
        if ((currentStatus == ApplicationStatus.ACCEPTED || currentStatus == ApplicationStatus.LOCKED) &&
                (status == ApplicationStatus.REJECTED || status == ApplicationStatus.CANCELED)) {

            // 해당 유저가 참여중인 태그 삭제 (삭제 방식은 설계에 따라 다름)
            userSubTagRepository.deleteByUserAndCategory(requester.getUser(), Category.PARTICIPATING);
        }

    }


    // 3. 게시글 신청 현황 조회
    public List<ApplicationResponseDto> getApplicationsForBoardByOwner(Long boardId, CustomUserDetails requester) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (!board.getWriter().getId().equals(requester.getUser().getId())) {
            throw new AccessDeniedException("신청 현황은 게시글 작성자만 조회할 수 있습니다.");
        }

        // ✅ 작성자 제외한 신청자만 조회
        List<BoardApplication> applications =
                applicationRepository.findWithApplicantAndTagsByBoardExcludingWriter(board, board.getWriter().getId());

        return applications.stream()
                .map(ApplicationResponseDto::from)
                .collect(Collectors.toList());
    }

}
