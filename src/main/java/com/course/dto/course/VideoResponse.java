package com.course.dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoResponse {
    
    private String videoTitle;
    private Integer sequenceOrder;
    private Integer videoDuration;
    private String telegramFileId;
    private String telegramMessageId;
    private String telegramVideoUrl;
    private Long videoFileSize;
}
