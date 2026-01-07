package com.course.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.course.dto.ApiResponse;
import com.course.dto.post.PostRequest;
import com.course.dto.post.PostResponse;
import com.course.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("api/post")
@RequiredArgsConstructor
@Slf4j
public class ApiPost {

    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@RequestBody PostRequest request, Authentication authentication) {
        PostResponse postResponse = postService.createPost(request, authentication);

        ApiResponse<PostResponse> response = new ApiResponse<>(
            200,
            "Postingan berhasil dibuat",
            postResponse
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
