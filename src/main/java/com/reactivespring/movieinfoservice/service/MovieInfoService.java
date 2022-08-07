package com.reactivespring.movieinfoservice.service;

import com.reactivespring.movieinfoservice.domain.MovieInfo;
import com.reactivespring.movieinfoservice.repository.MovieInfoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieInfoService {
  private MovieInfoRepository movieInfoRepository;

  public MovieInfoService(MovieInfoRepository movieInfoRepository) {
    this.movieInfoRepository = movieInfoRepository;
  }

  public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
    return movieInfoRepository.save(movieInfo).log();
  }

  public Flux<MovieInfo> getAllMovies() {
    return movieInfoRepository.findAll();
  }

  public Mono<MovieInfo> getMovieById(String id) {
    return movieInfoRepository.findById(id);
  }
}
