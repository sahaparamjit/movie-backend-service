package com.reactivespring.router;

import com.reactivespring.domain.Review;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {Router.class, ReviewHandler.class})
@AutoConfigureWebTestClient
class ReviewRouterTest {
  @MockBean ReviewReactiveRepository reviewReactiveRepository;

  @Autowired WebTestClient webTestClient;

  private final List<Review> reviewList = new ArrayList<>();

  @BeforeEach
  void setUp() {
    reviewList.addAll(
        Arrays.asList(
            new Review(null, 1L, "awesome movie", 4.5),
            new Review("abc", 2L, "great script", 4.2)));
    when(reviewReactiveRepository.findAll()).thenReturn(Flux.fromIterable(reviewList));
    when(reviewReactiveRepository.deleteById(any(String.class))).thenReturn(Mono.empty());
    reviewList.get(0).setReviewId("ced");
    when(reviewReactiveRepository.save(any(Review.class))).thenReturn(Mono.just(reviewList.get(0)));
    when(reviewReactiveRepository.findById(any(String.class)))
        .thenReturn(Mono.just(reviewList.get(1)));
    when(reviewReactiveRepository.findReviewsByMovieInfoId(any(Long.class)))
        .thenReturn(Flux.just(reviewList.get(0)));
  }

  @Test
  void testAddReview() {
    webTestClient
        .post()
        .uri("/v1/reviews")
        .bodyValue(reviewList.get(0))
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(Review.class)
        .consumeWith(
            reviewEntityExchangeResult -> {
              var responseBody = reviewEntityExchangeResult.getResponseBody();
              assert Objects.requireNonNull(responseBody).getRating() == 4.5;
            });
  }

  @Test
  void testGetAllReviewRoute() {
    webTestClient
        .get()
        .uri("/v1/reviews")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(Review.class)
        .consumeWith(
            reviewEntityExchangeResult -> {
              var responseBody = reviewEntityExchangeResult.getResponseBody();
              assert Objects.requireNonNull(responseBody).size() == 2;
            });
  }

  @Test
  void testReviewByInfoId() {
    webTestClient
        .get()
        .uri("/v1/reviews?movieInfoId=1")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(Review.class)
        .consumeWith(
            reviewEntityExchangeResult -> {
              var responseBody = reviewEntityExchangeResult.getResponseBody();
              assert Objects.requireNonNull(responseBody).size() == 1;
            });
  }

  @Test
  void testUpdateReview() {
    var update = new Review("abc", 2L, "decent movie", 4.0);
    when(reviewReactiveRepository.save(any(Review.class))).thenReturn(Mono.just(update));
    webTestClient
        .put()
        .uri("/v1/reviews/abc")
        .bodyValue(update)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(Review.class)
        .consumeWith(
            reviewEntityExchangeResult -> {
              var responseBody = reviewEntityExchangeResult.getResponseBody();
              assert Objects.requireNonNull(responseBody).getComment().equals(update.getComment());
              assert Objects.equals(
                  Objects.requireNonNull(responseBody).getRating(), update.getRating());
            });
  }

  @Test
  void testDeleteReview() {
    webTestClient
        .delete()
        .uri("/v1/reviews/abc")
        .exchange()
        .expectBody(Void.class)
        .consumeWith(
            reviewEntityExchangeResult -> {
              var responseBody = reviewEntityExchangeResult.getStatus();
              assert responseBody.is2xxSuccessful();
            });
  }
}
