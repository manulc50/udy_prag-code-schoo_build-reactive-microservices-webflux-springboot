package com.reactivespring.controller;

import com.reactivespring.domain.Movie;
import com.reactivespring.service.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/movies")
public class MovieController {
    private final MovieService movieService;

    @GetMapping("/{id}")
    public Mono<Movie> retrieveMovieById(@PathVariable(name = "id") String movieId) {
        return movieService.retrieveMovieById(movieId);
    }
}
