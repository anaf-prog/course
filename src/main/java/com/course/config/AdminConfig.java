package com.course.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class AdminConfig {

    @Value("${admin.default.email}")
    private String email;

    @Value("${admin.default.password}")
    private String password;

    @Value("${admin.default.fullName}")
    private String fullName;
    
}
