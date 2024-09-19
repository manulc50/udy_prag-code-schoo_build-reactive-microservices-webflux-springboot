package com.reactivespring.router;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReviewRouterIntgTest {
    private static final String REVIEW_URL = "v1/reviews";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        var reviewList = Arrays.asList(
                new Review("abc", 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));

        reviewRepository.saveAll(reviewList).blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll().block();
    }

    @Test
    void addReviewTest() {
        // given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        // when
        webTestClient.post()
                .uri(REVIEW_URL)
                .bodyValue(review)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var savedReview = reviewEntityExchangeResult.getResponseBody();

                    assertNotNull(savedReview);
                    assertNotNull(savedReview.getReviewId());
                });
    }

    @Test
    void getAllReviewsTest() {
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
    void getAllReviewsByMovieInfoIdTest() {
        // given
        var uri = UriComponentsBuilder.fromUriString(REVIEW_URL)
                .queryParam("movie-info-id", 1L)
                .buildAndExpand().toUri();

        // when
        webTestClient.get()
                .uri(uri.toString())
                .exchange()
                // then
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(2);
    }

    @Test
    void updateReviewTest() {
        // given
        var reviewId = "abc";

        var review = new Review(null, 1L, "Not an Awesome Movie", 8.0);

        // when
        webTestClient.put()
                .uri(REVIEW_URL + "/{id}", reviewId)
                .bodyValue(review)
                .exchange()
                // then
                .expectStatus().is2xxSuccessful()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var updatedReview = reviewEntityExchangeResult.getResponseBody();

                    assertNotNull(updatedReview);
                    assertEquals(reviewId, updatedReview.getReviewId());
                    assertEquals("Not an Awesome Movie", updatedReview.getComment());
                });
    }

    @Test
    void deleteReviewTest() {
        // given
        var reviewId = "abc";

        // when
        webTestClient.delete()
                .uri(REVIEW_URL + "/{id}", reviewId)
                .exchange()
                // then
                .expectStatus().isNoContent();
    }
}
