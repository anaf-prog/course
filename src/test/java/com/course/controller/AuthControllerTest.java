package com.course.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import com.course.PrettyPrintHandler;
import com.course.dto.LoginRequest;
import com.course.entity.User;
import com.course.entity.User.Role;
import com.course.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Optional;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
    }

    @Test
    void testLoginSuccess() throws Exception {

        User user = User.builder()
            .email("izzatannafs6@gmail.com")
            .password(passwordEncoder.encode("Admin123!@"))
            .fullName("Test User")
            .role(Role.ADMIN)
        .build();

        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("izzatannafs6@gmail.com");
        request.setPassword("Admin123!@");

        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andDo(PrettyPrintHandler.printBodyOnly())
            .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("success"));

        boolean userExists = userRepository.existsByEmail("izzatannafs6@gmail.com");
        System.out.println(">>> Verifikasi DB: Apakah user masuk? " + userExists);

        Optional<User> userDb = userRepository.findByEmail(request.getEmail());
        System.out.println(">>> Verifikasi User dari DB : " + userDb);

        Assertions.assertTrue(userExists, "User ada didatabase");
    }

    @Test
    void testLoginGagal() throws Exception {

        User user = User.builder()
                .email("izzatannafs6@gmail.com")
                .password(passwordEncoder.encode("Admin123!@"))
                .fullName("Test User")
                .role(Role.ADMIN)
                .build();

        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("izzatannafs7@gmail.com");
        request.setPassword("Admin123!@");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andDo(PrettyPrintHandler.printBodyOnly())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").isNotEmpty());

        boolean userExists = userRepository.existsByEmail("izzatannafs6@gmail.com");
        System.out.println(">>> Verifikasi DB: Apakah user masuk? " + userExists);

        Optional<User> userDb = userRepository.findByEmail(request.getEmail());
        System.out.println(">>> Verifikasi User dari DB : " + userDb);

        Assertions.assertTrue(userExists, "User ada didatabase");

    }

}
