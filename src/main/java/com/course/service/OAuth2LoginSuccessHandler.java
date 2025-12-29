package com.course.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.course.entity.User;
import com.course.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws ServletException, IOException {
        
        log.info("OAuth2 Login Successful: {}", authentication);
        
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauthToken.getPrincipal();
        
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String googleId = oauth2User.getAttribute("sub");
        
        log.info("Google User: {} - {}", email, name);
        
        // Cek atau buat user di database
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            // Buat user baru
            User newUser = User.builder()
                .email(email)
                .fullName(name)
                .googleId(googleId)
                .password(passwordEncoder.encode("Admin12345!@"))
                .role(User.Role.ADMIN) // Atau sesuaikan
                .active(true)
                .createdAt(LocalDateTime.now())
            .build();
            
            return userRepository.save(newUser);
        });
        
        // Update Google ID jika belum ada
        if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
            userRepository.save(user);
        }
        
        // Buat authorities baru berdasarkan role user
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("OAUTH2_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        
        // Buat authentication baru dengan authorities yang benar
        OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(
            oauth2User,
            authorities,
            oauthToken.getAuthorizedClientRegistrationId()
        );
        
        // Set authentication baru ke SecurityContext
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        
        log.info("User {} has role: {}", email, user.getRole());
        
        // Redirect ke dashboard
        response.sendRedirect("/admin/dashboard");
    }
}