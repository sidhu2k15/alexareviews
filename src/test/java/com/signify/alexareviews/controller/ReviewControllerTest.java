package com.signify.alexareviews.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.signify.alexareviews.entity.Review;
import com.signify.alexareviews.model.Response;
import com.signify.alexareviews.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private Review review;

    @BeforeEach
    void setUp() {
        review = new Review(UUID.randomUUID(), "Great app!", "John Doe", "Google Play", 5,
                "Amazing", "Alexa App", LocalDate.now());
    }

    @Test
    void addReview_ShouldReturnReview() throws Exception {
        given(reviewService.saveReview(any(Review.class))).willReturn(review);

        ResultActions response = mockMvc.perform(post("/v1/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(review)));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.review", is(review.getReview())))
                .andExpect(jsonPath("$.author", is(review.getAuthor())));
    }

    @Test
    void getReviews_ShouldReturnListOfReviews() throws Exception {
        Response<List<Review>> responseObj = new Response<>(true, "200", "Success", Collections.singletonList(review));
        given(reviewService.getReviews(any(), any(), any(), any())).willReturn(responseObj);

        ResultActions response = mockMvc.perform(get("/v1/api/reviews"));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.code", is("200")))
                .andExpect(jsonPath("$.message", is("Success")))
                .andExpect(jsonPath("$.data[0].review", is(review.getReview())));
    }

    @Test
    void getMonthlyAverageRatings_ShouldReturnData() throws Exception {
        Response<List<Map<String, Object>>> responseObj = new Response<>(true, "200", "Success", Collections.emptyList());
        given(reviewService.getMonthlyAverageRatings()).willReturn(responseObj);

        ResultActions response = mockMvc.perform(get("/v1/api/reviews/monthly-average"));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.code", is("200")))
                .andExpect(jsonPath("$.message", is("Success")))
                .andExpect(jsonPath("$.data").isArray());
    }
}