package com.acorn.movielink.comunity.controller;

import com.acorn.movielink.comunity.dto.CommentDTO;
import com.acorn.movielink.comunity.service.CommentService;
import com.acorn.movielink.comunity.service.CommunityPostService;
import com.acorn.movielink.login.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
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
    public ResponseEntity<?> addComment( @PathVariable("postId") int postId,
                                         @RequestBody CommentDTO commentDTO,
                                         Principal principal) {
        // 1. postId 유효성 검사
        if (postId <= 0 || !communityPostService.existsById(postId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 게시글입니다.");
        }

        // 2. DTO에 postId 설정 (누락 방지)
        commentDTO.setPostId(postId); // ★ 추가 ★

        // 3. 로그인 사용자 확인
        Integer memId = authenticationUtil.getCurrentUserId();
        if (memId == null || memId <= 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 4. parentId null 확인
        if (commentDTO.getParentId() != null) {
            return ResponseEntity.badRequest().body("댓글에는 parentId가 없어야 합니다.");
        }

        try {
            // 5. 댓글 작성 및 응답 반환
            CommentDTO savedComment = commentService.addComment(commentDTO, memId, postId);
            return ResponseEntity.ok(savedComment);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }



    // 댓글 수정
    @PutMapping("/{postId}/{commentId}/update")
    public ResponseEntity<?> updateComment(@PathVariable("postId") int postId,
                                           @PathVariable("commentId") int commentId,
                                           @RequestBody CommentDTO commentDTO) {
        try {
            // 로그인 여부 검증
            Integer memId = authenticationUtil.getCurrentUserId();
            if (memId == null || memId <= 0) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }
            // 댓글 수정 처리
            CommentDTO updatedComment = commentService.updateComment(commentDTO, postId, memId);

            // 성공 응답
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "댓글이 수정되었습니다.");
            response.put("data", updatedComment);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("댓글 수정 중 오류가 발생했습니다.");
        }
    }

}