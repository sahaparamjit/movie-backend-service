package com.reactivespring.movieinfoservice.controller;

import com.reactivespring.movieinfoservice.domain.MovieInfo;
import com.reactivespring.movieinfoservice.service.MovieInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

  private final MovieInfoService movieInfoService;

  public MoviesInfoController(MovieInfoService movieInfoService) {
    this.movieInfoService = movieInfoService;
  }

  @PostMapping("movieinfos")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
    return movieInfoService.addMovieInfo(movieInfo).log();
  }

  @GetMapping("/movieinfos")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public Flux<MovieInfo> getAllMovies() {
    return movieInfoService.getAllMovies();
  }

  @GetMapping("/movieinfos/{id}")
  public Mono<ResponseEntity<MovieInfo>> getMovieById(@PathVariable String id) {
    return movieInfoService.getMovieById(id).map(ResponseEntity.ok()::body)
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

  @PutMapping("/movieinfos/{id}")
  public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@RequestBody @Valid MovieInfo movieInfo, @PathVariable String id) {
    return movieInfoService.updateMovie(movieInfo, id)
            .map(ResponseEntity.ok()::body)
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

  @DeleteMapping("/movieinfos/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteMovieInfoById(@PathVariable String id) {
    return movieInfoService.deleteMoviesbyId(id);
  }
}
