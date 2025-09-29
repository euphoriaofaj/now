package com.morago.backend.entity;

import com.morago.backend.listener.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "translator_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TranslatorProfile extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_of_birth")
    @Past
    private LocalDate dateOfBirth;

    @Column(name = "email", length = 320, unique = true)
    @Email
    private String email;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "is_online")
    private Boolean isOnline;

    @Column(name = "level_of_korean", length = 200)
    private String levelOfKorean;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "translator_languages",
            joinColumns = @JoinColumn(name = "translator_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"translator_profile_id", "language_id"})
    )
    @Builder.Default
    private Set<Language> languages = new java.util.HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "translator_themes",
            joinColumns = @JoinColumn(name = "translator_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"translator_profile_id", "theme_id"})
    )
    @Builder.Default
    private Set<Theme> themes = new java.util.HashSet<>();

    @Column(name = "rating_avg", nullable = false)
    @Builder.Default
    private java.math.BigDecimal ratingAvg = java.math.BigDecimal.ZERO; // 0.00..5.00

    @Column(name = "rating_count", nullable = false)
    @Builder.Default
    private Integer ratingCount = 0;
}