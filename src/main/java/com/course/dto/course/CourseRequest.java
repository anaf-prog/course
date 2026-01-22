package com.course.dto.course;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseRequest {

    private String title;
    private String description;
    private BigDecimal price;
    private List<VideoRequest> videos;
}
