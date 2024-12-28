package com.acorn.movielink.config;

import com.acorn.movielink.login.dto.Member;
import com.acorn.movielink.login.service.CustomAuthenticationSuccessHandler;
import com.acorn.movielink.login.service.CustomOAuth2UserService;
import com.acorn.movielink.login.service.CustomUserDetailsService;
import com.acorn.movielink.login.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final MemberService memberService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          CustomOAuth2UserService customOAuth2UserService,
                          PasswordEncoder passwordEncoder,
                          CustomAuthenticationSuccessHandler successHandler,
                          MemberService memberService) {
        this.userDetailsService = userDetailsService;
        this.customOAuth2UserService = customOAuth2UserService;
        this.passwordEncoder = passwordEncoder;
        this.successHandler = successHandler;
        this.memberService = memberService;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider) throws Exception {
        http
                .authenticationProvider(authenticationProvider)
                //실시간 동접자
//                .sessionManagement(session -> session
//                        .maximumSessions(-1)
//                        .sessionRegistry(sessionRegistry())
//                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/uploads/**", "/img/**", "/css/**", "/js/**", "/webjars/**", "/fonts/**").permitAll()
                        .requestMatchers("/signup", "/forgot_password", "/reset_password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/person/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/person/**").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자 권한 요구
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler) // 커스텀 핸들러 설정
                        .failureHandler((request, response, exception) -> {
                            String email = request.getParameter("username");
                            logger.warn("로그인 실패: 이메일 - {}", email);
                            request.getSession().setAttribute("loginErrorMessage", exception.getMessage());
                            response.sendRedirect("/login");
                        })
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(successHandler) // OAuth2 로그인 성공 시에도 동일한 핸들러 사용
                )
                .logout(logout -> logout
                                .logoutSuccessUrl("/")
                                .invalidateHttpSession(true)
                                .addLogoutHandler((request, response, authentication) -> {
                                    if (authentication != null) {
                                        Object principal = authentication.getPrincipal();
                                        Integer memId = null;
                                        if (principal instanceof com.acorn.movielink.login.dto.Member) {
                                            memId = ((com.acorn.movielink.login.dto.Member) principal).getMemId();
                                        } else if (principal instanceof UserDetails) {
                                            String email = ((UserDetails) principal).getUsername();
                                            memId = memberService.findByEmail(email)
                                                    .map(Member::getMemId)
                                                    .orElse(null);
                                        }
                                        //실시간 동접자
//                                if (memId != null) {
//                                    SessionListener.removeUser(memId);
//                                    logger.info("로그아웃 성공: 사용자 ID - {}", memId);
//                                }
                                    }
                                })
                                .permitAll()
                )
        ;


        logger.debug("SecurityFilterChain 초기화 완료");
        return http.build();
    }

    //실시간 동접자
//    @Bean
//    public SessionRegistry sessionRegistry() {
//        return new SessionRegistryImpl();
//    }
//
//    @Bean
//    public HttpSessionListener httpSessionListener() {
//        return new SessionListener();
//    }
}
