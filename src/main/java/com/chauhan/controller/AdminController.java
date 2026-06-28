package com.chauhan.controller;


import com.chauhan.model.Movie;
import com.chauhan.model.MovieRating;
import com.chauhan.model.Rating;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;



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
            Movie saveMovie = restTemplate.postForObject(movieServiceUrl, movie, Movie.class);

            return ResponseEntity.ok.body(saveMovie);
        } catch (HttpStatusCodeException ex) {
            log.error("Error Adding Movie:{}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> UpdateMovie(@PathVariable Long id, @RequestBody Movie movie) {

        try {
            log.info("Updating  movie {}", id);
            restTemplate.put(movieServiceUrl + "/" + id, movie);
            return ResponseEntity.ok().build();
        } catch (HttpStatusCodeException ex) {
            log.error("Error Adding Movie:{}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }

    }
        @GetMapping("/{id}")
        public ResponseEntity<Object> fetchMovieAndRating(@PathVariable long id)
        {

            Movie movie;
            try {
                movie = restTemplate.getForObject(movieServiceUrl + "/" + id, Movie.class);
            }
            catch(HttpStatusCodeException ex){
                    log.error("Error Adding Movie:{}", ex.getMessage());
                    return ResponseEntity.status(ex.getStatusCode())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(ex.getResponseBodyAsString());

        }
            Rating rating;
                    try {
                          rating=restTemplate.getForObject(ratingsServiceUrl + "/" + movie.getName(), Rating.class);
                    }
                    catch(HttpStatusCodeException ex)
                    {
                        if(ex.getStatusCode() == HttpStatus.NOT_FOUND)
                        {
                            rating = new Rating(null, movie.getName(), 0.0, 0);
                        }
                        else {

                            rating = new Rating(null, movie.getName(), -1.0, 0);

                        }
                    }

        MovieRating movieRating = new MovieRating();
        movieRating.setMovie(Movie);
        movieRating.setRating(rating);

        return ResponseEntity.ok(movieRating);

        }


}
