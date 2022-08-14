package com.reactivespring.router;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class RouterTest {

  @Autowired WebTestClient webTestClient;

  @Autowired ReviewReactiveRepository reviewReactiveRepository;

  private final List<Review> reviewList = new ArrayList<>();

  @BeforeEach
  void setUp() {
    reviewList.addAll(
        Arrays.asList(
            new Review(null, 1L, "awesome movie", 4.5),
            new Review("abc", 2L, "great script", 4.2)));
    reviewReactiveRepository.saveAll(reviewList).blockLast();
  }

  @AfterEach
  void tearDown() {
    reviewReactiveRepository.deleteAll().block();
  }

  @Test
  void postReviewRoute() {
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
  void getAllReviewsRoute() {
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
  void getReviewsByInfoIdRoute() {
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
  void updateReviewRoute() {
    var update = new Review("abc", 2L, "decent movie", 4.0);
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
    void testUpdateReview_reviewNotFound() {
        var update = new Review("abc", 2L, "decent movie", 4.0);
        webTestClient
                .put()
                .uri("/v1/reviews/asd")
                .bodyValue(update)
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(String.class)
                .isEqualTo("Review not found for id, asd");
    }

  @Test
  void deleteReviewRoute() {
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
