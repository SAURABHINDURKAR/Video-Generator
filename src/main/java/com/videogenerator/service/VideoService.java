package com.videogenerator.service;

import com.videogenerator.entity.Video;
import com.videogenerator.entity.User;
import com.videogenerator.model.VideoRequest;
import com.videogenerator.repository.VideoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final AuthService authService;

    /**
     * Create a new video
     */
    @Transactional
    public Video createVideo(VideoRequest request) {
        log.info("Creating new video: {}", request.getTitle());

        User currentUser = authService.getCurrentUser();

        Video video = new Video();
        video.setTitle(request.getTitle());
        video.setDescription(request.getDescription());
        video.setFilePath(request.getFilePath());
        video.setUser(currentUser);
        video.setStatus(Video.VideoStatus.PROCESSING);
        video.setIsDeleted(false);
        video.setCreatedAt(LocalDateTime.now());
        video.setUpdatedAt(LocalDateTime.now());

        Video savedVideo = videoRepository.save(video);
        log.info("Video created successfully with ID: {}", savedVideo.getId());

        return savedVideo;
    }

    /**
     * Get video by ID
     */
    public Video getVideoById(Long id) {
        log.info("Fetching video with ID: {}", id);
        return videoRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Video not found with ID: {}", id);
                    return new RuntimeException("Video not found");
                });
    }

    /**
     * Get all videos for current user with pagination
     */
    public Page<Video> getUserVideos(int page, int size, String sortBy, String sortDirection) {
        log.info("Fetching videos for user - page: {}, size: {}", page, size);

        User currentUser = authService.getCurrentUser();
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return videoRepository.findByUserAndIsDeletedFalse(currentUser, pageable);
    }

    /**
     * Get all videos for current user without pagination
     */
    public List<Video> getUserAllVideos() {
        log.info("Fetching all videos for current user");
        User currentUser = authService.getCurrentUser();
        return videoRepository.findByUserAndIsDeletedFalse(currentUser);
    }

    /**
     * Update video
     */
    @Transactional
    public Video updateVideo(Long id, VideoRequest request) {
        log.info("Updating video with ID: {}", id);

        Video video = getVideoById(id);
        User currentUser = authService.getCurrentUser();

        // Check if user owns the video
        if (!video.getUser().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized update attempt for video ID: {}", id);
            throw new RuntimeException("Unauthorized: You can only update your own videos");
        }

        video.setTitle(request.getTitle());
        video.setDescription(request.getDescription());
        video.setUpdatedAt(LocalDateTime.now());

        Video updatedVideo = videoRepository.save(video);
        log.info("Video updated successfully with ID: {}", id);

        return updatedVideo;
    }

    /**
     * Update video status
     */
    @Transactional
    public Video updateVideoStatus(Long id, Video.VideoStatus status) {
        log.info("Updating video status - ID: {}, Status: {}", id, status);

        Video video = getVideoById(id);
        video.setStatus(status);
        video.setUpdatedAt(LocalDateTime.now());

        return videoRepository.save(video);
    }

    /**
     * Set video file size and duration
     */
    @Transactional
    public Video updateVideoMetadata(Long id, Long fileSize, Integer duration) {
        log.info("Updating video metadata - ID: {}, Size: {}, Duration: {}", id, fileSize, duration);

        Video video = getVideoById(id);
        video.setFileSize(fileSize);
        video.setDuration(duration);
        video.setUpdatedAt(LocalDateTime.now());

        return videoRepository.save(video);
    }

    /**
     * Soft delete video
     */
    @Transactional
    public void deleteVideo(Long id) {
        log.info("Deleting video with ID: {}", id);

        Video video = getVideoById(id);
        User currentUser = authService.getCurrentUser();

        // Check if user owns the video
        if (!video.getUser().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized delete attempt for video ID: {}", id);
            throw new RuntimeException("Unauthorized: You can only delete your own videos");
        }

        video.setIsDeleted(true);
        video.setDeletedAt(LocalDateTime.now());
        videoRepository.save(video);

        log.info("Video deleted successfully with ID: {}", id);
    }

    /**
     * Get video count for current user
     */
    public long getUserVideoCount() {
        User currentUser = authService.getCurrentUser();
        return videoRepository.countByUserAndIsDeletedFalse(currentUser);
    }

    /**
     * Archive video
     */
    @Transactional
    public Video archiveVideo(Long id) {
        log.info("Archiving video with ID: {}", id);
        return updateVideoStatus(id, Video.VideoStatus.ARCHIVED);
    }
}
