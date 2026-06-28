package com.chauhan.controller;

import com.chauhan.model.Rating;
import com.chauhan.model.RatingRequest;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/public")
@Slf4j
public class PublicController {


    private final RestTemplate restTemplate = new RestTemplate();



    @Value("${rating-service.url}")
    private String ratingsServiceUrl;

    public ResponseEntity<Object> addRating(@RequestBody RatingRequest ratingRequest)
    {
       Rating rating;

       try
       {
           rating = restTemplate.postForObject(ratingsServiceUrl, ratingRequest, Rating.class);
           return ResponseEntity.ok(rating);
       }
       catch (HttpStatusCodeException ex) {
           log.error("Error Adding Movie:{}", ex.getMessage());
           return ResponseEntity.status(ex.getStatusCode())
                   .contentType(MediaType.APPLICATION_JSON)
                   .body(ex.getResponseBodyAsString());
       }

    }



}
