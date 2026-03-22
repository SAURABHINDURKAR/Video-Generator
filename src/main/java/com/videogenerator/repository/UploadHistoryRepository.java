package com.videogenerator.repository;

import com.videogenerator.entity.UploadHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadHistoryRepository extends JpaRepository<UploadHistory, Long> {
    List<UploadHistory> findByUserId(Long userId);
    List<UploadHistory> findByVideoId(Long videoId);
}