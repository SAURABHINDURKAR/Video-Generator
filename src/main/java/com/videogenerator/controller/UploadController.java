package com.videogenerator.controller;

import com.videogenerator.entity.UploadHistory;
import com.videogenerator.model.ApiResponse;
import com.videogenerator.service.UploadService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/uploads")
@Slf4j
@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UploadController {

    private final UploadService uploadService;

    /**
     * Upload file for a video
     * POST /api/uploads/video/{videoId}
     */
    @PostMapping("/video/{videoId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EDITOR')")
    public ResponseEntity<?> uploadFile(
            @PathVariable Long videoId,
            @RequestParam("file") MultipartFile file) {
        log.info("Upload file request received for video ID: {}, file: {}", videoId, file.getOriginalFilename());

        try {
            UploadHistory uploadHistory = uploadService.uploadFile(videoId, file);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("File uploaded successfully", uploadHistory));
        } catch (RuntimeException e) {
            log.error("Error uploading file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), "/api/uploads/video/" + videoId));
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload file: " + e.getMessage(), "/api/uploads/video/" + videoId));
        }
    }

    /**
     * Get upload history for current user with pagination
     * GET /api/uploads/history?page=0&size=10&sortBy=createdAt&sortDirection=DESC
     */
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EDITOR')")
    public ResponseEntity<?> getUserUploadHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        log.info("Get upload history request received - page: {}, size: {}", page, size);

        try {
            Page<UploadHistory> history = uploadService.getUserUploadHistory(page, size, sortBy, sortDirection);
            return ResponseEntity.ok(ApiResponse.success("Upload history retrieved", history));
        } catch (Exception e) {
            log.error("Error retrieving upload history: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve upload history: " + e.getMessage(), "/api/uploads/history"));
        }
    }

    /**
     * Get upload history for specific video
     * GET /api/uploads/video/{videoId}/history
     */
    @GetMapping("/video/{videoId}/history")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EDITOR')")
    public ResponseEntity<?> getVideoUploadHistory(@PathVariable Long videoId) {
        log.info("Get video upload history request received for video ID: {}", videoId);

        try {
            List<UploadHistory> history = uploadService.getVideoUploadHistory(videoId);
            return ResponseEntity.ok(ApiResponse.success("Video upload history retrieved", history));
        } catch (RuntimeException e) {
            log.error("Error retrieving video upload history: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage(), "/api/uploads/video/" + videoId + "/history"));
        } catch (Exception e) {
            log.error("Error retrieving video upload history: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve video upload history: " + e.getMessage(), "/api/uploads/video/" + videoId + "/history"));
        }
    }

    /**
     * Get upload by ID
     * GET /api/uploads/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EDITOR')")
    public ResponseEntity<?> getUploadById(@PathVariable Long id) {
        log.info("Get upload by ID request received: {}", id);

        try {
            UploadHistory upload = uploadService.getUploadById(id);
            return ResponseEntity.ok(ApiResponse.success("Upload retrieved successfully", upload));
        } catch (RuntimeException e) {
            log.error("Upload not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), "/api/uploads/" + id));
        } catch (Exception e) {
            log.error("Error retrieving upload: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve upload: " + e.getMessage(), "/api/uploads/" + id));
        }
    }

    /**
     * Delete upload
     * DELETE /api/uploads/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EDITOR')")
    public ResponseEntity<?> deleteUpload(@PathVariable Long id) {
        log.info("Delete upload request received: {}", id);

        try {
            uploadService.deleteUpload(id);
            return ResponseEntity.ok(ApiResponse.success("Upload deleted successfully", null));
        } catch (RuntimeException e) {
            log.error("Error deleting upload: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage(), "/api/uploads/" + id));
        } catch (Exception e) {
            log.error("Error deleting upload: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete upload: " + e.getMessage(), "/api/uploads/" + id));
        }
    }

    /**
     * Get upload statistics for current user
     * GET /api/uploads/stats
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EDITOR')")
    public ResponseEntity<?> getUploadStats() {
        log.info("Get upload stats request received");

        try {
            Long totalSize = uploadService.getUserTotalUploadSize();
            return ResponseEntity.ok(ApiResponse.success("Upload statistics retrieved", 
                    new Object() {
                        public Long totalUploadSize = totalSize;
                        public String totalUploadSizeMB = String.format("%.2f MB", totalSize / (1024.0 * 1024.0));
                    }));
        } catch (Exception e) {
            log.error("Error retrieving upload stats: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve upload stats: " + e.getMessage(), "/api/uploads/stats"));
        }
    }
}
