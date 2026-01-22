package com.course.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.course.dto.ApiResponse;
import com.course.dto.course.CourseRequest;
import com.course.dto.course.CourseResponse;
import com.course.service.CourseService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/course")
@Slf4j
public class ApiCourse {

    @Autowired
    private CourseService courseService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CourseResponse>> create(@RequestBody CourseRequest request, Authentication authentication) {
        CourseResponse courseResponse = courseService.createCourse(request, authentication);

        ApiResponse<CourseResponse> response = new ApiResponse<>(
            200,
            "success",
            courseResponse
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
}
