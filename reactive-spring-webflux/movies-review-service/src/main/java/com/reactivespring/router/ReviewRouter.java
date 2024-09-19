package com.reactivespring.router;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;

@Configuration
public class ReviewRouter {
    private static final String REVIEW_BASE_URL = "v1/reviews";

    @Bean
    public RouterFunction<ServerResponse> reviewsRouter(ReviewHandler reviewHandler) {
        return route()
                .GET("/v1/helloworld", request -> ServerResponse.ok().bodyValue("Hello World!"))
                // Versión simplificada de la expresión "request -> reviewHandler.addReview(request)"
                //.POST(REVIEW_BASE_URL, reviewHandler::addReview)
                // Versión simplificada de la expresión "request -> reviewHandler.getReviews(request)"
                //.GET(REVIEW_BASE_URL, reviewHandler::getReviews)
                // Este método "nest" nos permite agrupar endpoints que comparten una misma url
                .nest(path(REVIEW_BASE_URL), builder ->
                    builder.POST("", reviewHandler::addReview)
                            .GET("", reviewHandler::getReviews)
                            .PUT("{id}", reviewHandler::uptadeReview)
                            .DELETE("{id}", reviewHandler::deleteReview)

                )
                .build();
    }
}
