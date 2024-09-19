package com.reactivespring;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

class SinksTest {

    @Test
    void replaySinkTest() {
        // Crea un Sink que emite múltiples eventos o señales.
        // El método "replay" es para que se repliquen todos los eventos o señales cada de haya un suscriptor.
        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> fluxOfIntegers1 = replaySink.asFlux();
        fluxOfIntegers1.subscribe(i -> System.out.println("Subscriber 1: " + i));

        Flux<Integer> fluxOfIntegers2 = replaySink.asFlux();
        fluxOfIntegers2.subscribe(i -> System.out.println("Subscriber 2: " + i));

        // Este método "tryEmitNext" es equivalente al método "emitNext" pero no tenemos que indicar ningún manejador
        // de errores.
        replaySink.tryEmitNext(3);

        Flux<Integer> fluxOfIntegers3 = replaySink.asFlux();
        fluxOfIntegers3.subscribe(i -> System.out.println("Subscriber 3: " + i));
    }
}
