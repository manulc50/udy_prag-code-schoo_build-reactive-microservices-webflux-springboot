package com.reactivespring.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.reactivespring.domain.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Levanta un servidor WireMock en el puerto 8084
@AutoConfigureWireMock(port = 8084)
// Sobrescribimos las url de los servicios MoviesInfoService y ReviewsService para que apunten al servidor WireMock.
@TestPropertySource(properties = {
        "restClient.moviesInfoUrl=http://localhost:8084/v1/movieinfos",
        "restClient.reviewsUrl=http://localhost:8084/v1/reviews"
})
class MoviesControllerIntgTest {
    static final String MOVIES_URL = "/v1/movies";

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        WireMock.reset();
    }

    @Test
    void retrieveMovieByIdTest() {
        // given
        var movieId = "abc";

        // Configuramos los stubs de Wiremocks
        // Si usamos el método "urlEqualTo", tenemos que indicar exactamente la url que debe coincidir.
        stubFor(get(urlEqualTo("/v1/movieinfos/" + movieId))
                .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                // Por defecto, automáticamente busca el archivo en el directorio "__files" dentro del directorio "resources"
                .withBodyFile("movieinfo.json"))
        );

        // Si usamos el método "urlPathEqualTo", tenemos que indicar una url que acepte Query Params
        // (Por ejemplo: "/v1/reviews?movie-info-id=2").
        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        // Por defecto, automáticamente busca el archivo en el directorio "__files" dentro del directorio "resources"
                        .withBodyFile("reviews.json"))
        );

        // when
        webTestClient.get()
                .uri(MOVIES_URL + "/{id}", movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    var retrievedMovie = movieEntityExchangeResult.getResponseBody();

                    assert retrievedMovie != null;
                    assert retrievedMovie.getReviewList() != null;
                    assert retrievedMovie.getMovieInfo() != null;
                    assert retrievedMovie.getReviewList().size() == 2;
                    assert retrievedMovie.getMovieInfo().getName().equals("Batman Begins");
                });
    }

    @Test
    void retrieveMovieByIdWithMovieInfoNotFoundTest() {
        // given
        var movieId = "abc";

        // Configuramos los stubs de Wiremocks
        // Si usamos el método "urlEqualTo", tenemos que indicar exactamente la url que debe coincidir.
        stubFor(get(urlEqualTo("/v1/movieinfos/" + movieId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value()))
        );

        // Si usamos el método "urlPathEqualTo", tenemos que indicar una url que acepte Query Params
        // (Por ejemplo: "/v1/reviews?movie-info-id=2").
        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        // Por defecto, automáticamente busca el archivo en el directorio "__files" dentro del directorio "resources"
                        .withBodyFile("reviews.json"))
        );

        // when
        webTestClient.get()
                .uri(MOVIES_URL + "/{id}", movieId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("There is no MovieInfo Available for the passed in id: " + movieId);

        // Verificamos que se haya invocado a este endpoint 1 vez ya que tenemos configurado que no se hagan reinteros
        // para respuestas con código de error 404.
        WireMock.verify(1, getRequestedFor(urlEqualTo("/v1/movieinfos/" + movieId)));
    }

    @Test
    void retrieveMovieByIdWithNoReviewsTest() {
        // given
        var movieId = "abc";

        // Configuramos los stubs de Wiremocks
        // Si usamos el método "urlEqualTo", tenemos que indicar exactamente la url que debe coincidir.
        stubFor(get(urlEqualTo("/v1/movieinfos/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        // Por defecto, automáticamente busca el archivo en el directorio "__files" dentro del directorio "resources"
                        .withBodyFile("movieinfo.json"))
        );

        // Si usamos el método "urlPathEqualTo", tenemos que indicar una url que acepte Query Params
        // (Por ejemplo: "/v1/reviews?movie-info-id=2").
        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]"))
        );

        // when
        webTestClient.get()
                .uri(MOVIES_URL + "/{id}", movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    var retrievedMovie = movieEntityExchangeResult.getResponseBody();

                    assert retrievedMovie != null;
                    assert retrievedMovie.getReviewList() != null;
                    assert retrievedMovie.getMovieInfo() != null;
                    assert retrievedMovie.getReviewList().isEmpty();
                    assert retrievedMovie.getMovieInfo().getName().equals("Batman Begins");
                });
    }

    @Test
    void retrieveMovieByIdWithError500InMovieInfoTest() {
        // given
        var movieId = "abc";

        // Configuramos los stubs de Wiremocks
        // Si usamos el método "urlEqualTo", tenemos que indicar exactamente la url que debe coincidir.
        stubFor(get(urlEqualTo("/v1/movieinfos/" + movieId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withBody("MoviesInfo Service Unavailable"))
        );

        // Si usamos el método "urlPathEqualTo", tenemos que indicar una url que acepte Query Params
        // (Por ejemplo: "/v1/reviews?movie-info-id=2").
        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        // Por defecto, automáticamente busca el archivo en el directorio "__files" dentro del directorio "resources"
                        .withBodyFile("reviews.json"))
        );

        // when
        webTestClient.get()
                .uri(MOVIES_URL + "/{id}", movieId)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Server Exception in MoviesInfoService: MoviesInfo Service Unavailable");

        // Verificamos que se haya invocado a este endpoint 4 veces ya que tenemos configurado 3 reintentos en el
        // cliente WebClient.
        WireMock.verify(4, getRequestedFor(urlEqualTo("/v1/movieinfos/" + movieId)));
    }

    @Test
    void retrieveMovieByIdWithError500InReviewTest() {
        // given
        var movieId = "abc";

        // Configuramos los stubs de Wiremocks
        // Si usamos el método "urlEqualTo", tenemos que indicar exactamente la url que debe coincidir.
        stubFor(get(urlEqualTo("/v1/movieinfos/" + movieId))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                // Por defecto, automáticamente busca el archivo en el directorio "__files" dentro del directorio "resources"
                                .withBodyFile("movieinfo.json")));


        // Configuramos los stubs de Wiremocks
        // Si usamos el método "urlPathEqualTo", tenemos que indicar una url que acepte Query Params
        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withBody("Review Service Not Available"))
        );

        // when
        webTestClient.get()
                .uri(MOVIES_URL + "/{id}", movieId)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Server Exception in ReviewService: Review Service Not Available");

        // Verificamos que se haya invocado a este endpoint 4 veces ya que tenemos configurado 3 reintentos en el
        // cliente WebClient.
        WireMock.verify(4, getRequestedFor(urlPathMatching("/v1/reviews")));
    }
}
