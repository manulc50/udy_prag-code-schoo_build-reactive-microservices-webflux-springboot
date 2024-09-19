package com.reactivespring.service;

import com.reactivespring.domain.Movie;
import reactor.core.publisher.Mono;

public interface MovieService {
    Mono<Movie> retrieveMovieById(String id);
}
