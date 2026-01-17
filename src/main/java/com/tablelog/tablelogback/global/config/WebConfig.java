package com.tablelog.tablelogback.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private static final String UPLOAD_DIR = "tablelog-images" + File.separator + "tablelog";
    
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET","POST","PUT","DELETE")
                .allowedHeaders("Authorization","Content-Type")
                .exposedHeaders("Custom-Header")
                .maxAge(3600);
    }
    
    /**
     * 정적 리소스 핸들러 추가 (로컬 파일 시스템의 이미지 서빙)
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 서버 실행 위치 기준으로 업로드 폴더 경로 설정
        String currentDir = System.getProperty("user.dir");
        String uploadPath = "file:" + currentDir + File.separator + UPLOAD_DIR + File.separator;
        
        registry.addResourceHandler("/uploads/tablelog/**")
                .addResourceLocations(uploadPath);
    }
}
