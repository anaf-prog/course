package com.course.controller.admin;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.course.entity.Post;
import com.course.entity.User;
import com.course.repository.PostRepository;
import com.course.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin")
@Slf4j
public class PostController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/posts/new")
    public String showNewPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "admin/posts/new-post";
    }

    @PostMapping("/posts")
    public String savePost(@ModelAttribute Post post, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            
            String email;

            if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
                OAuth2User oauthUser = oauthToken.getPrincipal();
                email = oauthUser.getAttribute("email");
            } else {
                email = authentication.getName();
            }

            User author = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

            post.setUser(author);
            post.setCreatedAt(LocalDateTime.now());
            post.setUpdatedAt(LocalDateTime.now());
            
            // Save post
            postRepository.save(post);
            
            redirectAttributes.addFlashAttribute("successMessage", "Post created successfully!");
            return "redirect:/admin/posts";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create post: " + e.getMessage());
            return "redirect:/admin/posts/new";
        }
    }
    
    @GetMapping("/posts")
    public String listPosts(Model model) {
        List<Post> posts = postRepository.findAllWithUser();
        model.addAttribute("posts", posts);
        return "admin/posts/list-post";
    }
    
    // GET: View single post
    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        model.addAttribute("post", post);
        return "admin/posts/view";
    }
    
    // GET: Edit post form
    @GetMapping("/posts/{id}/edit")
    public String editPostForm(@PathVariable Long id, Model model) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        model.addAttribute("post", post);
        return "admin/posts/edit-post";
    }
    
    // POST: Update post
    @PostMapping("/posts/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post postData, RedirectAttributes redirectAttributes) {
        try {
            Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
            
            // Update fields
            post.setTitle(postData.getTitle());
            post.setContent(postData.getContent());
            post.setImageUrl(postData.getImageUrl());
            post.setUpdatedAt(LocalDateTime.now());
            
            postRepository.save(post);
            
            redirectAttributes.addFlashAttribute("successMessage", "Post updated successfully!");
            return "redirect:/admin/posts";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update post: " + e.getMessage());
            return "redirect:/admin/posts/" + id + "/edit";
        }
    }
    
    // POST: Delete post
    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            postRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Post deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to delete post: " + e.getMessage());
        }
        return "redirect:/admin/posts";
    }
    
}
