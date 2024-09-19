package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class MovieInfoServiceImpl implements MovieInfoService {
    private final MovieInfoRepository movieInfoRepository;

    @Override
    public Flux<MovieInfo> getAllMovieInfos() {
        return movieInfoRepository.findAll();
    }

    @Override
    public Flux<MovieInfo> getAllMovieInfosByYear(Integer year) {
        return movieInfoRepository.findByYear(year);
    }

    @Override
    public Mono<MovieInfo> getMovieInfoById(String id) {
        return movieInfoRepository.findById(id);
    }

    @Override
    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    @Override
    public Mono<MovieInfo> updateMovieInfo(String id, MovieInfo movieInfo) {
        return getMovieInfoById(id)
                .flatMap(existingMovieInfo -> {
                    existingMovieInfo.setCast(movieInfo.getCast());
                    existingMovieInfo.setYear(movieInfo.getYear());
                    existingMovieInfo.setName(movieInfo.getName());
                    existingMovieInfo.setReleaseDate(movieInfo.getReleaseDate());

                    return addMovieInfo(existingMovieInfo);
                });
    }

    @Override
    public Mono<Void> deleteMovieInfoById(String id) {
        return movieInfoRepository.deleteById(id);
    }
}
