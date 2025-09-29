package com.morago.backend.entity;

import com.morago.backend.entity.enumFiles.FileCategory;
import com.morago.backend.entity.enumFiles.FileVisibility;
import com.morago.backend.listener.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_title")
    private String originalTitle;

    @Column(name = "path", length = 512, nullable = false, unique = true)
    private String path;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 32, nullable = false)
    private FileCategory category; // AVATAR / ICON / DOC

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", length = 16, nullable = false)
    private FileVisibility visibility; // PUBLIC / PRIVATE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}