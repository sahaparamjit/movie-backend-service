package com.reactivespring.router;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;

@Configuration
public class Router {
  @Bean
  public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler) {
    return RouterFunctions.route()
        .nest(
            path("/v1/reviews"),
            builder ->
                builder
                    .POST("", reviewHandler::addReview)
                    .GET("", reviewHandler::getAllReview)
                    .PUT("/{id}", reviewHandler::updateReview)
                    .DELETE("/{id}", reviewHandler::deleteReview))
        // Old route definitions
        .GET("/v1/helloworl", (req) -> ServerResponse.ok().bodyValue("hello-world"))
        /*.POST("/v1/reviews", reviewHandler::addReview)
        .GET("/v1/reviews", reviewHandler::getAllReview)*/
        .build();
  }
}
