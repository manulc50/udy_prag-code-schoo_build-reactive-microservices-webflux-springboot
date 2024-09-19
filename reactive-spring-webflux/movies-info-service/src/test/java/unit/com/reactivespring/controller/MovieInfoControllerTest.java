package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = MovieInfoController.class)
class MovieInfoControllerTest {
    final static String MOVIE_INFO_URL = "/v1/movieinfos";

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    MovieInfoService movieInfoService;

    @Test
    void getAllMovieInfosTest() {
        // given
        var movieInfos = List.of(new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
                        LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"),
                        LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
                        LocalDate.parse("2012-07-20")));

        when(movieInfoService.getAllMovieInfos()).thenReturn(Flux.fromIterable(movieInfos));

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
    void getMovieInfoByIdTest() {
        // given
        var movieInfoId = "abc";
        var movieInfo = new MovieInfo(movieInfoId, "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
                LocalDate.parse("2012-07-20"));

        when(movieInfoService.getMovieInfoById(anyString())).thenReturn(Mono.just(movieInfo));

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
    void addMovieInfoTest() {
        // given
        var movieInfo = new MovieInfo(null, "Batman Begins1", 2005, List.of("Christian Bale", "Michael Cane"),
                LocalDate.parse("2005-06-15"));

        var savedMovieInfo = new MovieInfo("mockId", "Batman Begins1", 2005, List.of("Christian Bale", "Michael Cane"),
                LocalDate.parse("2005-06-15"));

        when(movieInfoService.addMovieInfo(any(MovieInfo.class))).thenReturn(Mono.just(savedMovieInfo));

        // when
        webTestClient.post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var responseBody = movieInfoEntityExchangeResult.getResponseBody();

                    assertNotNull(responseBody);
                    assertNotNull(responseBody.getMovieInfoId());
                    assertEquals("mockId", responseBody.getMovieInfoId());
                });
    }

    @Test
    void addMovieInfoWithValidationTest() {
        // given
        var movieInfo = new MovieInfo(null, "", -2005, List.of("", "Michael Cane"),
                LocalDate.parse("2005-06-15"));

        // when
        webTestClient.post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    var responseBody = stringEntityExchangeResult.getResponseBody();

                    System.out.println("Response Body: " + responseBody);

                    assert responseBody != null;
                    assert responseBody.equals("movieInfo.cast must be present,movieInfo.name must be present,"
                            + "movieInfo.year must be a positive value");
                });
    }

    @Test
    void updateMovieInfoTest() {
        // given
        var movieInfoId = "abc";

        var movieInfo = new MovieInfo(null, "Dark Knight Rises1", 2005, List.of("Christian Bale", "Michael Cane"),
                LocalDate.parse("2005-06-15"));

        var updatedMovieInfo = new MovieInfo(movieInfoId, "Dark Knight Rises1", 2005, List.of("Christian Bale", "Michael Cane"),
                LocalDate.parse("2005-06-15"));

        when(movieInfoService.updateMovieInfo(anyString(), any(MovieInfo.class))).thenReturn(Mono.just(updatedMovieInfo));

        // when
        webTestClient.put()
                .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
                .bodyValue(movieInfo)
                .exchange()
                // then
                .expectStatus().is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var responseBody = movieInfoEntityExchangeResult.getResponseBody();

                    assertNotNull(responseBody);
                    assertEquals(movieInfoId, responseBody.getMovieInfoId());
                    assertEquals("Dark Knight Rises1", responseBody.getName());
                });
    }

    @Test
    void deleteMovieInfoByIdTest() {
        // given
        var movieInfoId = "abc";

        when(movieInfoService.deleteMovieInfoById(anyString())).thenReturn(Mono.empty());

        // when
        webTestClient.delete()
                .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                // then
                .expectStatus().isNoContent();
    }
}
