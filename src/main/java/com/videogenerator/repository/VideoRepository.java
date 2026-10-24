package com.videogenerator.repository;

import com.videogenerator.entity.Video;
import com.videogenerator.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findByUserAndIsDeletedFalse(User user, Pageable pageable);
    List<Video> findByUserAndIsDeletedFalse(User user);
    Optional<Video> findByIdAndIsDeletedFalse(Long id);
    long countByUserAndIsDeletedFalse(User user);
}
