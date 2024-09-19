package com.learnreaactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

class FluxAndMonoGeneratorServiceTest {
    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void getFluxOfNamesTest() {
        // when
        var fluxOfNames = fluxAndMonoGeneratorService.getFluxOfNames();

        // then
        StepVerifier.create(fluxOfNames)
                //.expectNext("alex", "ben", "chloe")
                //.expectNextCount(3)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getMonoOfNameTest() {
        // when
        var monoOfName = fluxAndMonoGeneratorService.getMonoOfName();

        // then
        StepVerifier.create(monoOfName)
                .expectNext("alex")
                // Los 2 últimos métodos son equivalentes a usar el método "verifyComplete"
                .expectComplete()
                .verify();
    }

    @Test
    void getFluxOfNamesWithMapAndFilterTest() {
        // given
        int nameSize = 3;

        // when
        var fluxOfNames = fluxAndMonoGeneratorService.getFluxOfNamesWithMapAndFilter(nameSize);

        // then
        StepVerifier.create(fluxOfNames)
                .expectNext("4-ALEX", "5-CHLOE")
                .expectComplete()
                .verify();
    }

    @Test
    void getFluxOfNamesImmutabilityTest() {
        // when
        var fluxOfNames = fluxAndMonoGeneratorService.getFluxOfNamesImmutability();

        // then
        StepVerifier.create(fluxOfNames)
                .expectNext("alex", "ben", "chloe")
                .expectComplete()
                .verify();
    }

