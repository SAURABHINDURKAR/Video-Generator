package com.videogenerator.controller;

import com.videogenerator.entity.Video;
import com.videogenerator.model.ApiResponse;
import com.videogenerator.model.VideoRequest;
import com.videogenerator.service.VideoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos")
@Slf4j
@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class VideoController {

    private final VideoService videoService;

    /**
     * Create a new video
     * POST /api/videos
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EDITOR')")
    public ResponseEntity<?> createVideo(@Valid @RequestBody VideoRequest request) {
        log.info("Create video request received: {}", request.getTitle());

        try {
            Video video = videoService.createVideo(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Video created successfully", video));
        } catch (Exception e) {
            log.error("Error creating video: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create video: " + e.getMessage(), "/api/videos"));
        }
    }

    /**
     * Get all videos for current user with pagination
     * GET /api/videos?page=0&size=10&sortBy=createdAt&sortDirection=DESC
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EDITOR')")
    public ResponseEntity<?> getUserVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        log.info("Get user videos request received - page: {}, size: {}", page, size);

        try {
            Page<Video> videos = videoService.getUserVideos(page, size, sortBy, sortDirection);
            return ResponseEntity.ok(ApiResponse.success("Videos retrieved successfully", videos));
        } catch (Exception e) {
            log.error("Error retrieving videos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve videos: " + e.getMessage(), "/api/videos"));
        }
    }

    /**
     * Get video by ID
     * GET /api/videos/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EDITOR')")
    public ResponseEntity<?> getVideoById(@PathVariable Long id) {
        log.info("Get video by ID request received: {}", id);

        try {
            Video video = videoService.getVideoById(id);
            return ResponseEntity.ok(ApiResponse.success("Video retrieved successfully", video));
        } catch (RuntimeException e) {
            log.error("Video not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), "/api/videos/" + id));
        } catch (Exception e) {
            log.error("Error retrieving video: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve video: " + e.getMessage(), "/api/videos/" + id));
        }
    }

    /**
     * Update video
     * PUT /api/videos/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EDITOR')")
    public ResponseEntity<?> updateVideo(@PathVariable Long id, @Valid @RequestBody VideoRequest request) {
        log.info("Update video request received: {}", id);

        try {
            Video video = videoService.updateVideo(id, request);
            return ResponseEntity.ok(ApiResponse.success("Video updated successfully", video));
        } catch (RuntimeException e) {
            log.error("Error updating video: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage(), "/api/videos/" + id));
        } catch (Exception e) {
            log.error("Error updating video: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update video: " + e.getMessage(), "/api/videos/" + id));
        }
    }

    /**
     * Delete video (soft delete)
     * DELETE /api/videos/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EDITOR')")
    public ResponseEntity<?> deleteVideo(@PathVariable Long id) {
        log.info("Delete video request received: {}", id);

        try {
            videoService.deleteVideo(id);
            return ResponseEntity.ok(ApiResponse.success("Video deleted successfully", null));
        } catch (RuntimeException e) {
            log.error("Error deleting video: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage(), "/api/videos/" + id));
        } catch (Exception e) {
            log.error("Error deleting video: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete video: " + e.getMessage(), "/api/videos/" + id));
        }
    }

    /**
     * Get video status
     * GET /api/videos/{id}/status
     */
    @GetMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EDITOR')")
    public ResponseEntity<?> getVideoStatus(@PathVariable Long id) {
        log.info("Get video status request received: {}", id);

        try {
            Video video = videoService.getVideoById(id);
            return ResponseEntity.ok(ApiResponse.success("Video status retrieved", 
                    new Object() {
                        public Long videoId = video.getId();
                        public String status = video.getStatus().toString();
                        public String title = video.getTitle();
                    }));
        } catch (Exception e) {
            log.error("Error retrieving video status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve video status: " + e.getMessage(), "/api/videos/" + id));
        }
    }

    /**
     * Archive video
     * POST /api/videos/{id}/archive
     */
    @PostMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EDITOR')")
    public ResponseEntity<?> archiveVideo(@PathVariable Long id) {
        log.info("Archive video request received: {}", id);

        try {
            Video video = videoService.archiveVideo(id);
            return ResponseEntity.ok(ApiResponse.success("Video archived successfully", video));
        } catch (Exception e) {
            log.error("Error archiving video: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to archive video: " + e.getMessage(), "/api/videos/" + id));
        }
    }

    /**
     * Get video count for current user
     * GET /api/videos/count/total
     */
    @GetMapping("/count/total")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EDITOR')")
    public ResponseEntity<?> getVideoCount() {
        log.info("Get video count request received");

        try {
            long count = videoService.getUserVideoCount();
            return ResponseEntity.ok(ApiResponse.success("Video count retrieved", 
                    new Object() {
                        public long totalVideos = count;
                    }));
        } catch (Exception e) {
            log.error("Error retrieving video count: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve video count: " + e.getMessage(), "/api/videos/count/total"));
        }
    }
}
