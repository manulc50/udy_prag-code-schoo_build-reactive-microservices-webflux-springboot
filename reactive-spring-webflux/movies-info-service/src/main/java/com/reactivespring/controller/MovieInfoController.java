package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("v1/movieinfos")
public class MovieInfoController {
    private final MovieInfoService movieInfoService;

    @GetMapping
    public Flux<MovieInfo> getAllMovieInfos(@RequestParam(required = false) Integer year) {
        log.info("Year is: {}", year);

        if(year != null)
            return movieInfoService.getAllMovieInfosByYear(year).log();

        return movieInfoService.getAllMovieInfos().log();
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable String id) {
        return movieInfoService.getMovieInfoById(id)
                // Versión simplificada de la expresión "movieInfo -> ResponseEntity.ok(movieInfo)"
                .map(ResponseEntity::ok)
                // Una manera
                //.defaultIfEmpty(ResponseEntity.notFound().build())
                // Otra manera
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Validated MovieInfo movieInfo) {
        return movieInfoService.addMovieInfo(movieInfo).log();
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@PathVariable String id, @RequestBody MovieInfo movieInfo) {
        return movieInfoService.updateMovieInfo(id, movieInfo)
                // Versión simplificada de la expresión "updateMovieInfo -> ResponseEntity.ok(updateMovieInfo)"
                .map(ResponseEntity::ok)
                // Una manera
                //.defaultIfEmpty(ResponseEntity.notFound().build())
                // Otra manera
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public Mono<Void> deleteMovieInfoById(@PathVariable String id) {
        return movieInfoService.deleteMovieInfoById(id);
    }
}
