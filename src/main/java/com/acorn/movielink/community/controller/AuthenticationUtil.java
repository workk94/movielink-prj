package com.acorn.movielink.community.controller;

import com.acorn.movielink.login.dto.Member;
import com.acorn.movielink.login.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class AuthenticationUtil {

    @Autowired
    private MemberService memberService;


    // 인증 상태 확인
    public Map<String, Object> checkAuthentication() {
        Authentication authentication = getAuthentication();
        Map<String, Object> response = new HashMap<>();

        if (!isAuthenticated(authentication)) {
            response.put("authenticated", false);
            return response;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            response.put("authenticated", true);
            response.put("username", userDetails.getUsername());
            response.put("roles", userDetails.getAuthorities());

            // memId 조회
            Integer memId = findMemberIdByEmail(userDetails.getUsername());
            response.put("memId", memId != null ? memId : -1); // null이면 -1 반환
            return response;
        }

        response.put("authenticated", false);
        return response;
    }

    // 현재 로그인 사용자 ID 조회
    public Integer getCurrentUserId() {
        Authentication authentication = getAuthentication();

        if (!isAuthenticated(authentication)) {
            throw new SecurityException("로그인이 필요합니다.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            Integer memId = findMemberIdByEmail(userDetails.getUsername());
            if (memId != null) {
                return memId;
            } else {
                throw new SecurityException("회원 정보를 찾을 수 없습니다.");
            }
        }

        throw new SecurityException("인증 정보가 올바르지 않습니다.");
    }

    // 공통 로직 1 - 인증 객체 가져오기
    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    // 공통 로직 2 - 인증 상태 확인
    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
    }

    // 공통 로직 3 - 이메일로 memId 조회
    private Integer findMemberIdByEmail(String email) {
        Optional<Member> memberOpt = memberService.findByEmail(email);
        return memberOpt.map(Member::getMemId).orElse(null);
    }


}