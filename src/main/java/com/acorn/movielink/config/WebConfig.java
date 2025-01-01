package com.acorn.movielink.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.base-dir}")
    private String uploadDir;

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
