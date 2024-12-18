package com.acorn.movielink.login.service;

import com.acorn.movielink.login.dto.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);
    private final MemberService memberService;

    public CustomAuthenticationSuccessHandler(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Member member = memberService.findByEmail(email).orElse(null);
        if (member != null) {
            Byte memType = member.getMemType();
            if (memType != null && memType == 1) {
                logger.info("관리자 {} 가 로그인했습니다.", email);
                response.sendRedirect("/admin/dashboard"); // 관리자 페이지로 리다이렉트
            } else {
                logger.info("일반 회원 {} 가 로그인했습니다.", email);
                response.sendRedirect("/"); // 일반 회원 페이지로 리다이렉트
            }
        } else {
            logger.error("로그인한 사용자를 찾을 수 없습니다: {}", email);
            response.sendRedirect("/login?error");
        }
    }
}
