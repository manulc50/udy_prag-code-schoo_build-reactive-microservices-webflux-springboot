package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

// Nota: Esta anotación también incluye la anotación "@AutoConfigureWebTestClient" que crea y configura un bean de
// Spring de tipo WebTestClient.
@WebFluxTest(controllers = FluxAndMonoController.class)
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void getFluxOfIntegersTest1() {
        // when
        webTestClient.get()
                .uri("/flux")
                .exchange()
                // then
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Integer.class)
                .hasSize(3);
    }

    @Test
    void getFluxOfIntegersTest2() {
        // when
        var fluxOfIntegers = webTestClient.get()
                .uri("/flux")
                .exchange()
                // then
                .expectStatus().is2xxSuccessful()
                .returnResult(Integer.class)
                .getResponseBody();

        // then
        StepVerifier.create(fluxOfIntegers)
                .expectNext(1, 2, 3)
                .verifyComplete();
    }

    @Test
    void getFluxOfIntegersTest3() {
        // when
        webTestClient.get()
                .uri("/flux")
                .exchange()
                // then
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Integer.class)
                .consumeWith(listEntityExchangeResult -> {
                    var responseBody = listEntityExchangeResult.getResponseBody();

                    assertNotNull(responseBody);
                    assertEquals(3, responseBody.size());
                });
    }

    @Test
    void getMonoOfStringTest() {
        // when
        webTestClient.get()
                .uri("/mono")
                .exchange()
                // then
                .expectStatus().is2xxSuccessful()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    var responseBody = stringEntityExchangeResult.getResponseBody();

                    assertEquals("hello-world", responseBody);
                });
    }

    @Test
    void getStreamOfLongsTest() {
        // when
        var fluxOfLongs = webTestClient.get()
                .uri("/stream")
                .exchange()
                // then
                .expectStatus().is2xxSuccessful()
                .returnResult(Long.class)
                .getResponseBody();

        // then
        StepVerifier.create(fluxOfLongs)
                .expectNext(0L, 1L, 2L)
                // Tenemos que cancelar la subscripción en algún momento porque este endpoint emite en forma de
                // streaming números sin parar.
                .thenCancel()
                .verify();
    }
}
