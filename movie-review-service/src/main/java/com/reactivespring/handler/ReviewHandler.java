package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ReviewHandler {
  private final ReviewReactiveRepository reviewReactiveRepository;

  public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
    this.reviewReactiveRepository = reviewReactiveRepository;
  }

  public Mono<ServerResponse> addReview(ServerRequest req) {
    return req.bodyToMono(Review.class)
        .flatMap(reviewReactiveRepository::save)
        .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
  }

  public Mono<ServerResponse> getAllReview(ServerRequest serverRequest) {
    var movieInfoId = serverRequest.queryParam("movieInfoId");
    if (movieInfoId.isPresent()) {
      var reviewFlux =
          reviewReactiveRepository.findReviewsByMovieInfoId(Long.valueOf(movieInfoId.get())).log();
      return ServerResponse.status(HttpStatus.OK).body(reviewFlux, Review.class);
    }
    var allMovieReviews = reviewReactiveRepository.findAll().log();
    return ServerResponse.status(HttpStatus.OK).body(allMovieReviews, Review.class);
  }

  public Mono<ServerResponse> updateReview(ServerRequest serverRequest) {
    var reviewId = serverRequest.pathVariable("id");
    var findReviewById = reviewReactiveRepository.findById(reviewId).log();
    return findReviewById.flatMap(
        review ->
            serverRequest
                .bodyToMono(Review.class)
                .map(
                    reqReview -> {
                      BeanUtils.copyProperties(reqReview, review);
                      return review;
                    })
                .flatMap(reviewReactiveRepository::save)
                .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview)));
  }

  public Mono<ServerResponse> deleteReview(ServerRequest serverRequest) {
    var reviewId = serverRequest.pathVariable("id");
    return reviewReactiveRepository
        .deleteById(reviewId)
        .flatMap((s) -> ServerResponse.noContent().build());
  }
}
