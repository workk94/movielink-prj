package com.acorn.movielink.login.service;

import com.acorn.movielink.config.KakaoOAuth2User;
import com.acorn.movielink.login.dto.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(MemberService memberService, PasswordEncoder passwordEncoder) {
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        // kakao_account 안에 email 정보가 있을 수 있음
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = (kakaoAccount != null && kakaoAccount.containsKey("email")) ? (String) kakaoAccount.get("email") : null;

        Map<String, Object> profile = kakaoAccount != null && kakaoAccount.containsKey("profile")
                ? (Map<String, Object>) kakaoAccount.get("profile") : null;
        String nickname = (profile != null && profile.containsKey("nickname")) ? (String) profile.get("nickname") : null;

        // memSnsId 생성: provider + "_" + providerUserId
        String providerUserId = attributes.get("id").toString();
        String memSnsId = registrationId + "_" + providerUserId;

        // memSnsId로 회원 조회 또는 신규 회원 등록
        Member member = memberService.findByMemSnsId(memSnsId)
                .orElseGet(() -> {
                    String randomPassword = generateRandomPassword();
                    Member newMember = Member.builder()
                            .memEmail(email)
                            .memNn(nickname)
                            .memSnsId(memSnsId)
                            .memPw(passwordEncoder.encode(randomPassword)) // 임의의 비밀번호 설정
                            .build();
                    memberService.registerMember(newMember);
                    return memberService.findByEmail(email)
                            .orElseThrow(() -> new IllegalStateException("User not found after creation"));
                });

        return new KakaoOAuth2User(member, attributes);
    }

    private String generateRandomPassword() {
        // UUID를 이용한 임의의 비밀번호 생성
        return UUID.randomUUID().toString();
    }
}
