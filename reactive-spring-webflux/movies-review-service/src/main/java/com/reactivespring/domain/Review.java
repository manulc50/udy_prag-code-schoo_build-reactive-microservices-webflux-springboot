package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Document(value = "reviews")
public class Review {

    @Id
    private String reviewId;

    @NotNull(message = "review.movieInfoId must not be null")
    private Long movieInfoId;
    private String comment;

    @Min(value = 0L, message = "review.rating: please pass a non-negative value")
    private Double rating;
}
