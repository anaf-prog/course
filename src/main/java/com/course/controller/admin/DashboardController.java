package com.course.controller.admin;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.course.entity.Enrollment;
import com.course.entity.Post;
import com.course.entity.User;
import com.course.repository.CourseRepository;
import com.course.repository.EnrollmentRepository;
import com.course.repository.PostRepository;
import com.course.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 1. Get statistics
        long totalUsers = userRepository.count();
        long totalCourses = courseRepository.count();
        long totalPosts = postRepository.count();
        long totalEnrollments = enrollmentRepository.count();
        
        // 2. Recent activities
        LocalDateTime lastWeek = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        List<User> newUsers = userRepository.findByCreatedAtAfter(lastWeek);
        List<Enrollment> newEnrollments = enrollmentRepository.findByEnrolledAtAfter(lastWeek);
        
        // 3. Latest posts
        List<Post> latestPosts = postRepository.findTop5WithUser(PageRequest.of(0, 5));
        
        // 4. Add to model
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("totalPosts", totalPosts);
        model.addAttribute("totalEnrollments", totalEnrollments);
        model.addAttribute("newUsers", newUsers);
        model.addAttribute("newEnrollments", newEnrollments);
        model.addAttribute("latestPosts", latestPosts);
        
        return "admin/dashboard";
    }
    
}
