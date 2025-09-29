package com.morago.backend.service.rating;

import com.morago.backend.dto.RatingUpsertRequest;
import com.morago.backend.entity.Rating;
import com.morago.backend.entity.enumFiles.CallStatus;
import com.morago.backend.exception.rating.RatingRequiresSuccessfulCallException;
import com.morago.backend.mapper.RatingMapper;
import com.morago.backend.repository.CallRepository;
import com.morago.backend.repository.RatingRepository;
import com.morago.backend.repository.TranslatorProfileRepository;
import com.morago.backend.service.profile.TranslatorProfileService;
import com.morago.backend.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final TranslatorProfileRepository translatorProfileRepository;
    private final TranslatorProfileService translatorProfileService;
    private final CallRepository callRepository;
    private final UserService userService;
    private final RatingMapper ratingMapper;

    @Override
    @Transactional
    public void upsertMyRating(Long translatorProfileId, RatingUpsertRequest dto) {
        var me = userService.getCurrentUser();

        var translator = translatorProfileService.getForRatingOrThrow(translatorProfileId, me.getId());
        Long translatorUserId = translator.getUser().getId();

        boolean hadSuccessfulCall = callRepository
                .existsByCaller_IdAndRecipient_IdAndTranslatorHasJoinedTrueAndEndCallTrueAndCallStatus(
                        me.getId(), translatorUserId, CallStatus.SUCCESSFUL);

        if (!hadSuccessfulCall) {
            throw new RatingRequiresSuccessfulCallException();
        }

        Rating rating = ratingRepository.findByUserIdAndTranslatorId(me.getId(), translatorProfileId)
                .map(r -> {
                    ratingMapper.updateEntityFromDto(dto, r);
                    return r;
                })
                .orElseGet(() -> {
                    Rating r = Rating.builder()
                            .user(me)
                            .translator(translator)
                            .build();
                    ratingMapper.updateEntityFromDto(dto, r);
                    return r;
                });

        ratingRepository.save(rating);

        callRepository.findTopByCaller_IdAndRecipient_IdAndTranslatorHasJoinedTrueAndEndCallTrueOrderByCreatedAtDesc(
                me.getId(), translatorUserId
        ).ifPresent(c -> { if (!c.isUserHasRated()) c.setUserHasRated(true); });

        recalcAndUpdateTranslatorStats(translatorProfileId);
    }

    @Override
    @Transactional
    public void deleteMyRating(Long translatorProfileId) {
        var me = userService.getCurrentUser();

        ratingRepository.findByUserIdAndTranslatorId(me.getId(), translatorProfileId)
                .ifPresent(ratingRepository::delete);

        recalcAndUpdateTranslatorStats(translatorProfileId);
    }

    /* ===== helpers ===== */

    private void recalcAndUpdateTranslatorStats(Long translatorProfileId) {
        Long count = ratingRepository.countByTranslator(translatorProfileId);
        Double avg  = ratingRepository.avgScore(translatorProfileId);

        BigDecimal avgBd = BigDecimal
                .valueOf(avg == null ? 0.0 : avg)
                .setScale(2, RoundingMode.HALF_UP);

        translatorProfileRepository.updateRatingStats(
                translatorProfileId,
                avgBd,
                count == null ? 0 : count.intValue()
        );
    }
}