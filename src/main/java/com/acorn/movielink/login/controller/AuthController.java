package com.acorn.movielink.login.controller;

import com.acorn.movielink.login.dto.Genre;
import com.acorn.movielink.login.dto.Member;
import com.acorn.movielink.login.service.GenreService;
import com.acorn.movielink.login.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final MemberService memberService;
    private final GenreService genreService;

    @Autowired
    public AuthController(MemberService memberService, GenreService genreService) {
        this.memberService = memberService;
        this.genreService = genreService;
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        logger.debug("회원가입 폼 요청");
        model.addAttribute("member", new Member());

        // 장르 목록을 모델에 추가
        List<Genre> genres = genreService.getAllGenres();
        model.addAttribute("genres", genres);

        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("member") Member member, BindingResult result, Model model, @RequestParam(value = "genreIds", required = false) List<Integer> genreIds) {
        logger.debug("회원가입 요청: {}", member);

        // 이메일 중복 체크
        if (memberService.findByEmail(member.getMemEmail()).isPresent()) {
            result.rejectValue("memEmail", "error.member", "이미 사용 중인 이메일입니다.");
            logger.warn("회원가입 실패 - 이미 사용 중인 이메일: {}", member.getMemEmail());
        }

        // 장르 선택 여부 검증 (필수라면)
        if (genreIds == null || genreIds.isEmpty()) {
            result.rejectValue("genreIds", "error.member", "적어도 하나의 장르를 선택해야 합니다.");
            logger.warn("회원가입 실패 - 장르 미선택");
        } else {
            member.setGenreIds(genreIds); // Member 객체에 genreIds 설정
        }

        if (result.hasErrors()) {
            logger.debug("회원가입 폼 검증 실패: {}", result.getAllErrors());

            // 다시 장르 목록을 모델에 추가
            List<Genre> genres = genreService.getAllGenres();
            model.addAttribute("genres", genres);

            return "signup";
        }

        // 회원 등록
        memberService.registerMember(member);
        logger.info("회원가입 성공: {}", member.getMemEmail());
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        logger.debug("로그인 폼 요청");
        return "login";
    }

}
