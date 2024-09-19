package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataMongoTest
class MovieInfoRepositoryIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        var movieInfos = List.of(new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
                        LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"),
                        LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
                        LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieInfos).blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void testFindAll() {
        // when
        var fluxOfMovieInfo = movieInfoRepository.findAll().log();

        // then
        StepVerifier.create(fluxOfMovieInfo)
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }

    @Test
    void testFindById() {
        // when
        var monoOfMovieInfo = movieInfoRepository.findById("abc").log();

        // then
        StepVerifier.create(monoOfMovieInfo)
                //.expectNextCount(1)
                .assertNext(movieInfo -> {
                    assertEquals("Dark Knight Rises", movieInfo.getName());
                    assertEquals(2012, movieInfo.getYear());
                })
                .verifyComplete();
    }

    @Test
    void testFindByYear() {
        // when
        var fluxOfMovieInfo = movieInfoRepository.findByYear(2005).log();

        // then
        StepVerifier.create(fluxOfMovieInfo)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testFindByName() {
        // when
        var monoOfMovieInfo = movieInfoRepository.findByName("Batman Begins").log();

        // then
        StepVerifier.create(monoOfMovieInfo)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testSave() {
        // given
        var movieInfo = new MovieInfo(null, "Batman Begins 1", 2005, List.of("Christian Bale", "Michael Cane"),
                LocalDate.parse("2005-06-15"));

        // when
        var monoOfMovieInfo = movieInfoRepository.save(movieInfo).log();

        // then
        StepVerifier.create(monoOfMovieInfo)
                //.expectNextCount(1)
                .assertNext(savedMovieInfo -> {
                    assertNotNull(savedMovieInfo.getMovieInfoId());
                    assertEquals("Batman Begins 1", savedMovieInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void testUpdate() {
        // given
        var movieInfo = movieInfoRepository.findById("abc").block();
        movieInfo.setYear(2021);

        // when
        var monoOfMovieInfo = movieInfoRepository.save(movieInfo).log();

        // then
        StepVerifier.create(monoOfMovieInfo)
                //.expectNextCount(1)
                .assertNext(updatedMovieInfo -> {
                    assertEquals("abc", updatedMovieInfo.getMovieInfoId());
                    assertEquals(2021, updatedMovieInfo.getYear());
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testDelete() {
        // when
        movieInfoRepository.deleteById("abc").block();
        var fluxOfMovieInfo = movieInfoRepository.findAll().log();

        // then
        StepVerifier.create(fluxOfMovieInfo)
                .expectNextCount(2)
                .verifyComplete();
    }

}
