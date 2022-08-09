package com.reactivespring.movieinfoservice.service;

import com.reactivespring.movieinfoservice.controller.MoviesInfoController;
import com.reactivespring.movieinfoservice.domain.MovieInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
class MovieInfoServiceTest {

  @Autowired WebTestClient webTestClient;

  @MockBean MovieInfoService movieInfoService;

  List<MovieInfo> movieList;

  @BeforeEach
  void setUp() {
    movieList =
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
    when(movieInfoService.addMovieInfo(movieList.get(0))).thenReturn(Mono.just(movieList.get(0)));
    when(movieInfoService.getAllMovies()).thenReturn(Flux.fromIterable(movieList));
    when(movieInfoService.getMovieById("abc")).thenReturn(Mono.just(movieList.get(2)));
    when(movieInfoService.updateMovie(movieList.get(2), "abc")).thenReturn(Mono.just(movieList.get(2)));
    when(movieInfoService.deleteMoviesbyId("abc")).thenReturn(Mono.empty());
  }

  @Test
  void addMovieInfo() {
    webTestClient
        .post()
        .uri("/v1/movieinfos")
        .bodyValue(movieList.get(0))
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(MovieInfo.class)
        .consumeWith(
            movieInfoEntityExchangeResult -> {
              var responseBody = movieInfoEntityExchangeResult.getResponseBody();
              assert responseBody != null;
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
                                  .filter(x -> x.getMovieInfoId() != null && x.getMovieInfoId().equals("abc"))
                                  .count()
                                  == 1;
                      });
  }

  @Test
  void getMovieById() {
      webTestClient
              .get()
              .uri("/v1/movieinfos/abc")
              .exchange()
              .expectBody()
              .jsonPath("$.name")
              .isEqualTo("Dark Knight Rises");
  }

  @Test
  void updateMovie() {
      movieList.get(2).setYear(2021);
      movieList.get(2).setName("Batman begins");
      webTestClient
              .put()
              .uri("/v1/movieinfos/abc")
              .bodyValue(movieList.get(2))
              .exchange()
              .expectStatus()
              .is2xxSuccessful()
              .expectBody(MovieInfo.class)
              .consumeWith(
                      movieInfoEntityExchangeResult -> {
                          var responseBody = movieInfoEntityExchangeResult.getResponseBody();
                          assert Objects.requireNonNull(responseBody).getYear() == 2021;
                          assert Objects.requireNonNull(responseBody).getName().equals("Batman begins");
                      });
  }

  @Test
  void deleteMoviesbyId() {
      webTestClient
              .delete()
              .uri("/v1/movieinfos/abc")
              .exchange()
              .expectStatus()
              .is2xxSuccessful();
  }
}
