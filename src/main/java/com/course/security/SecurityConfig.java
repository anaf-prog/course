package com.course.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.course.service.OAuth2LoginSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // CSRF untuk web, disable untuk API
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") // API no CSRF
            )
            
            // SESSION untuk web (OAuth2 butuh ini!)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
            )

            // Authorization
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll()

                // PUBLIC (Web)
                .requestMatchers("/", "/login", "/login.html").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                
                // PUBLIC (API & OAuth2) 
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                
                // WEB ROUTES (pakai session)
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // API ROUTES (pakai JWT)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                
                .anyRequest().authenticated()
            )

            // FORM LOGIN untuk WEB
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/form_login")  
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )

            // OAUTH2 LOGIN (GOOGLE)
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login") // Redirect ke login page kalo belum auth
                .successHandler(oAuth2LoginSuccessHandler)
                .failureUrl("/login?error=true") // Kalo gagal
                .authorizationEndpoint(authorization -> authorization
                    .baseUri("/oauth2/authorization") // URL untuk initiate OAuth2
                )
                .redirectionEndpoint(redirection -> redirection
                    .baseUri("/login/oauth2/code/*") // Callback URL
                )
            )

            // LOGOUT untuk WEB
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true) // Invalidate session
                .deleteCookies("JSESSIONID") // Hapus session cookie
                .permitAll()
            )

            // JWT FILTER untuk API
            .addFilterBefore(jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
