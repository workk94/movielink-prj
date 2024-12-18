package com.acorn.movielink.login.controller;

import com.acorn.movielink.login.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordController.class);

    @Autowired
    private MemberService memberService;

    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        logger.debug("비밀번호 찾기 페이지 요청");
        return "forgot_password";
    }

    @PostMapping("/forgot_password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        logger.debug("비밀번호 찾기 요청 for 이메일: {}", email);
        String tempPassword = memberService.resetPasswordAndSendEmail(email);
        if (tempPassword != null) {
            logger.info("임시 비밀번호 생성 및 이메일 발송 성공 for 이메일: {}", email);
            model.addAttribute("message", "임시 비밀번호가 이메일로 전송되었습니다. 로그인 후 비밀번호를 변경해주세요.");
        } else {
            logger.warn("비밀번호 찾기 실패 - 존재하지 않는 이메일: {}", email);
            model.addAttribute("error", "해당 이메일을 사용하는 사용자가 없습니다.");
        }
        return "forgot_password";
    }
}
