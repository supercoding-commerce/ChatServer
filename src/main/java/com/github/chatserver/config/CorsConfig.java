package com.github.chatserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 origin (도메인) 설정
        config.addAllowedOriginPattern("*"); // 모든 도메인 허용

        // 허용할 HTTP 메서드 설정
        config.addAllowedMethod(HttpMethod.OPTIONS);
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");

        // 허용할 헤더 설정
        config.addAllowedHeader("*"); // 모든 헤더 허용

        // 다양한 CORS 설정 추가 가능

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
