package com.videogenerator.entity;
import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "upload_history")
public class UploadHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uploadId;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Long videoId;
    @Column(columnDefinition = "CLOB")
    private String originalFileNames;
    @Column(nullable = false)
    private Integer numberOfPhotos;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable = false)
    private String uploadStatus;
    private String errorMessage;

    public Long getUploadId() {
        return uploadId;
    }
    public void setUploadId(Long uploadId) {
        this.uploadId = uploadId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getVideoId() {
        return videoId;
    }
    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }
    public String getOriginalFileNames() {
        return originalFileNames;
    }
    public void setOriginalFileNames(String originalFileNames) {
        this.originalFileNames = originalFileNames;
    }
    public Integer getNumberOfPhotos() {
        return numberOfPhotos;
    }
    public void setNumberOfPhotos(Integer numberOfPhotos) {
        this.numberOfPhotos = numberOfPhotos;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public String getUploadStatus() {
        return uploadStatus;
    }
    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}