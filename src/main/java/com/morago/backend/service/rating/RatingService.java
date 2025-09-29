package com.morago.backend.service.rating;

import com.morago.backend.dto.RatingUpsertRequest;

public interface RatingService {
    void upsertMyRating(Long translatorProfileId, RatingUpsertRequest dto);
    void deleteMyRating(Long translatorProfileId);

}