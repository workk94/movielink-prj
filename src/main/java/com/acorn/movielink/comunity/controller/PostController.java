package com.acorn.movielink.comunity.controller;

import com.acorn.movielink.comunity.dto.*;
import com.acorn.movielink.comunity.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
public class PostController {

    private final com.acorn.movielink.comunity.service.CommunityPostService postService;
    private final TagService tagService;
    private final PostImageService postImageService;
    private final CommunityLikeService likeService;
    private final CommentService commentService;
    private final FileStore fileStore;
    @Autowired
    private AuthenticationUtil authenticationUtil;

    @Autowired
    PostController(com.acorn.movielink.comunity.service.CommunityPostService postService,
                   TagService tagService,
                   PostImageService postImageService,
                   CommunityLikeService likeService,
                   CommentService commentService,
                   FileStore fileStore) {
        this.postService = postService;
        this.tagService = tagService;
        this.postImageService = postImageService;
        this.likeService = likeService;
        this.commentService = commentService;
        this.fileStore = fileStore;
    }

    //    좋아요 많은 유저 Top7
    @ModelAttribute("topSevenMem")
    public List<PostDTO> getTop7Member() {
        return postService.selectTop7Member();
    }

    @ModelAttribute("topTags")
    public List<TagDTO> getTopTags() {
        return tagService.selectTopTags(); // 그대로 문자열 리스트 반환
    }


    //유저 1명이 작성한 게시글 목록
    @GetMapping("/postOneMember/{memId}")
    public String getOneMemPostList(@PathVariable("memId") String memId,
                                    Model model) {
        List<PostDTO> postOneMemList = postService.selectOneMemberPostList(Integer.parseInt(memId));
        model.addAttribute("postOneMem", postOneMemList);
        return "postOneMemList";
    }

    //게시글 리스트 전체 조회
    @GetMapping("/postAll")
    public String getAllPosts(Model model) {
        List<PostDTO> posts = postService.selectAllList();
        for (PostDTO post : posts) {
            String thumbnailUrl = postImageService.getThumbnailUrl(post.getPostId());
            if (thumbnailUrl != null) {
                post.setThumbnailUrl(thumbnailUrl);
            }
        }
        model.addAttribute("postAll", posts);
        return "postAllList";
    }

    //좋아요 많은 게시글 Top10
    @GetMapping("/communityMain")
    public String getTopTenPosts(Model model) {

        List<PostDTO> postTopTen = postService.selectTopTenPosts();
        model.addAttribute("postTopTen", postTopTen);
        //System.out.println("TopTen =" +postTopTen);
        return "communityMain";
    }

    // 게시글 상세조회
    @GetMapping("/postDetail/{postId}")
    public String getPostOne(@PathVariable(name = "postId") int postId,
                             Model model) {
        Map<String, Object> authInfo = authenticationUtil.checkAuthentication();
        boolean isAuthenticated = (boolean) authInfo.get("authenticated");
        int memId = (int) authInfo.getOrDefault("memId", 0); // 기본값 0 설정
        boolean isLiked = false;

        if (isAuthenticated && memId > 0) {
            isLiked = likeService.isLikedByUser(postId, memId);
        }
        PostDTO postOne = postService.selectPostById(postId);
        List<TagDTO> tagNames = tagService.selectTagsByPostId(postId);
        List<CommentDTO> comments = commentService.getCommentsByPostId(postId);

        String thumbnail = postOne.getThumbnailUrl();
        // 게시글 상세 페이지에 댓글 개수도 전달
        int commentCount = commentService.getCommentCountByPostId(postId);
        model.addAttribute("commentCount", commentCount);
        model.addAttribute("tags", tagNames);
        model.addAttribute("postOne", postOne);
        model.addAttribute("isLiked", isLiked);
        model.addAttribute("memId", memId);
        model.addAttribute("comments", comments);
        model.addAttribute("thumbnail", thumbnail);
        return "postOneDetail";
    }


    @PostMapping("/like/{postId}")
    @ResponseBody  // AJAX 요청을 처리하므로 JSON 형태로 응답
    public LikeDTO toggleLikePost(@PathVariable(name = "postId") int postId) {
        LikeDTO likeDTO = new LikeDTO();

        try {
            // 사용자 ID 조회 (AuthenticationUtil 사용)
            int memId = authenticationUtil.getCurrentUserId();

            // 좋아요 상태 토글
            boolean isLiked = likeService.togglePostLike(postId, memId);

            // 게시글 좋아요 수 업데이트
            PostDTO post = postService.selectPostById(postId);

            // 결과 반환
            likeDTO.setTargetId(postId);
            likeDTO.setMemId(memId);
            likeDTO.setTargetType("POST");
            likeDTO.setLikeId(isLiked ? 1 : 0);
            likeDTO.setLikeCount(post.getPostLikeCnt());
        } catch (SecurityException e) {
            // 로그인되지 않은 경우 처리
            likeDTO.setTargetId(postId);
            likeDTO.setMemId(0); // 비로그인 처리
        }

        return likeDTO;
    }


