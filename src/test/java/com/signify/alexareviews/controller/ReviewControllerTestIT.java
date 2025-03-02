package com.signify.alexareviews.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.signify.alexareviews.entity.Review;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReviewControllerTestIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/v1/api/reviews";
    }

    @Test
    void testImportReviews() {
        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/import", null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Reviews imported successfully");
    }

    @Test
    void testAddReview() {
        Review review = new Review(UUID.randomUUID(), "Great Alexa skill!", "John Doe", "Google Play", 
                                   5, "Awesome", "Alexa Skill", LocalDate.now());

        ResponseEntity<Review> response = restTemplate.postForEntity(getBaseUrl(), review, Review.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getReview()).isEqualTo("Great Alexa skill!");
    }

    @Test
    void testGetReviews() {
        ResponseEntity<Map> response = restTemplate.getForEntity(getBaseUrl(), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testGetMonthlyAverageRatings() {
        ResponseEntity<Map> response = restTemplate.getForEntity(getBaseUrl() + "/monthly-average", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testGetTotalRatingsByCategory() {
        ResponseEntity<Map> response = restTemplate.getForEntity(getBaseUrl() + "/total-ratings", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }
}
