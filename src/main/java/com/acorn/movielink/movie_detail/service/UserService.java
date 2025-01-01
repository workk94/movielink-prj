package com.acorn.movielink.movie_detail.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Principal;

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
}
