package com.acorn.movielink.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.base-dir}")
    private String uploadDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 URL(/**)에 대해 CORS 허용
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // profile-images 경로 처리
        registry.addResourceHandler("/upload/profile-images/**")
                .addResourceLocations("file:///" + uploadDir + "profile-images/");

        // post-images 경로 처리
        registry.addResourceHandler("/upload/post-images/**")
                .addResourceLocations("file:///" + uploadDir + "post-images/");
    }
}
