package com.reactivespring.movieinfoservice.controller;

import com.reactivespring.movieinfoservice.domain.MovieInfo;
import com.reactivespring.movieinfoservice.service.MovieInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

  private MovieInfoService movieInfoService;

  public MoviesInfoController(MovieInfoService movieInfoService) {
    this.movieInfoService = movieInfoService;
  }

  @PostMapping("movieinfos")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<MovieInfo> addMovieInfo(@RequestBody MovieInfo movieInfo) {
    return movieInfoService.addMovieInfo(movieInfo).log();
  }

  @GetMapping("/movieinfos")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public Flux<MovieInfo> getAllMovies() {
    return movieInfoService.getAllMovies();
  }

  @GetMapping("/movieinfos/{id}")
  public Mono<MovieInfo> getMovieById(@PathVariable String id) {
    return movieInfoService.getMovieById(id);
  }
}
