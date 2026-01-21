package com.course.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.course.dto.ApiResponse;
import com.course.dto.post.PostRequest;
import com.course.dto.post.PostResponse;
import com.course.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("api/post")
@Slf4j
public class ApiPost {

    @Autowired
    private PostService postService;

    @GetMapping("/get")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getAllPosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Page<PostResponse> posts = postService.getAllPost(page, size);

        return ResponseEntity.ok(
            new ApiResponse<>(200, "Success", posts)
        );
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @ModelAttribute PostRequest request, 
            @RequestParam("imageFile") MultipartFile imageFile,
            Authentication authentication) {

        PostResponse postResponse = postService.createPost(request, imageFile, authentication);

        ApiResponse<PostResponse> response = new ApiResponse<>(
            200,
            "success",
            postResponse
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostResponse>> editPost(@PathVariable("id") Long id, @ModelAttribute PostRequest request, 
            @RequestParam("imageFile") MultipartFile imageFile, Authentication authentication) {

        PostResponse postResponse = postService.editPost(id, request, imageFile, authentication);

        ApiResponse<PostResponse> response = new ApiResponse<>(
            200,
            "success",
            postResponse
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
