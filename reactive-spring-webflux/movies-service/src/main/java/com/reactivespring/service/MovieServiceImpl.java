package com.reactivespring.service;

import com.reactivespring.client.MoviesInfoRestClient;
import com.reactivespring.client.ReviewRestClient;
import com.reactivespring.domain.Movie;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class MovieServiceImpl implements MovieService {
    private final MoviesInfoRestClient moviesInfoRestClient;
    private final ReviewRestClient reviewRestClient;

    // Una manera
    /*@Override
    public Mono<Movie> retrieveMovieById(String id) {
        return moviesInfoRestClient.retrieveMovieInfo(id)
                .flatMap(movieInfo -> {
                    var monoOfListReviews = reviewRestClient.retrieveReviews(id).collectList();

                    return monoOfListReviews.map(listOfReviews -> new Movie(movieInfo, listOfReviews));
                });
    }*/

    // Otra manera
    @Override
    public Mono<Movie> retrieveMovieById(String id) {
        var monoOfMovieInfo = moviesInfoRestClient.retrieveMovieInfo(id);
        var monoOfListReviews = reviewRestClient.retrieveReviews(id).collectList().log();

        // Versión simplificada de la expresión "(movieInfo, listOfReviews) -> new Movie(movieInfo, listOfReviews)"
        return monoOfMovieInfo.zipWith(monoOfListReviews, Movie::new);
    }
}
