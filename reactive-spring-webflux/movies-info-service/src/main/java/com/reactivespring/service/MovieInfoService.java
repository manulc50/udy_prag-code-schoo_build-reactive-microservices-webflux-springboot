package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieInfoService {
    Flux<MovieInfo> getAllMovieInfos();
    Flux<MovieInfo> getAllMovieInfosByYear(Integer year);
    Mono<MovieInfo> getMovieInfoById(String id);
    Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo);
    Mono<MovieInfo> updateMovieInfo(String id, MovieInfo movieInfo);
    Mono<Void> deleteMovieInfoById(String id);
}
