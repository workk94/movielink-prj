package com.acorn.movielink.config;

import com.acorn.movielink.login.dto.Member;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class KakaoOAuth2User implements OAuth2User, UserDetails {

    @Getter
    private final Member member;
    private final Map<String, Object> attributes;

    public KakaoOAuth2User(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return member.getMemEmail();
    }

    @Override
    public Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
        String role = member.getMemType() == 1 ? "ROLE_ADMIN" : "ROLE_USER";
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    // memSnsId를 반환하는 메서드로 수정
    public String getMemSnsId() {
        return member.getMemSnsId();
    }

    public String getEmail() {
        return member.getMemEmail();
    }

    public String getNickname() {
        return member.getMemNn();
    }

    @Override
    public String getPassword() {
        return member.getMemPw();
    }

    @Override
    public String getUsername() {
        return member.getMemEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !Boolean.TRUE.equals(member.getMemIsBanned());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


}
