package com.acorn.movielink.login.service;

import com.acorn.movielink.login.dto.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

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
        Integer memId = null;
        Byte memType = null;
        String email = null;

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
            Optional<Member> memberOpt = memberService.findByEmail(email);
            if (memberOpt.isPresent()) {
                Member member = memberOpt.get();
                memId = member.getMemId();
                memType = member.getMemType();
            }
        } else if (principal instanceof OAuth2User oauth2User) {
            // OAuth2User에서 memSnsId 또는 email을 추출하여 Member를 찾는 로직 필요

            email = oauth2User.getAttribute("email");
            String memSnsId = oauth2User.getAttribute("memSnsId"); // memSnsId는 OAuth2User에서 제공해야 함
            if (memSnsId != null) {
                Optional<Member> memberOpt = memberService.findByMemSnsId(memSnsId);
                if (memberOpt.isPresent()) {
                    Member member = memberOpt.get();
                    memId = member.getMemId();
                    memType = member.getMemType();
                }
            }
        }

        if (memId != null) {
            // 세션에 memId 저장
            HttpSession session = request.getSession();
            session.setAttribute("memId", memId);
            //실시간 동접자
            // activeUsers에 사용자 추가
            // SessionListener.addUser(memId);
            logger.info("사용자 ID {} (이메일: {}) 가 로그인했습니다.", memId, email);

            // memType에 따라 리다이렉트
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
