package com.videogenerator.repository;

import com.videogenerator.entity.UploadHistory;
import com.videogenerator.entity.Video;
import com.videogenerator.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadHistoryRepository extends JpaRepository<UploadHistory, Long> {
    List<UploadHistory> findByVideo(Video video);
    Page<UploadHistory> findByUser(User user, Pageable pageable);
    List<UploadHistory> findByVideoOrderByCreatedAtDesc(Video video);
    long countByVideo(Video video);
}
