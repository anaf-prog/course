package com.course.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import com.course.dto.course.CourseRequest;
import com.course.dto.course.CourseResponse;
import com.course.dto.course.VideoResponse;
import com.course.entity.Course;
import com.course.entity.CourseVideo;
import com.course.entity.User;
import com.course.error.custom.LoginException;
import com.course.repository.CourseRepository;
import com.course.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${telegram.channel.url}")
    private String telegramChannelUrl;
    
    @Transactional
    public CourseResponse createCourse(CourseRequest request, Authentication authentication) {

        // 1. Ambil Email User (Admin)
        String email = (authentication instanceof OAuth2AuthenticationToken oauthToken)
                ? oauthToken.getPrincipal().getAttribute("email")
                : authentication.getName();

        User creator = userRepository.findByEmail(email)
                .orElseThrow(() -> new LoginException("User tidak ditemukan"));

        // 2. Buat Course
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice());
        course.setCreatedBy(creator);

        // 3. Mapping VideoRequest -> CourseVideo
        if (request.getVideos() != null && !request.getVideos().isEmpty()) {

            List<CourseVideo> videoEntities = request.getVideos().stream()
                .map(vReq -> CourseVideo.builder()
                        .videoTitle(vReq.getVideoTitle())
                        .sequenceOrder(vReq.getSequenceOrder())
                        .videoDuration(vReq.getVideoDuration())
                        .videoFileSize(vReq.getVideoFileSize())
                        .telegramFileId(vReq.getTelegramFileId())
                        .telegramMessageId(vReq.getTelegramMessageId())
                        .telegramVideoUrl(
                                telegramChannelUrl.endsWith("/")
                                        ? telegramChannelUrl + vReq.getTelegramMessageId()
                                        : telegramChannelUrl + "/" + vReq.getTelegramMessageId())
                        .course(course)
                        .build())
                .toList();

            course.setVideos(videoEntities);
        }

        // 4. Save Course + Videos (cascade)
        Course savedCourse = courseRepository.save(course);

        log.info("Kursus '{}' berhasil ditambahkan dengan {} video",
                savedCourse.getTitle(),
                savedCourse.getVideos().size());

        // 5. Mapping Entity -> Response
        return CourseResponse.builder()
            .title(savedCourse.getTitle())
            .description(savedCourse.getDescription())
            .price(savedCourse.getPrice())
            .videos(
                    savedCourse.getVideos().stream()
                        .map(video -> VideoResponse.builder()
                            .videoTitle(video.getVideoTitle())
                            .sequenceOrder(video.getSequenceOrder())
                            .videoDuration(video.getVideoDuration())
                            .videoFileSize(video.getVideoFileSize())
                            .telegramFileId(video.getTelegramFileId())
                            .telegramMessageId(video.getTelegramMessageId())
                            .telegramVideoUrl(video.getTelegramVideoUrl())
                            .build())
                        .toList())
            .createdAt(savedCourse.getCreatedAt())
            .build();
    }
}
