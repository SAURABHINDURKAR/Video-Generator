package com.videogenerator.service;

import com.videogenerator.entity.UploadHistory;
import com.videogenerator.entity.Video;
import com.videogenerator.entity.User;
import com.videogenerator.repository.UploadHistoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class UploadService {

    private final UploadHistoryRepository uploadHistoryRepository;
    private final AuthService authService;
    private final VideoService videoService;

    @Value("${upload.directory:uploads}")
    private String uploadDirectory;

    @Value("${upload.max-file-size:104857600}")
    private Long maxFileSize; // 100MB default

    /**
     * Upload a file for a video
     */
    @Transactional
    public UploadHistory uploadFile(Long videoId, MultipartFile file) {
        log.info("Uploading file for video ID: {}, File: {}", videoId, file.getOriginalFilename());

        // Validate file
        if (file.isEmpty()) {
            log.error("Uploaded file is empty");
            throw new RuntimeException("File cannot be empty");
        }

        if (file.getSize() > maxFileSize) {
            log.error("File size exceeds limit: {} > {}", file.getSize(), maxFileSize);
            throw new RuntimeException("File size exceeds maximum limit of " + (maxFileSize / (1024 * 1024)) + "MB");
        }

        // Get video and current user
        Video video = videoService.getVideoById(videoId);
        User currentUser = authService.getCurrentUser();

        // Check if user owns the video
        if (!video.getUser().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized upload attempt for video ID: {}", videoId);
            throw new RuntimeException("Unauthorized: You can only upload files to your own videos");
        }

        try {
            // Create upload directory if it doesn't exist
            File dir = new File(uploadDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
                log.info("Upload directory created: {}", uploadDirectory);
            }

            // Generate unique filename
            String uniqueFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String filePath = uploadDirectory + File.separator + uniqueFilename;

            // Save file to disk
            file.transferTo(new File(filePath));
            log.info("File saved to disk: {}", filePath);

            // Create upload history record
            UploadHistory uploadHistory = new UploadHistory();
            uploadHistory.setVideo(video);
            uploadHistory.setUser(currentUser);
            uploadHistory.setFileName(file.getOriginalFilename());
            uploadHistory.setFilePath(filePath);
            uploadHistory.setFileSize(file.getSize());
            uploadHistory.setFileType(file.getContentType());
            uploadHistory.setUploadStatus(UploadHistory.UploadStatus.COMPLETED);
            uploadHistory.setCreatedAt(LocalDateTime.now());
            uploadHistory.setUpdatedAt(LocalDateTime.now());

            UploadHistory savedUpload = uploadHistoryRepository.save(uploadHistory);
            log.info("Upload history recorded with ID: {}", savedUpload.getId());

            return savedUpload;

        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            throw new RuntimeException("Error uploading file: " + e.getMessage());
        }
    }

    /**
     * Get upload history for current user with pagination
     */
    public Page<UploadHistory> getUserUploadHistory(int page, int size, String sortBy, String sortDirection) {
        log.info("Fetching upload history - page: {}, size: {}", page, size);

        User currentUser = authService.getCurrentUser();
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return uploadHistoryRepository.findByUser(currentUser, pageable);
    }

    /**
     * Get upload history for specific video
     */
    public List<UploadHistory> getVideoUploadHistory(Long videoId) {
        log.info("Fetching upload history for video ID: {}", videoId);

        Video video = videoService.getVideoById(videoId);
        User currentUser = authService.getCurrentUser();

        // Check if user owns the video
        if (!video.getUser().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized access to upload history for video ID: {}", videoId);
            throw new RuntimeException("Unauthorized: You can only access your own videos' upload history");
        }

        return uploadHistoryRepository.findByVideoOrderByCreatedAtDesc(video);
    }

    /**
     * Get upload by ID
     */
    public UploadHistory getUploadById(Long id) {
        log.info("Fetching upload with ID: {}", id);
        return uploadHistoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Upload not found with ID: {}", id);
                    return new RuntimeException("Upload not found");
                });
    }

    /**
     * Delete upload file (soft delete)
     */
    @Transactional
    public void deleteUpload(Long id) {
        log.info("Deleting upload with ID: {}", id);

        UploadHistory upload = getUploadById(id);
        User currentUser = authService.getCurrentUser();

        // Check if user owns the upload
        if (!upload.getUser().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized delete attempt for upload ID: {}", id);
            throw new RuntimeException("Unauthorized: You can only delete your own uploads");
        }

        // Delete file from disk
        File file = new File(upload.getFilePath());
        if (file.exists()) {
            if (file.delete()) {
                log.info("File deleted from disk: {}", upload.getFilePath());
            } else {
                log.warn("Failed to delete file from disk: {}", upload.getFilePath());
            }
        }

        // Delete database record
        uploadHistoryRepository.deleteById(id);
        log.info("Upload record deleted from database with ID: {}", id);
    }

    /**
     * Get total upload size for current user
     */
    public Long getUserTotalUploadSize() {
        User currentUser = authService.getCurrentUser();
        Page<UploadHistory> uploads = uploadHistoryRepository.findByUser(currentUser, PageRequest.of(0, Integer.MAX_VALUE));

        return uploads.getContent().stream()
                .mapToLong(UploadHistory::getFileSize)
                .sum();
    }

    /**
     * Update upload status
     */
    @Transactional
    public UploadHistory updateUploadStatus(Long id, UploadHistory.UploadStatus status) {
        log.info("Updating upload status - ID: {}, Status: {}", id, status);

        UploadHistory upload = getUploadById(id);
        upload.setUploadStatus(status);
        upload.setUpdatedAt(LocalDateTime.now());

        return uploadHistoryRepository.save(upload);
    }
}
