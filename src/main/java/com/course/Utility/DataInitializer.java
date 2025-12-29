package com.course.Utility;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.course.config.AdminConfig;
import com.course.entity.User;
import com.course.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminConfig adminConfig;

    @Override
    public void run(String... args) throws Exception {
        createDefaultAdmin();
    }
    
    private void createDefaultAdmin() {
        String adminEmail = adminConfig.getEmail();
        
        // Cek apakah admin sudah ada
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            log.debug("Admin default sudah ada: {}", adminEmail);
            return;
        }
        
        try {
            // Buat admin user
            User admin = User.builder()
                .email(adminEmail)
                .password(passwordEncoder.encode(adminConfig.getPassword()))
                .fullName(adminConfig.getFullName())
                .role(User.Role.ADMIN)
                .active(true)
                .createdAt(LocalDateTime.now())
            .build();
            
            userRepository.save(admin);
            
            log.debug("Admin default berhasil dibuat!");
            log.debug("Email: {}", adminEmail);
            log.debug("Password: {}", adminConfig.getPassword());
            
        } catch (Exception e) {
            log.error("Gagal membuat admin default: {}", e.getMessage());
        }
    }
    
}
