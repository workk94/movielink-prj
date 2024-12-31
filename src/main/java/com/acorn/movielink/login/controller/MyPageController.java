// src/main/java/com/acorn/movielink/login/controller/MyPageController.java

package com.acorn.movielink.login.controller;

import com.acorn.movielink.config.KakaoOAuth2User;
import com.acorn.movielink.login.dto.*;
import com.acorn.movielink.login.service.GenreService;
import com.acorn.movielink.login.service.MemberService;
import com.acorn.movielink.login.service.MypageCommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class MyPageController {

    private static final Logger logger = LoggerFactory.getLogger(MyPageController.class);
    private final MemberService memberService;
    private final GenreService genreService;
    private final MypageCommentService mypageCommentService;

    @Value("${file.profile-images-dir}")
    private String uploadDir;

    @Autowired
    public MyPageController(MemberService memberService, GenreService genreService, MypageCommentService mypageCommentService) {
        this.memberService = memberService;
        this.genreService = genreService;
        this.mypageCommentService = mypageCommentService;
    }

    @GetMapping("/mypage")
    public String showMyPage(Authentication authentication, Model model) {
        logger.debug("마이페이지 요청");
        Member member = extractMemberFromAuthentication(authentication);
        if (member == null) {
            return "redirect:/login";
        }

        // 회원의 좋아요한 인물 목록 조회
        List<Person> likedPersons = memberService.getLikedPersons(member.getMemId());
        // 회원의 좋아요한 영화 목록 조회 추가
        List<Movie> likedMovies = memberService.getLikedMovies(member.getMemId());
        // 회원의 작성 게시글 목록 조회
        List<Post> writtenPosts = memberService.getWrittenPosts(member.getMemId());
        // 회원의 아이템 구매 목록 조회
//        List<Item> purchasedItems = memberService.getPurchasedItems(member.getMemId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = member.getMemCreatedAt() != null ? member.getMemCreatedAt().format(formatter) : "";

        // 개수 계산
        int likedMoviesCount = likedMovies.size();
        int writtenPostsCount = writtenPosts.size();

        //  댓글 수 조회
        int commentCount = mypageCommentService.countCommentsByMemId(member.getMemId());


        model.addAttribute("member", member);
        model.addAttribute("formattedDate", formattedDate);

        model.addAttribute("likedPersons", likedPersons);
        model.addAttribute("likedMovies", likedMovies);
        model.addAttribute("writtenPosts", writtenPosts);

        // ★ 실제 카운트값
        model.addAttribute("likedMoviesCount", likedMoviesCount);
        model.addAttribute("writtenPostsCount", writtenPostsCount);
        model.addAttribute("commentCount", commentCount);

        return "mypage";
    }

    @GetMapping("/mypage/edit")
    public String showEditProfile(Authentication authentication, Model model) {
        logger.debug("개인정보 수정 페이지 요청");
        Member member = extractMemberFromAuthentication(authentication);
        if (member == null) {
            return "redirect:/login";
        }
        List<Genre> genres = genreService.getAllGenres();
        model.addAttribute("genres", genres);

        // 사용자가 선택한 장르 ID 불러오기
        List<Integer> selectedGenreIds = memberService.getGenreIdsByMemberId(member.getMemId());
        model.addAttribute("selectedGenreIds", selectedGenreIds);
        model.addAttribute("member", member);
        return "edit_profile";
    }

    @PostMapping("/mypage/edit")
    public String editProfile(
            @ModelAttribute("member") Member updatedMember,
            BindingResult result, // 검증 오류 처리를 위한 BindingResult 추가
            Authentication authentication,
            Model model,
            @RequestParam(value = "genreIds", required = false) List<Integer> genreIds, // 폼에서 genreIds 캡처
            @RequestParam("profileImage") MultipartFile profileImage) {

        logger.debug("개인정보 수정 요청");
        Member member = extractMemberFromAuthentication(authentication);
        if (member == null) {
            return "redirect:/login";
        }

        // 프로필 이미지 처리 (기존 코드)
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                String originalFilename = StringUtils.cleanPath(profileImage.getOriginalFilename());
                String fileExtension = getFileExtension(originalFilename);

                if (!isImageFile(fileExtension)) {
                    result.rejectValue("profileImage", "error.member", "유효한 이미지 파일을 업로드해주세요 (PNG, JPG, JPEG, GIF).");
                    logger.warn("프로필 이미지 업로드 실패 - 잘못된 파일 형식");
                } else {
                    String profileImagePath = memberService.saveProfileImage(profileImage, member.getMemId());

                    if (member.getMemProfileFilePath() != null && !member.getMemProfileFilePath().isEmpty()) {
                        memberService.deleteProfileImage(member.getMemProfileConvertedFileNm());
                    }

                    member.setMemProfileOriginFileNm(originalFilename);
                    member.setMemProfileConvertedFileNm(getFileNameFromPath(profileImagePath));
                    member.setMemProfileFilePath(profileImagePath);
                }
            } catch (IOException e) {
                logger.error("프로필 이미지 업로드 실패", e);
                result.reject("profileImage", "프로필 이미지를 업로드하는 중 오류가 발생했습니다.");
            }
        }

        // 장르 선택 여부 검증 (필수라면)
        if (genreIds == null || genreIds.isEmpty()) {
            result.rejectValue("genreIds", "error.member", "적어도 하나의 장르를 선택해야 합니다.");
            logger.warn("개인정보 수정 실패 - 장르 미선택");
        } else {
            updatedMember.setGenreIds(genreIds); // Member 객체에 genreIds 설정
        }

        // 비밀번호 업데이트
        if (updatedMember.getMemPw() != null && !updatedMember.getMemPw().isEmpty()) {
            member.setMemPw(memberService.getPasswordEncoder().encode(updatedMember.getMemPw()));
        }

        // 기타 필드 업데이트
        member.setMemTel(updatedMember.getMemTel());
        member.setMemNn(updatedMember.getMemNn());

        if (result.hasErrors()) {
            logger.debug("개인정보 수정 폼 검증 실패: {}", result.getAllErrors());

            // 다시 장르 목록과 선택된 장르를 모델에 추가
            List<Genre> genres = genreService.getAllGenres();
            model.addAttribute("genres", genres);
            model.addAttribute("selectedGenreIds", genreIds);

            return "edit_profile";
        }

        // Member 업데이트
        memberService.updateMember(member);

        // 장르 업데이트
        memberService.updateMemberGenres(member.getMemId(), genreIds);
        logger.info("개인정보 수정 성공 for 사용자: {}", member.getMemEmail());

        // 업데이트된 회원 정보 다시 로드
        member = memberService.findById(member.getMemId()).orElse(member);

        // 가입일 포맷
        String formattedDate = member.getMemCreatedAt() != null ?
                member.getMemCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";

        // 좋아요한 인물, 영화, 작성글, 구매 아이템 다시 로드
        List<Person> likedPersons = memberService.getLikedPersons(member.getMemId());
        List<Movie> likedMovies = memberService.getLikedMovies(member.getMemId());
        List<Post> writtenPosts = memberService.getWrittenPosts(member.getMemId());
//        List<Item> purchasedItems = memberService.getPurchasedItems(member.getMemId());

        model.addAttribute("member", member);
        model.addAttribute("formattedDate", formattedDate);
        model.addAttribute("likedPersons", likedPersons);
        model.addAttribute("likedMovies", likedMovies);
        model.addAttribute("writtenPosts", writtenPosts);
//        model.addAttribute("purchasedItems", purchasedItems);
        model.addAttribute("message", "개인정보가 성공적으로 업데이트되었습니다.");

        return "mypage";
    }


    private boolean isImageFile(String fileExtension) {
        String ext = fileExtension.toLowerCase();
        return ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("gif");
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex >= 0) ? filename.substring(dotIndex + 1) : "";
    }

    private String getFileNameFromPath(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }
        return Paths.get(filePath).getFileName().toString();
    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof User userDetails) {
            String email = userDetails.getUsername();
            return memberService.findByEmail(email).orElse(null);
        } else if (principal instanceof KakaoOAuth2User kakaoUser) {
            return kakaoUser.getMember();
        } else if (principal instanceof OAuth2User) {

            return null;
        }
        return null;
    }
}
