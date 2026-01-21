package com.course.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.course.dto.post.PostRequest;
import com.course.dto.post.PostResponse;
import com.course.entity.Post;
import com.course.entity.User;
import com.course.repository.PostRepository;
import com.course.repository.UserRepository;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ImageKit imageKit;

    private String email;

    public Page<PostResponse> getAllPost(int page, int size) {
        int pageSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        return postRepository.findAllPostResponse(pageable);
    }

    @Transactional
    public PostResponse createPost(PostRequest request, MultipartFile imageFile, Authentication authentication) {

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            log.info("login via google");
            OAuth2User oAuth2User = oauthToken.getPrincipal();
            email = oAuth2User.getAttribute("email");
        } else {
            email = authentication.getName();
        }

        User author = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User tidak ditemukan"));

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCreatedAt(LocalDateTime.now());

        // --- LOGIC UPLOAD IMAGEKIT ---
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // 1. Buat objek request untuk ImageKit
                FileCreateRequest fileCreateRequest = new FileCreateRequest(
                    imageFile.getBytes(), 
                    imageFile.getOriginalFilename()
                );

                // 2. Upload menggunakan objek request tersebut
                Result result = imageKit.upload(fileCreateRequest); 
                
                // 3. Simpan hasil ke entity
                post.setImageUrl(result.getUrl());
                post.setImageId(result.getFileId());
                
                log.info("Gambar berhasil di upload");
            } catch (Exception e) {
                log.error("Gagal upload ke ImageKit: {}", e.getMessage());
                throw new RuntimeException("Gagal mengunggah gambar");
            }
        }

        post.setUser(author);

        Post savedPost = postRepository.save(post);

        PostResponse response = new PostResponse(
            savedPost.getId(),
            savedPost.getTitle(),
            savedPost.getContent(),
            savedPost.getImageUrl() != null ? savedPost.getImageUrl() : "",
            savedPost.getCreatedAt().withNano(0),
            savedPost.getUpdatedAt(),
            savedPost.getUser().getFullName()
        );

        log.info("Postingan berhasil dibuat");
        return response;

    }

    @Transactional
    public PostResponse editPost(Long postId, PostRequest request, MultipartFile imageFile, Authentication authentication) {

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2User oAuth2User = oauthToken.getPrincipal();
            email = oAuth2User.getAttribute("email");
        } else {
            email = authentication.getName();
        }

        User editor = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User tidak ditemukan"));

        Post post = postRepository.findById(postId).orElseThrow(()-> new RuntimeException("Postingan tidak ditemukan"));

        if (!post.getUser().getId().equals(editor.getId())) {
            log.info("Tidak ada akses untuk edit posingan");
            throw new AccessDeniedException("Postingan tidak dapat di edit");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                if (post.getImageId() != null) {
                    imageKit.deleteFile(post.getImageId());
                }
                
                // 1. Buat objek request untuk ImageKit
                FileCreateRequest fileCreateRequest = new FileCreateRequest(
                    imageFile.getBytes(), 
                    imageFile.getOriginalFilename()
                );

                // 2. Upload menggunakan objek request tersebut
                Result result = imageKit.upload(fileCreateRequest); 
                
                // 3. Simpan hasil ke entity
                post.setImageUrl(result.getUrl());
                post.setImageId(result.getFileId());
                
                log.info("Gambar berhasil di upload");
            } catch (Exception e) {
                log.error("Gagal upload ke ImageKit: {}", e.getMessage());
                throw new RuntimeException("Gagal mengunggah gambar");
            }
        }

        post.setUser(editor);

        Post updatePost = postRepository.save(post);

        log.info("Postingan berhasil diupdate");

        return new PostResponse(
            updatePost.getId(),
            updatePost.getTitle(),
            updatePost.getContent(),
            updatePost.getImageUrl() != null ? updatePost.getImageUrl() : "",
            updatePost.getCreatedAt().withNano(0),
            updatePost.getUpdatedAt().withNano(0),
            editor.getFullName()

        );

    }

}
