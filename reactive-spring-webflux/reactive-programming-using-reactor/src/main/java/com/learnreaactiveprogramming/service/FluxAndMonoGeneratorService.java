package com.learnreaactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    public Flux<String> getFluxOfNames() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe")).log();
    }

    public Mono<String> getMonoOfName() {
        return Mono.just("alex").log();
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

        fluxAndMonoGeneratorService.getFluxOfNames()
                .subscribe(name -> System.out.println("Name is: " + name));

        fluxAndMonoGeneratorService.getMonoOfName()
                .subscribe(name -> System.out.println("Mono Name is: " + name));
    }

    public Flux<String> getFluxOfNamesWithMapAndFilter(int size) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                // Versión simplificada de la expresión "name -> name.toUpperCase()"
                .map(String::toUpperCase)
                .filter(name -> name.length() > size)
                .map(name -> name.length() + "-" + name)
                .log();
    }

    public Flux<String> getFluxOfNamesImmutability() {
        var fluxOfNames = Flux.fromIterable(List.of("alex", "ben", "chloe"));

        // Versión simplificada de la expresión "name -> name.toUpperCase()"
        fluxOfNames.map(String::toUpperCase);

        return fluxOfNames;
    }

    public Mono<String> getMonoOfNameWithMapAndFilter(int size) {
        return Mono.just("alex")
                // Versión simplificada de la expresión "name -> name.toUpperCase()"
                .map(String::toUpperCase)
                .filter(name -> name.length() > size)
                .log();
    }

    public Flux<String> getFluxOfNamesWithFlatMapAndFilter(int size) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                // Versión simplificada de la expresión "name -> name.toUpperCase()"
                .map(String::toUpperCase)
                .filter(name -> name.length() > size)
                // Versión simplificada de la expresión "name -> splitName(name)"
                .flatMap(this::splitName)
                .log();
    }

    // Nota: El operador "flatMap" trabaja de forma asíncrona y, por lo tanto, no garantiza el orden de los elementos.
    public Flux<String> getFluxOfNamesWithFlatMapAsyncAndFilter(int size) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                // Versión simplificada de la expresión "name -> name.toUpperCase()"
                .map(String::toUpperCase)
                .filter(name -> name.length() > size)
                // Versión simplificada de la expresión "name -> splitNameWitDelay(name)"
                .flatMap(this::splitNameWitDelay)
                .log();
    }

    // Nota: El operador "concatMap" trabaja de forma similar al operados "flatMap" pero sí garantiza el orden de
    // los elementos. Como tiene que garantizar el orden, tiene que ejecutar las tareas de forma síncrona(una después
    // de otra) y, por lo tanto, este operador "concatMap" tarda más tiempo en ejecutarse que el operador "flatMap".
    public Flux<String> getFluxOfNamesWithConcatMapAndFilter(int size) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                // Versión simplificada de la expresión "name -> name.toUpperCase()"
                .map(String::toUpperCase)
                .filter(name -> name.length() > size)
                // Versión simplificada de la expresión "name -> splitNameWitDelay(name)"
                .concatMap(this::splitNameWitDelay)
                .log();
    }

    // Nota: En general, se usa "concatMap" cuando el orden de los elementos importa. Si no importa, debe usarse
    // "flatMap" porque trabaja de forma asíncrona y tarda menos tiempo que "concatMap".

    public Mono<List<String>> getMonoOfNameWithFlatMapAndFilter(int size) {
        return Mono.just("alex")
                // Versión simplificada de la expresión "name -> name.toUpperCase()"
                .map(String::toUpperCase)
                .filter(name -> name.length() > size)
                // Versión simplificada de la expresión "name -> splitNameToMono(name)"
                .flatMap(this::splitNameToMono)
                .log();
    }

    public Flux<String> getFluxOfLettersWithFlatMapManyAndFilter(int size) {
        return Mono.just("alex")
                // Versión simplificada de la expresión "name -> name.toUpperCase()"
                .map(String::toUpperCase)
                .filter(name -> name.length() > size)
                // Versión simplificada de la expresión "name -> splitName(name)"
                .flatMapMany(this::splitName)
                .log();
    }

    // Nota: El operador "transform" nos permite definir una función que agrupe una serie de operaciones de flujos
    // reactivos con el objetivo de poder reutilizarlos en varios flujos.
    public Flux<String> getFluxOfLettersWithTransformAndDefaultIfEmpty(int size) {
        Function<Flux<String>, Flux<String>> mapAndFilter = fluxOfNames -> fluxOfNames
                // Versión simplificada de la expresión "name -> name.toUpperCase()"
                .map(String::toUpperCase)
                .filter(name -> name.length() > size);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(mapAndFilter)
                // Versión simplificada de la expresión "name -> splitName(name)"
                .flatMap(this::splitName)
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> getFluxOfLettersWithTransformAndSwitchIfEmpty(int size) {
        Function<Flux<String>, Flux<String>> mapAndFilter = fluxOfNames -> fluxOfNames
                // Versión simplificada de la expresión "name -> name.toUpperCase()"
                .map(String::toUpperCase)
                .filter(name -> name.length() > size)
                // Versión simplificada de la expresión "name -> splitName(name)"
                .flatMap(this::splitName);

        Flux<String> fluxOfDefault = Flux.just("default")
                .transform(mapAndFilter);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(mapAndFilter)
                .switchIfEmpty(fluxOfDefault)
                .log();
    }

    public Mono<String> getMonoOfDefaultWithMapFilterAndDefaultIfEmpty(int size) {
        return Mono.just("alex")
                // Versión simplificada de la expresión "name -> name.toUpperCase()"
                .map(String::toUpperCase)
                .filter(name -> name.length() > size)
                .defaultIfEmpty("default")
                .log();
    }

    public Mono<String> getMonoOfDefaultWithMapFilterAndSwitchIfEmpty(int size) {
        return Mono.just("alex")
                // Versión simplificada de la expresión "name -> name.toUpperCase()"
                .map(String::toUpperCase)
                .filter(name -> name.length() > size)
                .switchIfEmpty(Mono.just("default"))
                .log();
    }

    // Nota: En los métodos "concat" y "concatWith", primero se subscribe al primer flujo reactivo y, cuando termine de
    // emitirse el último elemento de ese flujo reactivo, se realiza la subscripción al segundo flujo reactivo. Es decir,
    // las subscripciones se hacen en orden y de foma secuencial.

    public Flux<String> getFluxOfStringWithConcat() {
        var fluxOfABC = Flux.just("A", "B", "C");
        var fluxOfDEF = Flux.just("D", "E", "F");

        return Flux.concat(fluxOfABC, fluxOfDEF).log();
    }

    public Flux<String> getFluxOfStringWithConcatWith() {
        var fluxOfABC = Flux.just("A", "B", "C");
        var fluxOfDEF = Flux.just("D", "E", "F");

        return fluxOfABC.concatWith(fluxOfDEF).log();
    }

    public Flux<String> getFluxOfStringWithMonosAndConcatWith() {
        var monoOfA = Mono.just("A");
        var monoOfB = Mono.just("B");

        return monoOfA.concatWith(monoOfB).log();
    }

    // Nota: A diferencia de los métodos "concat" y "concatWith", en los métodos "merge" y "mergeWith" las
    // subscripciones a los flujos reactivos se realizan al mismo tiempo.

    public Flux<String> getFluxOfStringWithMerge() {
        var fluxOfABC = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var fluxOfDEF = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return Flux.merge(fluxOfABC, fluxOfDEF).log();
    }

    public Flux<String> getFluxOfStringWithMergeWith() {
        var fluxOfABC = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var fluxOfDEF = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return fluxOfABC.mergeWith(fluxOfDEF).log();
    }

    public Flux<String> getFluxOfStringWithMonosAndMergeWith() {
        var monoOfA = Mono.just("A");
        var monoOfB = Mono.just("B");

        return monoOfA.mergeWith(monoOfB).log();
    }

    // En este método "mergeSequential" las subscripciones a los flujos reactivos se realizan al mismo tiempo pero
    // los resultados de unen de forma secuencial.

    public Flux<String> getFluxOfStringWithMergeSequential() {
        var fluxOfABC = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var fluxOfDEF = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return Flux.mergeSequential(fluxOfABC, fluxOfDEF).log();
    }

    // En los métodos "zip" y "zipWith", las subscripciones a los flujos reativos se realizan al mismo tiempo.

    public Flux<String> getFluxOfStringWithZip1() {
        var fluxOfABC = Flux.just("A", "B", "C");
        var fluxOfDEF = Flux.just("D", "E", "F");

        return Flux.zip(fluxOfABC, fluxOfDEF, (first, second) -> first + second).log();
    }

    public Flux<String> getFluxOfStringWithZip2() {
        var fluxOfABC = Flux.just("A", "B", "C");
        var fluxOfDEF = Flux.just("D", "E", "F");
        var fluxOf123 = Flux.just("1", "2", "3");
        var fluxOf456 = Flux.just("4", "5", "6");

        return Flux.zip(fluxOfABC, fluxOfDEF, fluxOf123, fluxOf456)
                .map(tuple4 -> tuple4.getT1() + tuple4.getT2() + tuple4.getT3() + tuple4.getT4())
                .log();
    }

    public Flux<String> getFluxOfStringWithZipWith() {
        var fluxOfABC = Flux.just("A", "B", "C");
        var fluxOfDEF = Flux.just("D", "E", "F");

        return fluxOfABC.zipWith(fluxOfDEF, (first, second) -> first + second).log();
    }

    public Mono<String> getMonoOfStringWithZipWith() {
        var monoOfA = Mono.just("A");
        var monoOfB = Mono.just("B");

        return monoOfA.zipWith(monoOfB, (first, second) -> first + second).log();
    }

    private Flux<String> splitName(String name) {
        var letters = name.split("");

        return Flux.fromArray(letters);
    }

    private Flux<String> splitNameWitDelay(String name) {
        var letters = name.split("");
        //var delay = random.nextInt(1000);
        var delay = 1000;

        return Flux.fromArray(letters)
                .delayElements(Duration.ofMillis(delay));
    }

    private Mono<List<String>> splitNameToMono(String name) {
        var charList = List.of(name.split(""));

        return Mono.just(charList);
    }
}