    //게시글 수정하기
    @GetMapping("/postEdit/{postId}")
    public String updatePostForm(@PathVariable("postId") int postId,
                                 Model model) {
        PostDTO postEdit = postService.selectPostById(postId);
        model.addAttribute("post", postEdit);
        return "postEdit";
    }

    @PostMapping("/postEdit/update")
    public String updatePost(@ModelAttribute("post") PostDTO post) {
        postService.updatePost(post);
        return "redirect:/postDetail/" + post.getPostId();
    }


    //게시글 삭제하기
    @PostMapping("/postDelete/{postId}")
    public String deletePost(@PathVariable("postId") int postId) {
        postService.deletePost(postId);
        return "redirect:/postAll";
    }

    //게시글 작성하기
//    @GetMapping("/postCreate")
//    public String createPostForm(HttpSession httpSession, Model model){
//        Map<String, Object> authInfo = authenticationUtil.checkAuthentication();
//        boolean isAuthenticated = (boolean) authInfo.get("authenticated");
//        int memId = (int) authInfo.getOrDefault("memId", 0); // 기본값 0 설정
//        // 로그인 여부 체크
//        boolean loginChecking = httpSession.getAttribute("memId") != null;
//        model.addAttribute("loginChecking", loginChecking);  // 로그인 상태를 뷰로 전달
//        // 로그인되지 않았다면 로그인 페이지로 리디렉션
//        if (!loginChecking) {
//            return "redirect:/login";
//        }
//        //빈 객체를 모델에 추가하여 뷰에 전달
//        model.addAttribute("post", new PostDTO());
//        return "postCreateView";
//    }


    @GetMapping("/postCreate")
    public String createPostForm(Model model) {

        // 인증 상태 체크
        Map<String, Object> authInfo = authenticationUtil.checkAuthentication();
        boolean isAuthenticated = (boolean) authInfo.get("authenticated");
        if (!isAuthenticated) {
            return "redirect:/login";
        }

        // 사용자 ID 추가
        int memId = (int) authInfo.getOrDefault("memId", 0);
        model.addAttribute("memId", memId);

        //빈 객체를 모델에 추가하여 뷰에 전달
        model.addAttribute("post", new PostDTO());
        return "postCreateView";
    }


    @PostMapping("/postCreate")
    public String createPost(@RequestParam(name = "postTitle") String postTitle,
                             @RequestParam(name = "content") String content,
                             @RequestParam(name = "images") List<MultipartFile> images, // 게시글 이미지 파일
                             @RequestParam(value = "tags", required = false) String tagsInput, // 폼 데이터 기반 태그 처리
                             Model model) {
        try {

            // 로그인한 사용자 ID를 게시글에 설정
            int memId = authenticationUtil.getCurrentUserId();

            // 1. 게시글 저장
            PostDTO postDTO = new PostDTO();
            postDTO.setMemId(memId);
            postDTO.setPostTitle(postTitle);
            postDTO.setContent(content);

            int postId = postService.insertPost(postDTO); // 게시글 저장 후 ID 반환

            // 2. 태그 처리 (태그 입력이 있는 경우만 실행)
            if (tagsInput != null && !tagsInput.trim().isEmpty()) {
                List<TagDTO> tagList = tagService.parseTags(tagsInput);

                for (TagDTO tagDTO : tagList) {
                    int tagId = tagService.insertOrGetTagId(tagDTO.getTagName());
                    tagService.insertPostTag(postId, tagId);
                }
            }

            // 3. 이미지 저장
            if (images != null && !images.isEmpty()) {
                List<UploadFile> uploadedFiles = fileStore.storeFiles(images); // 파일 저장
                postImageService.savePostImages(postId, uploadedFiles); // 게시물 이미지 DB에 저장
            }

            // 성공 메시지 추가
            model.addAttribute("message", "게시글이 성공적으로 작성되었습니다!");
            return "redirect:/postAll"; // 전체 게시글 페이지로 이동

        } catch (Exception e) {
            // 예외 발생 시 로그 출력
            e.printStackTrace();
            model.addAttribute("errorMessage", "게시글 작성 중 오류가 발생했습니다.");
            return "redirect:/postCreate"; // 게시글 작성 페이지로 리다이렉트
        }
    }


}


