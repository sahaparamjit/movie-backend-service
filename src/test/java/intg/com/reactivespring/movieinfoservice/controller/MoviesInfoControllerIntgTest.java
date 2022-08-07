package com.reactivespring.movieinfoservice.controller;

import com.reactivespring.movieinfoservice.domain.MovieInfo;
import com.reactivespring.movieinfoservice.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerIntgTest {

  @Autowired MovieInfoRepository movieInfoRepository;

  @Autowired WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    var movieList =
        List.of(
            new MovieInfo(
                null,
                "Batman Begins",
                2005,
                List.of("Christian Bale", "Michael Cane"),
                LocalDate.parse("2005-06-15")),
            new MovieInfo(
                null,
                "The Dark Knight",
                2008,
                List.of("Christian Bale", "HeathLedger"),
                LocalDate.parse("2008-07-18")),
            new MovieInfo(
                "abc",
                "Dark Knight Rises",
                2012,
                List.of("Christian Bale", "Tom Hardy"),
                LocalDate.parse("2012-07-20")));

    movieInfoRepository.saveAll(movieList).blockLast();
  }

  @AfterEach
  void tearDown() {
    movieInfoRepository.deleteAll().block();
  }

  @Test
  void addMovieInfo() {
    var movieInfo =
        new MovieInfo(
            null,
            "Batman Begins",
            2005,
            List.of("Christian Bale", "Michael Cane"),
            LocalDate.parse("2005-06-15"));
    webTestClient
        .post()
        .uri("/v1/movieinfos")
        .bodyValue(movieInfo)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(MovieInfo.class)
        .consumeWith(
            movieInfoEntityExchangeResult -> {
              var responseBody = movieInfoEntityExchangeResult.getResponseBody();
              assert responseBody != null;
              assertNotNull(responseBody.getMovieInfoId());
              assert (responseBody.getName().equals("Batman Begins"));
            });
  }

  @Test
  void getAllMovies() {
    webTestClient
        .get()
        .uri("/v1/movieinfos")
        .exchange()
        .expectBodyList(MovieInfo.class)
        .hasSize(3)
        .consumeWith(
            listEntityExchangeResult -> {
              var responseBody = listEntityExchangeResult.getResponseBody();
              assert Objects.requireNonNull(responseBody).stream()
                      .filter(x -> x.getMovieInfoId().equals("abc")).count()
                  == 1;
            });
  }

  @Test
  void getMoviesById() {
    webTestClient
        .get()
        .uri("/v1/movieinfos/abc")
        .exchange()
        .expectBody()
            .jsonPath("$.name").isEqualTo("Dark Knight Rises");
        /*.consumeWith(
            listEntityExchangeResult -> {
              var responseBody = listEntityExchangeResult.getResponseBody();
              assert Objects.requireNonNull(responseBody).getMovieInfoId().equals("abc");
              assert responseBody.getName().equals("Dark Knight Rises");
              assert responseBody.getYear() == 2012;
            });*/
  }
}
