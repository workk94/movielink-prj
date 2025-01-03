package com.acorn.movielink.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // 간단한 로그 예시
        logger.warn("[CustomAuthenticationEntryPoint] 인증되지 않은 사용자의 접근: {} {}",
                request.getMethod(), request.getRequestURI());

        // X-Requested-With 헤더를 확인(주로 AJAX 요청인지 구분)
        String xRequestedWith = request.getHeader("X-Requested-With");
        logger.debug("[CustomAuthenticationEntryPoint] X-Requested-With: {}", xRequestedWith);

        // AJAX 요청이면 401 에러, 그 외는 /login 리다이렉트
        if ("XMLHttpRequest".equalsIgnoreCase(xRequestedWith)) {
            logger.info("[CustomAuthenticationEntryPoint] AJAX 요청 -> 401 Unauthorized 전송");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        } else {
            logger.info("[CustomAuthenticationEntryPoint] 일반 요청 -> /login 리다이렉트");
            response.sendRedirect("/login");
        }
    }
}
