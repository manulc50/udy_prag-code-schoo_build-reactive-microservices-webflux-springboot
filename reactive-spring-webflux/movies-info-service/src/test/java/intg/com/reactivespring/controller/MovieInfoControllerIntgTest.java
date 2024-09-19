package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MovieInfoControllerIntgTest {
    final static String MOVIE_INFO_URL = "v1/movieinfos";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        var movieInfos = List.of(new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
                        LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"),
                        LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
                        LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieInfos).blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void getAllMovieInfosTest() {
        // when
        webTestClient.get()
                .uri(MOVIE_INFO_URL)
                .exchange()
                // then
                .expectStatus().is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getAllMovieInfosByYearTest() {
        // given
        var uri = UriComponentsBuilder.fromUriString(MOVIE_INFO_URL)
                .queryParam("year", 2005)
                .buildAndExpand().toUri();

        // when
        webTestClient.get()
                .uri(uri.toString())
                .exchange()
                // then
                .expectStatus().is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void getMovieInfoByIdTest() {
        // given
        var movieInfoId = "abc";

        // when
        webTestClient.get()
                .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                // then
                .expectStatus().is2xxSuccessful()
                // Una manera
                /*.expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var retrieveMovieInfo = movieInfoEntityExchangeResult.getResponseBody();

                    assert retrieveMovieInfo != null;
                    assert retrieveMovieInfo.getId().equals(movieInfoId);
                });*/
                // Otra manera
                .expectBody()
                .jsonPath("$.name").isEqualTo("Dark Knight Rises");
    }

    @Test
    void getMovieInfoByIdNotFoundTest() {
        // given
        var movieInfoId = "def";

        // when
        webTestClient.get()
                .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                // then
                .expectStatus().isNotFound();
    }

    @Test
    void addMovieInfoTest() {
        // given
        var movieInfo = new MovieInfo(null, "Batman Begins1", 2005, List.of("Christian Bale", "Michael Cane"),
                LocalDate.parse("2005-06-15"));

        // when
        webTestClient.post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();

                    assertNotNull(savedMovieInfo);
                    assertNotNull(savedMovieInfo.getMovieInfoId());
                });
    }

    @Test
    void updateMovieInfoTest() {
        // given
        var movieInfoId = "abc";

        var movieInfo = new MovieInfo(null, "Dark Knight Rises1", 2005, List.of("Christian Bale", "Michael Cane"),
                LocalDate.parse("2005-06-15"));

        // when
        webTestClient.put()
                .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
                .bodyValue(movieInfo)
                .exchange()
                // then
                .expectStatus().is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();

                    assertNotNull(updatedMovieInfo);
                    assertEquals(movieInfoId, updatedMovieInfo.getMovieInfoId());
                    assertEquals("Dark Knight Rises1", updatedMovieInfo.getName());
                });
    }

    @Test
    void updateMovieInfoNotFoundTest() {
        // given
        var movieInfoId = "def";

        var movieInfo = new MovieInfo(null, "Dark Knight Rises1", 2005, List.of("Christian Bale", "Michael Cane"),
                LocalDate.parse("2005-06-15"));

        // when
        webTestClient.put()
                .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
                .bodyValue(movieInfo)
                .exchange()
                // then
                .expectStatus().isNotFound();
    }

    @Test
    void deleteMovieInfoByIdTest() {
        // given
        var movieInfoId = "abc";

        // when
        webTestClient.delete()
                .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                // then
                .expectStatus().isNoContent();
    }
}
