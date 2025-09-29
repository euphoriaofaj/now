package com.morago.backend.repository;

import com.morago.backend.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserIdAndTranslatorId(Long userId, Long translatorId);

    @Query("select coalesce(avg(r.score),0) from Rating r where r.translator.id = :translatorId")
    Double avgScore(@Param("translatorId") Long translatorId);

    @Query("select count(r) from Rating r where r.translator.id = :translatorId")
    Long countByTranslator(@Param("translatorId") Long translatorId);
}
