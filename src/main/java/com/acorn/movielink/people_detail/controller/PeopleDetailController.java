package com.acorn.movielink.people_detail.controller;

import com.acorn.movielink.data.dto.MovieDTO;
import com.acorn.movielink.login.dto.Member;
import com.acorn.movielink.login.service.MemberService;
import com.acorn.movielink.login.service.MovieService;
import com.acorn.movielink.people_detail.dto.People;
import com.acorn.movielink.people_detail.dto.Post;
import com.acorn.movielink.people_detail.service.PeopleLikeService;
import com.acorn.movielink.people_detail.service.PeopleService;
import com.acorn.movielink.people_detail.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/person")
public class PeopleDetailController {

    @Autowired
    private PeopleService peopleService;

    @Autowired
    private PostService postService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private PeopleLikeService peopleLikeService;

    @Autowired
    private MemberService memberService;

    @GetMapping("/{peopleId}/filmography")
    public String showPersonFilmography(
            @PathVariable("peopleId") Integer peopleId,
            @RequestParam(value = "tab", required = false, defaultValue = "filmography") String tab,
            Model model,
            Principal principal) {

        // 인물 정보 조회
        People people = peopleService.getPeopleById(peopleId);
        model.addAttribute("peopleInfo", people);

        if ("community".equals(tab)) {
            // 커뮤니티 게시글 조회 (태그가 인물 이름과 같은 게시글)
            String tagName = people.getPeopleNm(); // 인물 이름을 태그 이름으로 사용
            List<Post> communityPosts = postService.getPostsByTagName(tagName);
            model.addAttribute("communityPosts", communityPosts);
        } else {
            // 필모그래피 정보 조회
            List<MovieDTO> filmography = movieService.getFilmographyByPeopleId(peopleId);
            model.addAttribute("filmography", filmography);
        }

        // 좋아요 정보 추가
        if (principal != null) {
            Integer memId = getMemIdFromPrincipal(principal); // 실제 memId 가져오기
            if (memId != null) {
                boolean isLiked = peopleLikeService.isPersonLikedByUser(peopleId, memId);
                model.addAttribute("isLiked", isLiked);
                model.addAttribute("likeCount", peopleLikeService.getLikeCount(peopleId));
            } else {
                model.addAttribute("isLiked", false);
                model.addAttribute("likeCount", peopleLikeService.getLikeCount(peopleId));
            }
        } else {
            model.addAttribute("isLiked", false);
            model.addAttribute("likeCount", peopleLikeService.getLikeCount(peopleId));
        }

        model.addAttribute("selectedTab", tab);

        return "person-detail";
    }

    // 좋아요 액션을 처리하는 엔드포인트
    @PostMapping("/{peopleId}/like")
    @ResponseBody
    public ResponseEntity<?> likePerson(
            @PathVariable("peopleId") Integer peopleId,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Integer memId = getMemIdFromPrincipal(principal);
        if (memId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        boolean alreadyLiked = peopleLikeService.isPersonLikedByUser(peopleId, memId);

        if (alreadyLiked) {
            return ResponseEntity.badRequest().body("이미 좋아요를 눌렀습니다.");
        }

        peopleLikeService.likePerson(peopleId, memId);
        int likeCount = peopleLikeService.getLikeCount(peopleId);
        return ResponseEntity.ok(likeCount);
    }

    // 좋아요 취소 액션을 처리하는 엔드포인트
    @DeleteMapping("/{peopleId}/like")
    @ResponseBody
    public ResponseEntity<?> unlikePerson(
            @PathVariable("peopleId") Integer peopleId,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Integer memId = getMemIdFromPrincipal(principal);
        if (memId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        boolean alreadyLiked = peopleLikeService.isPersonLikedByUser(peopleId, memId);

        if (!alreadyLiked) {
            return ResponseEntity.badRequest().body("좋아요를 누르지 않았습니다.");
        }

        peopleLikeService.unlikePerson(peopleId, memId);
        int likeCount = peopleLikeService.getLikeCount(peopleId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * Principal 객체로부터 실제 사용자 id추출
     */
    private Integer getMemIdFromPrincipal(Principal principal) {
        if (principal instanceof Authentication authentication) {
            Object principalObj = authentication.getPrincipal();

            // OAuth2User
            if (principalObj instanceof OAuth2User oauth2User) {
                // KakaoOAuth2User가 Member 정보를 포함하고 있다고 가정
                Object memberObj = oauth2User.getAttribute("member");
                if (memberObj instanceof Member member) {
                    return member.getMemId();
                }
            }

            // UserDetails (일반 로그인 사용자)
            if (principalObj instanceof UserDetails userDetails) {
                String email = userDetails.getUsername();
                Optional<Member> memberOpt = memberService.findByEmail(email);
                return memberOpt.map(Member::getMemId).orElse(null);
            }
        }
        return null;
    }
}
