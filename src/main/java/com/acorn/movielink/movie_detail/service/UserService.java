package com.acorn.movielink.movie_detail.service;

import com.acorn.movielink.login.dto.Member;
import com.acorn.movielink.login.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class UserService {

    public String getUserEmailFromPrincipal(Principal principal) {
        if (principal instanceof Authentication authentication) {
            Object principalObj = authentication.getPrincipal();

            if (principalObj instanceof UserDetails userDetails) {
                return userDetails.getUsername(); // 이메일 반환
            }
        }
        return null; // 이메일을 가져올 수 없는 경우 null 반환
    }

    public Integer getMemberIdFromAuthentication(Authentication authentication, MemberService memberService) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String email = getUserEmailFromPrincipal(authentication);
        if (email == null || email.isEmpty()) {
            return null;
        }

        return memberService.findByEmail(email)
                .map(member -> member.getMemId())
                .orElse(null);
    }

}
