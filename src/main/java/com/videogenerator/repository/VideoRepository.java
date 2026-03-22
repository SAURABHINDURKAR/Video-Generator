package com.videogenerator.repository;

import com.videogenerator.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByUserId(Long userId, Sort sort);
    List<Video> findByStatus(String status);
}