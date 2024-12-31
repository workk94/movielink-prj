package com.acorn.movielink.community.controller;

import com.acorn.movielink.community.dto.CommentDTO;
import com.acorn.movielink.community.service.CommentService;
import com.acorn.movielink.community.service.CommunityPostService;
import com.acorn.movielink.login.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/comments") // 게시글 ID가 URL에 포함됨
public class CommentController {
    @Autowired
    private CommunityPostService communityPostService;


    @Autowired
    private CommentService commentService;

    @Autowired
    private MemberService memberService;

    // 비동기 처리를 위한 스레드 풀 생성
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

//    // 동적 스레드 풀 적용
//    private final ThreadPoolExecutor executor =
//            (ThreadPoolExecutor) Executors.newCachedThreadPool();
// Logger 초기화
private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private AuthenticationUtil authenticationUtil; // 유틸 클래스 주입


    // 댓글 작성
    @PostMapping("/{postId}/add")
    public ResponseEntity<?> addComment(@PathVariable("postId") int postId,
                                        @RequestBody CommentDTO commentDTO) {
        if (postId <= 0 || !communityPostService.existsById(postId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 게시글입니다.");
        }

        if (commentDTO.getPostId() == null || !commentDTO.getPostId().equals(postId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("URL과 본문의 게시글 ID가 일치하지 않습니다.");
        }

        Integer memId = authenticationUtil.getCurrentUserId();
        if (memId == null || memId <= 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // parentId null 확인
        if (commentDTO.getParentId() != null) {
            return ResponseEntity.badRequest().body("댓글에는 parentId가 없어야 합니다.");
        }
        try {
        CommentDTO savedComment = commentService.addComment(commentDTO, memId, postId);
            return ResponseEntity.ok(Map.of("success", true, "data", savedComment));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }

    }

    @PostMapping("/{parentId}/replyAdd")
    public CompletableFuture<ResponseEntity<?>> addReplyAsync(
            @RequestBody CommentDTO commentDTO,
            HttpServletRequest request) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                // 로그인 여부 검증
                Integer memId = authenticationUtil.getCurrentUserId();
                if (memId == null || memId <= 0) {
                    throw new SecurityException("로그인이 필요합니다.");
                }

                // 부모 댓글(parentId) 검증
                if (commentDTO.getParentId() == null ||
                        !commentService.validateCommentExists(commentDTO.getParentId())) {
                    throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
                }

                // 댓글 내용 검증
                if (commentDTO.getContent() == null ||
                        commentDTO.getContent().trim().isEmpty()) {
                    throw new IllegalArgumentException("댓글 내용은 비어 있을 수 없습니다.");
                }

                // 대댓글 추가
                CommentDTO savedReply = commentService.addReply(commentDTO, memId);
                return ResponseEntity.ok(savedReply);

            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage()); // 400 에러 처리
            } catch (SecurityException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()); // 401 에러 처리
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("댓글 작성 중 오류가 발생했습니다."); // 500 에러 처리
            }
        }, executor);
    }






    // 댓글 수정
    @PutMapping("/{commentId}/update")
    public ResponseEntity<?> updateComment(@PathVariable("commentId") int commentId,
                                           @RequestBody CommentDTO commentDTO) {
        try {
            // 로그인 여부 검증
            Integer memId = authenticationUtil.getCurrentUserId();
            if (memId == null || memId <= 0) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            // 댓글 존재 여부 검증
            if (!commentService.validateCommentExists(commentId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 댓글입니다.");
            }

            // 수정 권한 검증
            if (!commentService.isCommentOwner(commentId, memId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
            }

            // 댓글 수정
            CommentDTO updatedComment = commentService.updateComment(commentDTO, memId);
            return ResponseEntity.ok(updatedComment); // 수정 후 반환

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("댓글 수정 중 오류가 발생했습니다.");
        }
    }

    // 대댓글 수정
    @PutMapping("/replyUpdate")
    public ResponseEntity<?> updateReply(@RequestBody CommentDTO commentDTO) {
        try {
            // 로그인 여부 검증
            Integer memId = authenticationUtil.getCurrentUserId();
            if (memId == null || memId <= 0) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            // 대댓글 존재 여부 검증
            if (commentDTO.getCommentId() <= 0 || !commentService.validateCommentExists(commentDTO.getCommentId())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 대댓글입니다.");
            }

            // 권한 검증
            if (!commentService.isCommentOwner(commentDTO.getCommentId(), memId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
            }

            // 대댓글 수정 처리
            commentService.updateReply(commentDTO, memId);
            return ResponseEntity.ok("대댓글 수정 성공");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("대댓글 수정 중 오류가 발생했습니다.");
        }
    }


    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public CompletableFuture<ResponseEntity<?>> deleteCommentAsync(
            @PathVariable("postId") int postId,
            @PathVariable("commentId") int commentId) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                // 로그인 여부 검증
                Integer memId = authenticationUtil.getCurrentUserId();
                if (memId == null || memId <= 0) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
                }

                // 댓글 존재 여부 검증
                if (!commentService.validateCommentExists(commentId)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 댓글입니다.");
                }

                // 권한 검증
                if (!commentService.isCommentOwner(commentId, memId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
                }

                // 댓글 삭제
                commentService.deleteComment(commentId, memId);
                return ResponseEntity.ok("댓글 삭제 완료");

            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (SecurityException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("댓글 삭제 중 오류가 발생했습니다.");
            }
        }, executor);
    }

    // 대댓글 삭제
    @DeleteMapping("/reply/{commentId}")
    public CompletableFuture<ResponseEntity<?>> deleteReplyAsync(
            @PathVariable int postId,
            @PathVariable int commentId,
            @PathVariable int replyId) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                // 로그인 여부 검증
                Integer memId = authenticationUtil.getCurrentUserId();
                if (memId == null || memId <= 0) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
                }

                // 대댓글 존재 여부 검증
                if (!commentService.validateCommentExists(replyId)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 대댓글입니다.");
                }

                // 부모-자식 관계 검증
                if (!commentService.isParentChildRelation(commentId, replyId)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("대댓글의 부모 댓글이 일치하지 않습니다.");
                }

                // 권한 검증
                if (!commentService.isCommentOwner(replyId, memId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
                }

                // 대댓글 삭제
                commentService.deleteReply(replyId, memId);
                return ResponseEntity.ok("대댓글 삭제 완료");

            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (SecurityException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("대댓글 삭제 중 오류가 발생했습니다.");
            }
        }, executor);
    }


    // 좋아요 증가
    @PutMapping("/like/{commentId}")
    public CompletableFuture<ResponseEntity<String>> increaseLike(
            @PathVariable int commentId, HttpSession session) {
        Integer memId =  authenticationUtil.getCurrentUserId();
        return CompletableFuture.supplyAsync(() -> {
            commentService.increaseLikeCount(commentId, memId);
            return ResponseEntity.ok("좋아요 증가");
        });
    }

    // 좋아요 감소
    @PutMapping("/unlike/{commentId}")
    public CompletableFuture<ResponseEntity<String>> decreaseLike(
            @PathVariable int commentId, HttpSession session) {
        Integer memId = authenticationUtil.getCurrentUserId();
        return CompletableFuture.supplyAsync(() -> {
            commentService.decreaseLikeCount(commentId, memId);
            return ResponseEntity.ok("좋아요 감소");
        });
    }








}