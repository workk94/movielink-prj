package com.acorn.movielink.people_detail.controller;

import com.acorn.movielink.data.dto.MovieDTO;
import com.acorn.movielink.login.service.MovieService;
import com.acorn.movielink.people_detail.dto.People;
import com.acorn.movielink.people_detail.dto.Post;
import com.acorn.movielink.people_detail.service.PeopleService;
import com.acorn.movielink.people_detail.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/person")
public class PeopleDetailController {

    @Autowired
    private PeopleService peopleService;

    @Autowired
    private PostService postService;

    @Autowired
    private MovieService movieService;

    @GetMapping("/{peopleId}/filmography")
    public String showPersonFilmography(
            @PathVariable("peopleId") Integer peopleId,
            @RequestParam(value = "tab", required = false, defaultValue = "filmography") String tab,
            Model model) {

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

        model.addAttribute("selectedTab", tab);

        return "person-detail";
    }
}
