package com.reactivespring.router;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.GlobalExceptionHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = { ReviewRouter.class, ReviewHandler.class, GlobalExceptionHandler.class })
class ReviewRouterTest {
    private static final String REVIEW_URL = "/v1/reviews";

    @MockBean
    ReviewRepository reviewRepository;

    @Autowired
    WebTestClient webTestClient;

    @Test
    void addReviewTest() {
        // given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        var savedReview = new Review("mockId", 1L, "Awesome Movie", 9.0);

        when(reviewRepository.save(isA(Review.class))).thenReturn(Mono.just(savedReview));

        // when
        webTestClient.post()
                .uri(REVIEW_URL)
                .bodyValue(review)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var responseBody = reviewEntityExchangeResult.getResponseBody();

                    assertNotNull(responseBody);
                    assertNotNull(responseBody.getReviewId());
                    assertEquals("mockId", responseBody.getReviewId());
                });
    }

    @Test
    void addReviewWithValidationsTest() {
        // given
        var review = new Review(null, null, "Awesome Movie", -9.0);

        // when
        webTestClient.post()
                .uri(REVIEW_URL)
                .bodyValue(review)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("review.movieInfoId must not be null,review.rating: please pass a non-negative value");
    }

    @Test
    void getAllReviewsTest() {
        // given
        var reviewList = Arrays.asList(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));

        when(reviewRepository.findAll()).thenReturn(Flux.fromIterable(reviewList));

        // when
        webTestClient.get()
                .uri(REVIEW_URL)
                .exchange()
                // then
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    void updateReviewTest() {
        // given
        var reviewId = "abc";

        var review = new Review(null, 1L, "Awesome Movie1", 9.0);

        var existingReview = new Review(reviewId, 1L, "Awesome Movie", 9.0);

        var updatedReview = new Review(reviewId, 1L, "Awesome Movie1", 9.0);

        when(reviewRepository.findById(isA(String.class))).thenReturn(Mono.just(existingReview));
        when(reviewRepository.save(isA(Review.class))).thenReturn(Mono.just(updatedReview));

        // when
        webTestClient.put()
                .uri(REVIEW_URL + "/{id}", reviewId)
                .bodyValue(review)
                .exchange()
                // then
                .expectStatus().is2xxSuccessful()
                .expectBody(Review.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var responseBody = movieInfoEntityExchangeResult.getResponseBody();

                    assertNotNull(responseBody);
                    assertEquals(reviewId, responseBody.getReviewId());
                    assertEquals("Awesome Movie1", responseBody.getComment());
                });
    }

    @Test
    void updateReviewNotFoundTest() {
        // given
        var reviewId = "abc";

        var review = new Review(null, 1L, "Awesome Movie1", 9.0);

        when(reviewRepository.findById(isA(String.class))).thenReturn(Mono.empty());

        // when
        webTestClient.put()
                .uri(REVIEW_URL + "/{id}", reviewId)
                .bodyValue(review)
                .exchange()
                // then
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("Review not found for the given id " + reviewId);
    }

    @Test
    void deleteReviewTest() {
        // given
        var reviewId = "abc";

        when(reviewRepository.deleteById(isA(String.class))).thenReturn(Mono.empty());

        // when
        webTestClient.delete()
                .uri(REVIEW_URL + "/{id}", reviewId)
                .exchange()
                // then
                .expectStatus().isNoContent();
    }
}
