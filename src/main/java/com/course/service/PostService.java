package com.course.service;

import java.time.LocalDateTime;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import com.course.dto.post.PostRequest;
import com.course.dto.post.PostResponse;
import com.course.entity.Post;
import com.course.entity.User;
import com.course.repository.PostRepository;
import com.course.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public PostResponse createPost(PostRequest request, Authentication authentication) {
        String email;

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

}
