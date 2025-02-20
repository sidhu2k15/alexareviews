package com.signify.alexareviews.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.signify.alexareviews.entity.Review;
import com.signify.alexareviews.model.Response;
import com.signify.alexareviews.repository.ReviewRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @InjectMocks
    private ReviewService reviewService;

    private Review sampleReview;

    @BeforeEach
    void setUp() {
        sampleReview = new Review();
        sampleReview.setId(UUID.randomUUID());
        sampleReview.setReviewSource("Amazon");
        sampleReview.setRating(5);
        sampleReview.setReviewedDate(LocalDate.now());
    }

    @Test
    void testSaveReview() {
        when(reviewRepository.save(any(Review.class))).thenReturn(sampleReview);
        Review savedReview = reviewService.saveReview(sampleReview);
        assertNotNull(savedReview);
        assertEquals(sampleReview.getId(), savedReview.getId());
    }

    @Test
    void testGetReviewsByDateRange() {
        List<Review> reviews = List.of(sampleReview);
        when(reviewRepository.findByReviewedDateBetween(any(), any())).thenReturn(Optional.of(reviews));

        Response<List<Review>> response = reviewService.getReviews(LocalDate.now().minusDays(10), LocalDate.now(), null, null);
        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
    }

    @Test
    void testGetReviewsByStoreType() {
        List<Review> reviews = List.of(sampleReview);
        when(reviewRepository.findByReviewSource(any())).thenReturn(Optional.of(reviews));

        Response<List<Review>> response = reviewService.getReviews(null, null, "Amazon", null);
        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
    }

    @Test
    void testGetReviewsByRating() {
        List<Review> reviews = List.of(sampleReview);
        when(reviewRepository.findByRating(anyInt())).thenReturn(Optional.of(reviews));

        Response<List<Review>> response = reviewService.getReviews(null, null, null, 5);
        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
    }

    @Test
    void testGetAllReviews() {
        List<Review> reviews = List.of(sampleReview);
        when(reviewRepository.findAll()).thenReturn(reviews);

        Response<List<Review>> response = reviewService.getReviews(null, null, null, null);
        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
    }

    @Test
    void testImportReviewsFromFile() throws IOException {
        // Create a temp JSON file
        Path tempFile = Files.createTempFile("test-reviews", ".json");
        Files.writeString(tempFile, "{\"reviewSource\": \"Amazon\", \"rating\": 5, \"reviewedDate\": \"2025-02-20\"}");

        // Create a sample Review object
        Review sampleReview = new Review();
        sampleReview.setReviewSource("Amazon");
        sampleReview.setRating(5);
        sampleReview.setReviewedDate(LocalDate.of(2025, 2, 20));

        // Mock ObjectMapper behavior
        when(objectMapper.readValue(any(String.class), any(Class.class))).thenReturn(sampleReview);

        // Mock repository behavior
        when(reviewRepository.save(any(Review.class))).thenReturn(sampleReview);

        // Run test
        assertDoesNotThrow(() -> reviewService.importReviewsFromFile(tempFile.toString()));
    }
}
