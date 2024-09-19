package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Review {
    private String reviewId;
    private Long movieInfoId;
    private String comment;

    //@Min(value = 0L, message = "rating.negative : rating is negative and please pass a non-negative value")
    private Double rating;
}