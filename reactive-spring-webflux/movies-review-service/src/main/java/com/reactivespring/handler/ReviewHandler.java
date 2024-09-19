package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Component
public class ReviewHandler {
    private final ReviewRepository reviewRepository;
    private final Validator validator;

    public Mono<ServerResponse> addReview(ServerRequest serverRequest) {
        // Convertimos el cuerpo de la petición http a un flujo reactivo Mono de tipo Review
        return serverRequest.bodyToMono(Review.class)
                // Versión simplificada de la expresión "review -> validate(review)"
                .doOnNext(this::validate)
                // Versión simplificada de la expresión "review -> reviewRepository.save(review)"
                .flatMap(reviewRepository::save)
                // Versión simplificada de la expresión "savedReview -> ServerResponse.status(HttpStatus.CREATED).bodyValue(savedReview)"
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    public Mono<ServerResponse> getReviews(ServerRequest serverRequest) {
        var movieInfoId = serverRequest.queryParam("movie-info-id");

        if(movieInfoId.isPresent()) {
            return ServerResponse.ok().body(reviewRepository.findAllByMovieInfoId(Long.valueOf(movieInfoId.get())),
                    Review.class);
        }
        else
            return ServerResponse.ok().body(reviewRepository.findAll(), Review.class);
    }

    public Mono<ServerResponse> uptadeReview(ServerRequest serverRequest) {
        var reviewId = serverRequest.pathVariable("id");

        return reviewRepository.findById(reviewId)
                // Una manera
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for the given id " + reviewId)))
                .flatMap(existingReview -> serverRequest.bodyToMono(Review.class)
                        .map(review -> {
                            existingReview.setComment(review.getComment());
                            existingReview.setRating(review.getRating());

                            return existingReview;
                        })
                        // Versión simplificada de la expresión "review -> reviewRepository.save(review)"
                        .flatMap(reviewRepository::save)
                        // Versión simplificada de la expresión "updatedReview -> ServerResponse.ok().bodyValue(updatedReview)"
                        .flatMap(ServerResponse.ok()::bodyValue)
                );
                // Otra manera
                //.switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteReview(ServerRequest serverRequest) {
        var reviewId = serverRequest.pathVariable("id");

        return reviewRepository.deleteById(reviewId)
                .then(ServerResponse.noContent().build());
    }

    private void validate(Review review) {
        var constraintViolations = validator.validate(review);

        if(!constraintViolations.isEmpty()) {
            log.info("ContraintViolations: {}", constraintViolations);

            var errorMessages = constraintViolations.stream()
                    // Versión simplificada de la expresión "constraintViolation -> constraintViolation.getMessage()"
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(","));

            throw new ReviewDataException(errorMessages);
        }
    }
}
