package com.acorn.movielink.config;

import com.acorn.movielink.login.service.CustomAuthenticationSuccessHandler;
import com.acorn.movielink.login.service.CustomOAuth2UserService;
import com.acorn.movielink.login.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthenticationSuccessHandler successHandler;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          CustomOAuth2UserService customOAuth2UserService,
                          PasswordEncoder passwordEncoder,
                          CustomAuthenticationSuccessHandler successHandler) {
        this.userDetailsService = userDetailsService;
        this.customOAuth2UserService = customOAuth2UserService;
        this.passwordEncoder = passwordEncoder;
        this.successHandler = successHandler;
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
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/uploads/**", "/img/**", "/css/**", "/js/**", "/webjars/**", "/fonts/**").permitAll()
                        .requestMatchers("/signup", "/forgot_password", "/reset_password").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()//.authenticated()
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
                                logger.info("로그아웃 성공: 사용자 - {}", authentication.getName());
                            }
                        })
                        .permitAll()
                );

        logger.debug("SecurityFilterChain 초기화 완료");
        return http.build();
    }
}
