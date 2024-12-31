package com.acorn.movielink.comunity.controller;

import com.acorn.movielink.comunity.dto.CommentDTO;
import com.acorn.movielink.comunity.dto.LikeDTO;
import com.acorn.movielink.comunity.service.CommentService;
import com.acorn.movielink.comunity.service.CommunityLikeService;
import com.acorn.movielink.comunity.service.CommunityPostService;
import com.acorn.movielink.comunity.service.TagService;
import com.acorn.movielink.comunity.dto.PostDTO;
import com.acorn.movielink.comunity.dto.TagDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PostController {

    @Autowired
    private CommunityPostService postService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommunityLikeService likeService;

    @Autowired
    private CommentService commentService;

    //    좋아요 많은 유저 Top7
    @ModelAttribute("topSevenMem")
    public List<PostDTO> getTop7Member(){
        return postService.selectTop7Member();
    }

    @ModelAttribute("topTags")
    public List<TagDTO> getTopTags() {
        return tagService.selectTopTags(); // 그대로 문자열 리스트 반환
    }



    //유저 1명이 작성한 게시글 목록
    @GetMapping("/postOneMember/{memId}")
    public String getOneMemPostList(@PathVariable("memId") String memId,
                                    Model model){
        List<PostDTO> postOneMemList = postService.selectOneMemberPostList(Integer.parseInt(memId));
        model.addAttribute("postOneMem", postOneMemList);
        return "postOneMemList";
    }







    //게시글 리스트 전체 조회
    @GetMapping("/postAll")
    public String getAllPosts(Model model){
        List<PostDTO> postAll = postService.selectAllList();
        model.addAttribute("postAll", postAll);
        System.out.println("전체리스트는 " + postAll);
        return "postAllList";
    }

    //좋아요 많은 게시글 Top10
    @GetMapping("/communityMain")
    public String getTopTenPosts(Model model){

        List<PostDTO> postTopTen = postService.selectTopTenPosts();
        model.addAttribute("postTopTen", postTopTen);
        System.out.println("TopTen =" +postTopTen);
        return "communityMain";
    }

//    // 게시글 상세조회
//    @GetMapping("/postDetail/{postId}")
//    public String getPostOne(@PathVariable(name = "postId") int postId,
//                             Model model,
//                             HttpSession httpSession){
//        Integer memId = (Integer) httpSession.getAttribute("memId");
//        boolean isLiked = false;
//        if(memId != null){
//            isLiked = likeService.isLikedByUser(postId, memId);
//        }else {
//            memId = 0; // 또는 다른 기본값 설정
//        }
//        // 게시글 조회
//        PostDTO postOne = postService.selectPostById(postId);
//
//        // 태그는 이미 문자열 리스트로 설정되어 있음
//        List<TagDTO> tagNames =tagService.selectTagsByPostId(postId);
//
//        // 댓글 조회
//        List<CommentDTO> comments = commentService.getCommentsByPostId(postId);
//        // 게시글 상세 페이지에 댓글 개수도 전달
//        int commentCount = commentService.getCommentCountByPostId(postId);
//        model.addAttribute("commentCount", commentCount);
//        // 모델에 데이터 추가
//        model.addAttribute("tags", tagNames);
//        model.addAttribute("postOne", postOne);
//        model.addAttribute("isLiked", isLiked);  // 좋아요 상태 (로그인 여부에 따라)
//        model.addAttribute("memId", memId);  // 로그인한 사용자의 ID (로그인하지 않은 경우 null)
//        model.addAttribute("comments", comments); // 댓글과 대댓글 리스트 추가
//
//
//        // 디버깅 출력
//        System.out.println("해당게시글의 태그는 " + tagNames);
//        System.out.println("해당게시글의 id는 " + postOne.getPostId());
//        System.out.println("세션 memId: " + memId);
//
//        return "postOneDetail";
//    }


    @GetMapping("/postDetail/{postId}")
    public ResponseEntity<?> getPostOne(@PathVariable int postId, HttpSession session) {
        Integer memId = (Integer) session.getAttribute("memId");
        boolean isLiked = memId != null && likeService.isLikedByUser(postId, memId);

        // 게시글 조회
        PostDTO postOne = postService.selectPostById(postId);
        List<TagDTO> tags = tagService.selectTagsByPostId(postId);
        List<CommentDTO> comments = commentService.getCommentsByPostId(postId);

        // JSON 응답
        Map<String, Object> response = new HashMap<>();
        response.put("postOne", postOne);
        response.put("tags", tags);
        response.put("isLiked", isLiked);
        response.put("memId", memId);
        response.put("comments", comments);

        return ResponseEntity.ok(response);
    }







    @PostMapping("/like/{postId}")
    @ResponseBody  // AJAX 요청을 처리하므로 JSON 형태로 응답
    public LikeDTO toggleLikePost(@PathVariable(name = "postId") int postId,
                                  HttpSession session) {

        // 로그인된 사용자 확인
        Integer memId = (Integer) session.getAttribute("memId");

        // 로그인하지 않은 경우에는 좋아요 상태 변경을 할 수 없으므로 false 반환
        if (memId == null) {
            LikeDTO likeDTO = new LikeDTO();
            likeDTO.setTargetId(postId);  // 좋아요를 누른 게시글 ID
            likeDTO.setMemId(memId); // null일 경우에도 처리
            return likeDTO;
        }

        // 좋아요 상태를 토글 (추가 또는 제거)
        boolean isLiked = likeService.togglePostLike(postId, memId);

        // 게시글 정보 불러오기 (좋아요 수 포함)
        PostDTO post = postService.selectPostById(postId);

        // LikeDTO 객체에 응답 값 설정
        LikeDTO likeDTO = new LikeDTO();
        likeDTO.setTargetId(postId);  // 좋아요가 적용된 게시글 ID
        likeDTO.setMemId(memId);  // 사용자 ID
        likeDTO.setTargetType("POST");  // 게시글 좋아요로 설정

        // 좋아요 상태와 총합을 포함한 값 반환
        likeDTO.setLikeId(isLiked ? 1 : 0);  // 상태가 추가되었으면 1, 취소되었으면 0
        likeDTO.setLikeCount(post.getPostLikeCnt()); // 좋아요 수 추가

        return likeDTO;
    }









    //게시글 수정하기
    @GetMapping("/postEdit/{postId}")
    public String updatePostForm(@PathVariable("postId") int postId,
                                 Model model){
        PostDTO postEdit = postService.selectPostById(postId);
        model.addAttribute("post", postEdit);
        return "postEdit";
    }

    @PostMapping("/postEdit/update")
    public String updatePost(@ModelAttribute("post") PostDTO post){
        postService.updatePost(post);
        return "redirect:/postDetail/" +post.getPostId();
    }


    //게시글 삭제하기
    @PostMapping("/postDelete/{postId}")
    public String deletePost(@PathVariable("postId") int postId){
        postService.deletePost(postId);
        return "redirect:/postAll";
    }




    //게시글 작성하기
    @GetMapping("/postCreate")
    public String createPostForm(HttpSession httpSession, Model model){
        // 로그인 여부 체크
        boolean loginChecking = httpSession.getAttribute("memId") != null;
        model.addAttribute("loginChecking", loginChecking);  // 로그인 상태를 뷰로 전달
        // 로그인되지 않았다면 로그인 페이지로 리디렉션
        if (!loginChecking) {
            return "redirect:/login";
        }
        //빈 객체를 모델에 추가하여 뷰에 전달
        model.addAttribute("post", new PostDTO());
        return "postCreateView";
    }


@PostMapping("/postCreate")
public String createPost(@RequestParam("postTitle") String postTitle,
                         @RequestParam("content") String content,
                         @RequestParam(value = "tags", required = false) String tagsInput, // 폼 데이터 기반 태그 처리
                         Model model,
                         HttpSession httpSession) {
    try {
        // 로그인한 사용자 ID를 게시글에 설정
        int memId = (int) httpSession.getAttribute("memId");

        // 1. 게시글 저장
        PostDTO postDTO = new PostDTO();
        postDTO.setMemId(memId);
        postDTO.setPostTitle(postTitle);
        postDTO.setContent(content);

        int postId = postService.insertPost(postDTO); // 게시글 저장 후 ID 반환
        System.out.println("게시글 저장 완료: postId=" + postId); // 로그 추가

        // 2. 태그 처리 (태그 입력이 있는 경우만 실행)
        if (tagsInput != null && !tagsInput.trim().isEmpty()) {
            // 태그 문자열 파싱 및 DTO 변환
            List<TagDTO> tagList = tagService.parseTags(tagsInput);

            for (TagDTO tagDTO : tagList) {
                // 태그 삽입 또는 조회 후 관계 설정
                int tagId = tagService.insertOrGetTagId(tagDTO.getTagName());
                tagService.insertPostTag(postId, tagId);
                System.out.println("태그 관계 설정 완료: postId=" + postId + ", tagId=" + tagId); // 로그
            }
        }

        // 성공 메시지 추가
        model.addAttribute("message", "게시글이 성공적으로 작성되었습니다!");
        return "redirect:/postAll"; // 전체 게시글 페이지로 이동

    } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("errorMessage", "게시글 작성 중 오류가 발생했습니다.");
        return "redirect:/postCreate"; // 게시글 작성 페이지로 리다이렉트
    }
}




}

