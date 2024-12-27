package com.acorn.movielink.comunity.controller;

import com.acorn.movielink.comunity.dto.CommentDTO;
import com.acorn.movielink.comunity.service.CommentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;


//    // 댓글 목록 조회 (게시글에 달린 댓글)
//    @GetMapping("/comments/{postId}")
//    public String getCommentsByPostId(@PathVariable int postId, Model model) {
//        // 댓글 조회
//        List<CommentDTO> comments = commentService.getCommentsByPostId(postId);
//        model.addAttribute("comments", comments);
//        return "postOneDetail"; // 댓글 포함된 게시글 상세 페이지로 이동
//    }

    // 댓글 작성
//    @PostMapping("/{postId}/comments/add")
//    public String addComment(@PathVariable int postId,
//                             @ModelAttribute CommentDTO comment) {
//        comment.setPostId(postId);
//        commentService.addComment(comment);
//        return "redirect:/postDetail/" + comment.getPostId(); // 댓글 작성 후 해당 게시글로 리다이렉트
//    }

    @PostMapping("/{postId}")
    public ResponseEntity<?> addComment(@PathVariable int postId, @RequestBody CommentDTO commentDTO, HttpSession session) {
        Integer memId = (Integer) session.getAttribute("memId");
        if (memId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        commentDTO.setPostId(postId);
        commentDTO.setMemId(memId);
        commentService.addComment(commentDTO);
        return ResponseEntity.ok(commentDTO);
    }

    // 대댓글 등록
    @PostMapping("/comment/{commentId}/reply")
    public String addReply(@PathVariable int commentId, @ModelAttribute CommentDTO replyDTO) {
        replyDTO.setParentId(commentId);
        commentService.addReply(replyDTO);
        return "redirect:/post/" + replyDTO.getPostId();  // 댓글이 달린 게시글로 리다이렉트
    }

//    // 댓글 수정
//    @PostMapping("/comment/{commentId}/edit")
//    public String updateComment(@PathVariable int commentId, @ModelAttribute CommentDTO commentDTO) {
//        commentDTO.setCommentId(commentId);
//        commentService.updateComment(commentDTO);
//        return "redirect:/post/" + commentDTO.getPostId();
//    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable int commentId, @RequestBody CommentDTO commentDTO, HttpSession session) {
        Integer memId = (Integer) session.getAttribute("memId");
        if (memId == null || !commentService.isOwner(commentId, memId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
        }
        commentDTO.setCommentId(commentId);
        commentService.updateComment(commentDTO);
        return ResponseEntity.ok(commentDTO);
    }





    // 대댓글 수정
    @PostMapping("/reply/{commentId}/edit")
    public String updateReply(@PathVariable int commentId, @ModelAttribute CommentDTO commentDTO) {
        commentDTO.setCommentId(commentId);
        commentService.updateReply(commentDTO);
        return "redirect:/post/" + commentDTO.getPostId();
    }


//    // 댓글 삭제
//    @PostMapping("/comment/{commentId}/delete")
//    public String deleteComment(@PathVariable int commentId) {
//        commentService.deleteComment(commentId);
//        return "redirect:/post/{postId}";
//    }

//    @DeleteMapping("/{commentId}")
//    public ResponseEntity<?> deleteComment(@PathVariable int commentId, HttpSession session) {
//        Integer memId = (Integer) session.getAttribute("memId");
//        if (memId == null || !commentService.isOwner(commentId, memId)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
//        }
//        commentService.deleteComment(commentId);
//        return ResponseEntity.ok("댓글 삭제 완료");
//    }




    // 대댓글 삭제
    @PostMapping("/reply/{commentId}/delete")
    public String deleteReply(@PathVariable int commentId) {
        commentService.deleteReply(commentId);
        return "redirect:/post/{postId}";
    }

    // 댓글 좋아요 증가
    @PostMapping("/comments/like/{commentId}")
    @ResponseBody
    public void increaseLikeCount(@PathVariable int commentId) {
        commentService.increaseLikeCount(commentId);
    }

    // 댓글 좋아요 취소
    @PostMapping("/comments/unlike/{commentId}")
    @ResponseBody
    public void decreaseLikeCount(@PathVariable int commentId) {
        commentService.decreaseLikeCount(commentId);
    }

    public void addReply(CommentDTO reply) {
        // 부모 댓글이 있으면 parent_id를 설정, 없으면 NULL
        if (reply.getParentId() == null) {
            reply.setParentId(null);
        }
        commentService.addReply(reply);
    }

}



