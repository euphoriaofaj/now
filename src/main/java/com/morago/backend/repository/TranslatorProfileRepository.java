package com.morago.backend.repository;

import com.morago.backend.entity.TranslatorProfile;
import com.morago.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TranslatorProfileRepository extends JpaRepository<TranslatorProfile, Long> {

    Optional<TranslatorProfile> findByUserId(Long userId);
    Optional<TranslatorProfile> findByUser(User user);
    boolean existsByUser_Id(Long userId);


    List<TranslatorProfile> findByIsVerifiedTrue();

    List<TranslatorProfile> findByIsOnlineTrue();

    List<TranslatorProfile> findByIsVerifiedTrueAndIsOnlineTrue();

    List<TranslatorProfile> findByEmailContainingIgnoreCase(String email);

    List<TranslatorProfile> findByLevelOfKoreanContainingIgnoreCase(String levelOfKorean);

    List<TranslatorProfile> findByLanguages_Id(Long languageId);

    List<TranslatorProfile> findByThemes_Id(Long themeId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update TranslatorProfile t " +
            "set t.ratingAvg = :avg, t.ratingCount = :count " +
            "where t.id = :id")
    void updateRatingStats(@Param("id") Long id,
                          @Param("avg") BigDecimal avg,
                          @Param("count") Integer count);
}
