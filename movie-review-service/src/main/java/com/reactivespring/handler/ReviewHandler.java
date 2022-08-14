package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReviewHandler {
    private Validator validator;
    private final ReviewReactiveRepository reviewReactiveRepository;

    public ReviewHandler(Validator validator, ReviewReactiveRepository reviewReactiveRepository) {
        this.validator = validator;
        this.reviewReactiveRepository = reviewReactiveRepository;
    }

    public Mono<ServerResponse> addReview(ServerRequest req) {
        return req.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .doOnError(throwable -> ServerResponse.badRequest().bodyValue(throwable.getMessage()))
                .flatMap(reviewReactiveRepository::save)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    private void validate(Review review) {
        var constraintValidations = validator.validate(review);
        log.info("Constraint Violations : {}", constraintValidations);
        if (constraintValidations.size() > 0) {
            var errors = constraintValidations.stream().map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(","));
            throw new ReviewDataException(errors);
        }
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
        var findReviewById = reviewReactiveRepository.findById(reviewId)
                .switchIfEmpty(
                        Mono.error(new ReviewNotFoundException(String.format("Review not found for id, %s", reviewId))))
                .log();
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
