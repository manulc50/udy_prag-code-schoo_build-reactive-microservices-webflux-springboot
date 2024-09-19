package com.reactivespring.util;

import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.exception.ReviewsServerException;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

import java.time.Duration;

// Clase de utilidad para tener una configuración de los reintentos común a todos los clientes("MoviesInfoRestClient" y
// "ReviewRestClien").

public class RetryUtil {

    public static Retry getRetrySpec() {
        // En este caso, cada reintento ocurre cada segundo y se configura un máximo de 3 reintentos.
        // Con el método "filter", indicamos que solo se hagan los reintentos para excepciones de
        // tipo "MoviesInfoServerException" o de tipo "ReviewsServerException".
        // Usamos el método "onRetryExhaustedThrow" para propagar la excepción original que causó el reintento al
        // cliente que realizó la llamada. De lo contrario, si no usamos este método, la excepción original es
        // sobrescrita por otra dentro del método "retryWhen".
        return Retry.fixedDelay(3, Duration.ofSeconds(1))
                .filter(ex -> ex instanceof MoviesInfoServerException || ex instanceof ReviewsServerException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure())));
    }
}