    @Test
    void getMonoOfNameWithMapAndFilterTest() {
        // given
        int nameSize = 3;

        // when
        var monoOfName = fluxAndMonoGeneratorService.getMonoOfNameWithMapAndFilter(nameSize);

        // then
        StepVerifier.create(monoOfName)
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void getFluxOfNamesWithFlatMapAndFilterTest() {
        // given
        int nameSize = 3;

        // when
        var fluxOfLetters = fluxAndMonoGeneratorService.getFluxOfNamesWithFlatMapAndFilter(nameSize);

        // then
        StepVerifier.create(fluxOfLetters)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void getFluxOfNamesWithFlatMapAsyncAndFilterTest() {
        // given
        int nameSize = 3;

        // when
        var fluxOfLetters = fluxAndMonoGeneratorService.getFluxOfNamesWithFlatMapAsyncAndFilter(nameSize);

        // then
        StepVerifier.create(fluxOfLetters)
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void getFluxOfNamesWithConcatMapAndFilterTest() {
        // given
        int nameSize = 3;

        // when
        var fluxOfLetters = fluxAndMonoGeneratorService.getFluxOfNamesWithConcatMapAndFilter(nameSize);

        // then
        StepVerifier.create(fluxOfLetters)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void getMonoOfNameWithFlatMapAndFilterTest() {
        // given
        int nameSize = 3;

        // when
        var monoOfListOfLetters = fluxAndMonoGeneratorService.getMonoOfNameWithFlatMapAndFilter(nameSize);

        // then
        StepVerifier.create(monoOfListOfLetters)
                .expectNext(List.of("A", "L", "E", "X"))
                .expectComplete()
                .verify();
    }

    @Test
    void getFluxOfLettersWithFlatMapManyAndFilterTest() {
        // given
        int nameSize = 3;

        // when
        var fluxOfLetters = fluxAndMonoGeneratorService.getFluxOfLettersWithFlatMapManyAndFilter(nameSize);

        // then
        StepVerifier.create(fluxOfLetters)
                .expectNext("A", "L", "E", "X")
                .expectComplete()
                .verify();
    }

    @Test
    void getFluxOfLettersWithTransformAndDefaultIfEmpty() {
        // given
        int nameSize = 3;

        // when
        var fluxOfLetters = fluxAndMonoGeneratorService.getFluxOfLettersWithTransformAndDefaultIfEmpty(nameSize);

        // then
        StepVerifier.create(fluxOfLetters)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void getFluxOfDefaultWithTransformAndDefaultIfEmptyTest() {
        // given
        int nameSize = 6;

        // when
        var fluxOfDefault = fluxAndMonoGeneratorService.getFluxOfLettersWithTransformAndDefaultIfEmpty(nameSize);

        // then
        StepVerifier.create(fluxOfDefault)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void getFluxOfDefaultWithTransformAndSwitchIfEmptyTest() {
        // given
        int nameSize = 6;

        // when
        var fluxOfLetters = fluxAndMonoGeneratorService.getFluxOfLettersWithTransformAndSwitchIfEmpty(nameSize);

        // then
        StepVerifier.create(fluxOfLetters)
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .verifyComplete();
    }

    @Test
    void getMonoOfDefaultWithMapFilterAndDefaultIfEmptyTest() {
        // given
        int nameSize = 4;

        // when
        var monoOfDefault = fluxAndMonoGeneratorService.getMonoOfDefaultWithMapFilterAndDefaultIfEmpty(nameSize);

        // then
        StepVerifier.create(monoOfDefault)
                .expectNext("default")
                .expectComplete()
                .verify();
    }

    @Test
    void getMonoOfDefaultWithMapFilterAndSwitchIfEmptyTest() {
        // given
        int nameSize = 4;

        // when
        var monoOfDefault = fluxAndMonoGeneratorService.getMonoOfDefaultWithMapFilterAndSwitchIfEmpty(nameSize);

        // then
        StepVerifier.create(monoOfDefault)
                .expectNext("default")
                .expectComplete()
                .verify();
    }

    @Test
    void getFluxOfStringWithConcatTest() {
        // when
        var concatFlux = fluxAndMonoGeneratorService.getFluxOfStringWithConcat();

        // then
        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .expectComplete()
                .verify();
    }

    @Test
    void getFluxOfStringWithConcatWithTest() {
        // when
        var concatFlux = fluxAndMonoGeneratorService.getFluxOfStringWithConcatWith();

        // then
        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void getFluxOfStringWithMonosAndConcatWithTest() {
        // when
        var concatFlux = fluxAndMonoGeneratorService.getFluxOfStringWithMonosAndConcatWith();

        // then
        StepVerifier.create(concatFlux)
                .expectNext("A", "B")
                .expectComplete()
                .verify();
    }

    @Test
    void getFluxOfStringWithMergeTest() {
        // when
        var mergeFlux = fluxAndMonoGeneratorService.getFluxOfStringWithMerge();

        // then
        StepVerifier.create(mergeFlux)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void getFluxOfStringWithMergeWithTest() {
        // when
        var mergeFlux = fluxAndMonoGeneratorService.getFluxOfStringWithMergeWith();

        // then
        StepVerifier.create(mergeFlux)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void getFluxOfStringWithMonosAndMergeWithTest() {
        // when
        var mergeFlux = fluxAndMonoGeneratorService.getFluxOfStringWithMonosAndMergeWith();

        // then
        StepVerifier.create(mergeFlux)
                .expectNext("A","B")
                .verifyComplete();
    }

    @Test
    void getFluxOfStringWithMergeSequentialTest() {
        // when
        var mergeFlux = fluxAndMonoGeneratorService.getFluxOfStringWithMergeSequential();

        // then
        StepVerifier.create(mergeFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .expectComplete()
                .verify();
    }

    @Test
    void getFluxOfStringWithZip1Test() {
        // when
        var zipFlux = fluxAndMonoGeneratorService.getFluxOfStringWithZip1();

        // then
        StepVerifier.create(zipFlux)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void getFluxOfStringWithZip2Test() {
        // when
        var zipFlux = fluxAndMonoGeneratorService.getFluxOfStringWithZip2();

        // then
        StepVerifier.create(zipFlux)
                .expectNext("AD14", "BE25", "CF36")
                .verifyComplete();
    }

    @Test
    void getFluxOfStringWithZipWithTest() {
        // when
        var zipFlux = fluxAndMonoGeneratorService.getFluxOfStringWithZipWith();

        // then
        StepVerifier.create(zipFlux)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void getMonoOfStringWithZipWithTest() {
        // when
        var zipMono = fluxAndMonoGeneratorService.getMonoOfStringWithZipWith();

        // then
        StepVerifier.create(zipMono)
                .expectNext("AB")
                .expectComplete()
                .verify();
    }
}