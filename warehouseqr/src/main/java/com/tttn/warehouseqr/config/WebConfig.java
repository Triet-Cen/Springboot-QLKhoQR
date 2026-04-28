package com.tttn.warehouseqr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ánh xạ URL ảnh trực tiếp vào thư mục vật lý trong src
        registry.addResourceHandler("/assets/images/avatars/**")
                .addResourceLocations("file:src/main/resources/static/assets/images/avatars/");
    }
}