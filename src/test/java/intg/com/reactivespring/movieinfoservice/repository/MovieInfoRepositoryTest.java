package com.reactivespring.movieinfoservice.repository;

import com.reactivespring.movieinfoservice.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryTest {

  @Autowired MovieInfoRepository movieInfoRepository;

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
  void findAll() {
    Flux<MovieInfo> movieInfoFlux = movieInfoRepository.findAll().log();

    StepVerifier.create(movieInfoFlux).expectNextCount(3).verifyComplete();
  }

  @Test
  void findById() {
    Mono<MovieInfo> movieInfoFlux = movieInfoRepository.findById("abc").log();
    StepVerifier.create(movieInfoFlux)
        .assertNext(
            movieInfo -> {
              assert movieInfo.getName().equals("Dark Knight Rises");
              assert movieInfo.getYear() == 2012;
            })
        .verifyComplete();
  }

  @Test
  void saveMovieInfo() {
    MovieInfo movieInfo =
        new MovieInfo(
            null,
            "Batman Begins",
            2005,
            List.of("Christian Bale", "Michael Cane"),
            LocalDate.parse("2005-06-15"));
    Mono<MovieInfo> movieInfoFlux = movieInfoRepository.save(movieInfo).log();
    StepVerifier.create(movieInfoFlux)
        .assertNext(
            info -> {
              assert Objects.nonNull(info.getMovieInfoId());
              assert info.getName().equals("Batman Begins");
            })
        .verifyComplete();
  }

  @Test
  void updateMovieInfo() {
    // given
    var movieInfoMono = movieInfoRepository.findById("abc").log().block();
    assert movieInfoMono != null;
    movieInfoMono.setYear(2021);
    // when
    var movieInfoMonoUpdate = movieInfoRepository.save(movieInfoMono).log();
    StepVerifier.create(movieInfoMonoUpdate)
        .assertNext(
            info -> {
              assert info.getName().equals("Dark Knight Rises");
              assert info.getYear() == 2021;
            })
        .verifyComplete();
  }

  @Test
  void deleteMovieInfo() {
    movieInfoRepository.deleteById("abc").log().block();
    var allMovieList = movieInfoRepository.findAll().log();
    StepVerifier.create(allMovieList).expectNextCount(2).verifyComplete();
  }
}
