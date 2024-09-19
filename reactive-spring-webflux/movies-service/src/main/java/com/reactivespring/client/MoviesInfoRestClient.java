package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.util.RetryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class MoviesInfoRestClient {
    private final WebClient webClient;
    private final String moviesInfoUrl;

    public MoviesInfoRestClient(WebClient webClient, @Value("${restClient.moviesInfoUrl}") String moviesInfoUrl) {
        this.webClient = webClient;
        this.moviesInfoUrl = moviesInfoUrl;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {
        return webClient.get()
                .uri(moviesInfoUrl.concat("/{id}"), movieId)
                .retrieve()
                // Manejador de errores para las respuestas con errores de tipo 4xx.
                // Versión simplificada de la expresión "httpStatus -> httpStatus.is4xxClientError()".
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    log.info("Status code is: {}", clientResponse.statusCode().value());

                    // Caso para el error de tipo 404.
                    if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new MoviesInfoClientException(
                                "There is no MovieInfo Available for the passed in id: " + movieId,
                                clientResponse.statusCode().value()));
                    }

                    // En cualquier otro caso, devolvemos nuestra excepción personalizada "MoviesInfoClientException"
                    // con la respuesta y el código de estado recibidos del cliente.
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseBody -> Mono.error(new MoviesInfoClientException(responseBody,
                                    clientResponse.statusCode().value())));
                })
                // Manejador de errores para las respuestas con errores de tipo 5xx.
                // Versión simplificada de la expresión "httpStatus -> httpStatus.is5xxServerError()".
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    log.info("Status code is: {}", clientResponse.statusCode().value());

                    // En cualquier otro caso, devolvemos nuestra excepción personalizada "MoviesInfoServerException"
                    // con la respuesta y el código de estado recibidos del cliente.
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseBody -> Mono.error(new MoviesInfoServerException(
                                    "Server Exception in MoviesInfoService: " + responseBody)));
                })
                .bodyToMono(MovieInfo.class)
                // Realiza un reintento en caso de fallo o excepción(el reintento ocurre inmediatamente después del fallo)
                //.retry()
                // Realiza n reintentos en caso de fallo o excepción(los reintentos ocurren inmediatamente después del fallo)
                //.retry(3)
                // Podemos configurar o detallar más los reintentos usando este método "retryWhen".
                .retryWhen(RetryUtil.getRetrySpec())
                .log();
    }
}
