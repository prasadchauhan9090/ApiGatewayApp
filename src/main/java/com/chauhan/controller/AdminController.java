package com.chauhan.controller;

import com.chauhan.model.Movie;
import com.chauhan.model.MovieRating;
import com.chauhan.model.Rating;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/admin")
@Slf4j
public class AdminController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${movie-service.url}")
    private String movieServiceUrl;

    @Value("${rating-service.url}")
    private String ratingServiceUrl;

    @PostMapping
    public ResponseEntity<Object> addMovie(@RequestBody Movie movie) {

        try {

            log.info("Adding movie {}", movie);

            Movie savedMovie = restTemplate.postForObject(
                    movieServiceUrl,
                    movie,
                    Movie.class
            );

            return ResponseEntity.ok(savedMovie);

        } catch (HttpStatusCodeException ex) {

            log.error("Error Adding Movie: {}", ex.getMessage());

            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());

        } catch (ResourceAccessException ex) {

            log.error("Movie Service Unavailable: {}", ex.getMessage());

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Movie Service is unavailable.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateMovie(
            @PathVariable Long id,
            @RequestBody Movie movie) {

        try {

            log.info("Updating movie {}", id);

            restTemplate.put(movieServiceUrl + "/" + id, movie);

            return ResponseEntity.ok().build();

        } catch (HttpStatusCodeException ex) {

            log.error("Error Updating Movie: {}", ex.getMessage());

            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());

        } catch (ResourceAccessException ex) {

            log.error("Movie Service Unavailable: {}", ex.getMessage());

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Movie Service is unavailable.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> fetchMovieAndRating(@PathVariable Long id) {

        Movie movie;

        try {

            movie = restTemplate.getForObject(
                    movieServiceUrl + "/" + id,
                    Movie.class
            );

        } catch (HttpStatusCodeException ex) {

            log.error("Error Fetching Movie: {}", ex.getMessage());

            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());

        } catch (ResourceAccessException ex) {

            log.error("Movie Service Unavailable: {}", ex.getMessage());

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Movie Service is unavailable.");
        }

        Rating rating;

        try {

            rating = restTemplate.getForObject(
                    ratingServiceUrl + "/" + movie.getName(),
                    Rating.class
            );

        } catch (HttpStatusCodeException ex) {

            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {

                rating = new Rating(
                        null,
                        movie.getName(),
                        0.0,
                        0
                );

            } else {

                rating = new Rating(
                        null,
                        movie.getName(),
                        -1.0,
                        0
                );
            }

        } catch (ResourceAccessException ex) {

            log.warn("Rating Service Unavailable: {}", ex.getMessage());

            rating = new Rating(
                    null,
                    movie.getName(),
                    -1.0,
                    0
            );
        }

        MovieRating movieRating = new MovieRating();
        movieRating.setMovie(movie);
        movieRating.setRating(rating);

        return ResponseEntity.ok(movieRating);
    }
}