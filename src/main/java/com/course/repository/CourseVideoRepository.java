package com.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.course.entity.CourseVideo;

@Repository
public interface CourseVideoRepository extends JpaRepository<CourseVideo, Long>, JpaSpecificationExecutor<CourseVideo> {
    
}
