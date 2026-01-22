package com.course.error;

import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.course.dto.ApiResponse;
import com.course.error.custom.BusinessException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

     @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        log.error("Error bussiness");
        return ResponseEntity.badRequest().body(
            ApiResponse.<Void>builder()
                .statusCode(400)
                .message(ex.getMessage())
                .data(null)
            .build()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        log.error("Login gagal: Password salah");
        return ResponseEntity.badRequest().body(
            ApiResponse.<Void>builder()
                .statusCode(400)
                .message("Email/Username atau password salah")
                .data(null)
            .build()
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException ex) {
        log.error("Authentication error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiResponse.<Void>builder()
                .statusCode(401)
                .message("Akun belum terdaftar, silahkan registrasi kembali")
            .build());
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(TypeMismatchException ex) {

        log.error("Type mismatch error : {}", ex.getMessage());

        return ResponseEntity.badRequest().body(
            ApiResponse.<Void>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Format data tidak sesuai")
                .data(null)
            .build());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResource() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.error("Other error : " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiResponse.<Void>builder()
                .statusCode(500)
                .message("Other Error")
            .build());
    }
    
}
