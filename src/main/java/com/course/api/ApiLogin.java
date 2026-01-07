package com.course.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.course.dto.ApiResponse;
import com.course.dto.LoginRequest;
import com.course.dto.LoginResponse;
import com.course.entity.User;
import com.course.repository.UserRepository;
import com.course.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class ApiLogin {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User tidak ditemukan"));

        String token = jwtUtil.generateToken(user);

        LoginResponse loginResponse = new LoginResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole().name(),
            token
        );

        ApiResponse<LoginResponse> response = new ApiResponse<>(
            200,
            "success",
            loginResponse
        );

        log.info("Login via API sukses");

        return ResponseEntity.ok(response);
    }
    
}
