package com.reactivespring.client;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewsServerException;
import com.reactivespring.util.RetryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ReviewRestClient {
    private final WebClient webClient;
    private final String reviewsUrl;

    public ReviewRestClient(WebClient webClient, @Value("${restClient.reviewsUrl}") String reviewsUrl) {
        this.webClient = webClient;
        this.reviewsUrl = reviewsUrl;
    }

    public Flux<Review> retrieveReviews(String movieId) {
        var uri = UriComponentsBuilder.fromUriString(reviewsUrl)
                .queryParam("movie-info-id", movieId)
                .buildAndExpand().toUriString();

        return webClient.get()
                .uri(uri)
                .retrieve()
                // Manejador de errores para las respuestas con errores de tipo 5xx.
                // Versión simplificada de la expresión "httpStatus -> httpStatus.is5xxServerError()".
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    log.info("Status code is: {}", clientResponse.statusCode().value());

                    // En cualquier otro caso, devolvemos nuestra excepción personalizada "ReviewsServerException"
                    // con la respuesta y el código de estado recibidos del cliente.
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseBody -> Mono.error(new ReviewsServerException(
                                    "Server Exception in ReviewService: " + responseBody)));
                })
                .bodyToFlux(Review.class)
                // Podemos configurar o detallar más los reintentos usando este método "retryWhen".
                .retryWhen(RetryUtil.getRetrySpec());
    }
}
