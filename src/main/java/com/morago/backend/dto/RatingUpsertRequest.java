package com.morago.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record RatingUpsertRequest(
        @Min(1) @Max(5) Integer score,
        @Size(max = 1000) String comment
) { }

