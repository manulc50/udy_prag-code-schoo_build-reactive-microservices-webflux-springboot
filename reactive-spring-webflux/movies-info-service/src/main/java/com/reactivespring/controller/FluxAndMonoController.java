package com.reactivespring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")
    public Flux<Integer> getFluxOfIntegers() {
        return Flux.just(1, 2, 3).log();
    }

    @GetMapping("/mono")
    public Mono<String> getMonoOfString() {
        return Mono.just("hello-world").log();
    }

    // Este endpoint emite en streaming, es decir, cuando el flujo reactivo emite un elemento, directamente se envía
    // al cliente que está suscrito sin esperar a que se emitan todos los elementos del flujo para enviarselos al
    // cliente(este último caso ocurre en el endpoint cuyo método handler es "getFluxOfIntegers").
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Long> getStreamOfLongs() {
        // En este caso, el método "interval" emite, de forma continuada y sin parar, números enteros cada segundo
        // empezando en 0.
        return Flux.interval(Duration.ofSeconds(1)).log();
    }
}
