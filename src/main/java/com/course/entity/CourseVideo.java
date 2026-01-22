package com.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "course_videos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "video_title")
    private String videoTitle; // Contoh: "Eps 1: Install AutoCAD"

    @Column(name = "sequence_order")
    private Integer sequenceOrder; // Urutan video (1, 2, 3...)

    @Column(name = "telegram_file_id")
    private String telegramFileId;

    @Column(name = "telegram_message_id")
    private String telegramMessageId;

    @Column(name = "telegram_video_url")
    private String telegramVideoUrl;

    @Column(name = "video_duration")
    private Integer videoDuration; // durasi video dalam detik

    @Column(name = "video_file_size")
    private Long videoFileSize; // ukuran file video dalam bytes

    @Column(name = "video_preview_url")
    private String videoPreviewUrl; // URL thumbnail/preview

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}