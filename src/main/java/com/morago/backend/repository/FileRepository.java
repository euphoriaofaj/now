package com.morago.backend.repository;

import com.morago.backend.entity.File;
import com.morago.backend.entity.enumFiles.FileCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByPath(String path);
    Optional<File> findByUserIdAndCategory(Long userId, FileCategory category);
}