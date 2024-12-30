package com.acorn.movielink.login.controller;

import com.acorn.movielink.login.dto.*;
import com.acorn.movielink.login.service.GenreService;
import com.acorn.movielink.login.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    // 이메일 중복 확인을 위한 POST 엔드포인트 추가
    @PostMapping("/check_email")
    @ResponseBody
    public ResponseEntity<CheckEmailResponse> checkEmail(@RequestBody CheckEmailRequest request) {
        String email = request.getEmail();
        logger.debug("이메일 중복 확인 요청: {}", email);

        boolean exists = false;
        if (email != null && !email.isEmpty() && email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            Optional<Member> memberOpt = memberService.findByEmail(email);
            exists = memberOpt.isPresent();
        } else {
            logger.warn("이메일 중복 확인 실패 - 이메일이 비어있거나 형식이 올바르지 않음");
        }

        CheckEmailResponse response = new CheckEmailResponse(exists);
        return ResponseEntity.ok(response);
    }

    // 닉네임 중복 확인을 위한 POST 엔드포인트 추가
    @PostMapping("/check_nickname")
    @ResponseBody
    public ResponseEntity<CheckNicknameResponse> checkNickname(@RequestBody CheckNicknameRequest request) {
        String nickname = request.getNickname();
        logger.debug("닉네임 중복 확인 요청: {}", nickname);

        boolean exists = false;
        if (nickname != null && !nickname.isEmpty() && nickname.length() >= 2) {
            Optional<Member> memberOpt = memberService.findByNickname(nickname);
            exists = memberOpt.isPresent();
        } else {
            logger.warn("닉네임 중복 확인 실패 - 닉네임이 비어있거나 형식이 올바르지 않음");
        }

        CheckNicknameResponse response = new CheckNicknameResponse(exists);
        return ResponseEntity.ok(response);
    }
}
