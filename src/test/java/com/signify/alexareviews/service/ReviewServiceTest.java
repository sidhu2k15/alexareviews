package com.signify.alexareviews.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.signify.alexareviews.entity.Review;
import com.signify.alexareviews.model.Response;
import com.signify.alexareviews.repository.ReviewRepository;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private ReviewService reviewService;

	private Review review;
	private Review review1;
	private Review review2;
	private static Path tempFile;

	@BeforeAll
	static void setupTempFile() throws IOException {
		tempFile = Files.createTempFile("test-reviews", ".json");
		Files.write(tempFile, List.of(
				"{\"review\":\"Great app!\",\"author\":\"John Doe\",\"review_source\":\"Google Play\",\"rating\":5,\"title\":\"Awesome\",\"product_name\":\"Alexa\",\"reviewed_date\":\"2024-03-02\"}",
				"{\"invalid\":\"bad json\"}"));
	}

	@BeforeEach
	public void setup() {
		review = new Review(UUID.randomUUID(), "Great app!", "John Doe", "Google Play", 5, "Awesome", "Alexa",
				LocalDate.now());
		review1 = new Review(UUID.randomUUID(), "Great Alexa skill!", "John Doe", "Google", 5, "Awesome", "Alexa",
				LocalDate.of(2024, 2, 1));
		review2 = new Review(UUID.randomUUID(), "Average experience", "Jane Doe", "Apple", 3, "Okay", "Alexa",
				LocalDate.of(2024, 2, 2));
	}

	@Test
	public void importReviewsFromFile_ValidReview_Success() throws IOException {
		when(objectMapper.readValue(anyString(), eq(Review.class))).thenReturn(review);
		when(reviewRepository.save(any(Review.class))).thenReturn(review);

		reviewService.importReviewsFromFile(tempFile.toString());

		verify(reviewRepository, times(2)).save(any(Review.class));
	}

	@Test
	public void importReviewsFromFile_FileNotFound_ThrowsException() {
		assertThatThrownBy(() -> reviewService.importReviewsFromFile("invalid-path.json"))
				.isInstanceOf(IOException.class);
	}

	@Test
	public void saveReview_ValidReview_Success() {
		when(reviewRepository.save(any(Review.class))).thenReturn(review);

		Review savedReview = reviewService.saveReview(review);

		assertThat(savedReview).isNotNull();
		assertThat(savedReview.getId()).isEqualTo(review.getId());

		verify(reviewRepository, times(1)).save(review);
	}

	@Test
	void shouldThrowExceptionWhenReviewIsNull() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			reviewService.saveReview(null);
		});

		assertEquals("Review cannot be null", exception.getMessage());
		verify(reviewRepository, never()).save(any(Review.class)); // Ensure repo is never called
	}

	@Test
	public void saveReview_DatabaseError_ThrowsException() {
		when(reviewRepository.save(any(Review.class))).thenThrow(new RuntimeException("Database Error"));

		assertThatThrownBy(() -> reviewService.saveReview(review)).isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Database Error");

		verify(reviewRepository, times(1)).save(any(Review.class));
	}

	@Test
	void testGetReviews_FilterByDate() {
		LocalDate startDate = LocalDate.of(2024, 2, 1);
		LocalDate endDate = LocalDate.of(2024, 2, 2);
		when(reviewRepository.findByReviewedDateBetween(startDate, endDate))
				.thenReturn(Optional.of(List.of(review1, review2)));

		Response<List<Review>> response = reviewService.getReviews(startDate, endDate, null, null);

		assertThat(response.isSuccess()).isTrue();
		assertThat(response.getCode()).contains("Success");
		assertThat(response.getMessage()).contains("Fetch reviews with start and end date as filter succeeded");
		assertThat(response.getData()).hasSize(2);
	}

	@Test
	void testGetReviewsByDateRange_NoResults() {
		// Given: A date range where no reviews exist
		LocalDate startDate = LocalDate.of(2023, 1, 1);
		LocalDate endDate = LocalDate.of(2023, 12, 31);

		// Mock repository to return an empty Optional
		when(reviewRepository.findByReviewedDateBetween(startDate, endDate))
				.thenReturn(Optional.of(Collections.emptyList()));

		// When: Service method is called
		Response<List<Review>> response = reviewService.getReviews(startDate, endDate, null, null);

		// Then: Verify response
		assertThat(response.isSuccess()).isFalse();
		assertThat(response.getCode()).contains("FAILED");
		assertThat(response.getMessage()).isEqualTo("Failed to fetch reviews with start and end date as filter");
		assertThat(response.getData()).isNull();
	}

	@Test
	void testGetReviews_FilterByStoreType() {
		when(reviewRepository.findByReviewSource("Google")).thenReturn(Optional.of(List.of(review1)));

		Response<List<Review>> response = reviewService.getReviews(null, null, "Google", null);

		assertThat(response.isSuccess()).isTrue();
		assertThat(response.getCode()).contains("Success");
		assertThat(response.getMessage()).contains("Fetch reviews with storeType as filter succeeded");
		assertThat(response.getData()).hasSize(1);
		assertThat(response.getData().get(0).getReviewSource()).isEqualTo("Google");
	}

	@Test
	void testGetReviewsByStoreType_NoResults() {
		// Given: A store type that has no reviews
		String storeType = "NonExistentStore";

		// Mock repository to return an Optional containing an empty list
		when(reviewRepository.findByReviewSource(storeType)).thenReturn(Optional.of(Collections.emptyList()));

		// When: Service method is called
		Response<List<Review>> response = reviewService.getReviews(null, null, storeType, null);

		// Then: Verify response
		assertThat(response.isSuccess()).isFalse();
		assertThat(response.getCode()).contains("FAILED");
		assertThat(response.getMessage()).isEqualTo("Failed to fetch reviews with storeType as parameter");
		assertThat(response.getData()).isNull();
	}

	@Test
	void testGetReviews_FilterByRating() {
		when(reviewRepository.findByRating(5)).thenReturn(Optional.of(List.of(review1)));

		Response<List<Review>> response = reviewService.getReviews(null, null, null, 5);

		assertThat(response.isSuccess()).isTrue();
		assertThat(response.getCode()).contains("Success");
		assertThat(response.getData()).hasSize(1);
		assertThat(response.getData().get(0).getRating()).isEqualTo(5);
	}

	@Test
	void testGetReviewsByRating_NoResults() {
		// Given: A rating that has no reviews
		int rating = 1;

		// Mock repository to return an Optional containing an empty list
		when(reviewRepository.findByRating(rating)).thenReturn(Optional.of(Collections.emptyList()));

		// When: Service method is called
		Response<List<Review>> response = reviewService.getReviews(null, null, null, rating);

		// Then: Verify response
		assertThat(response.isSuccess()).isFalse();
		assertThat(response.getCode()).contains("FAILED");
		assertThat(response.getMessage()).isEqualTo("Failed to fetch reviews with rating as parameter");
		assertThat(response.getData()).isNull();

	}

	@Test
	void testGetReviews_NoFilters_FetchAll() {
		when(reviewRepository.findAll()).thenReturn(List.of(review1, review2));

		Response<List<Review>> response = reviewService.getReviews(null, null, null, null);

		assertThat(response.isSuccess()).isTrue();
		assertThat(response.getCode()).contains("Success");
		assertThat(response.getMessage()).isEqualTo("Fetching all reviews succeeded");
		assertThat(response.getData()).hasSize(2);
	}

	@Test
	public void getMonthlyAverageRatings_Success() {
		List<Map<String, Object>> ratings = List.of(Map.of("month", 3, "avgRating", 4.5));
		when(reviewRepository.getMonthlyAverageRatings()).thenReturn(Optional.of(ratings));

		Response<List<Map<String, Object>>> response = reviewService.getMonthlyAverageRatings();

		assertThat(response.isSuccess()).isTrue();
		assertThat(response.getCode()).contains("Success");
		assertThat(response.getMessage()).isEqualTo("Succeeded in getting Monthly Average Ratings");
		assertThat(response.getData()).hasSize(1);
	}

	@Test
	public void getMonthlyAverageRatings_EmptyResult_Failure() {
		when(reviewRepository.getMonthlyAverageRatings()).thenReturn(Optional.empty());

		Response<List<Map<String, Object>>> response = reviewService.getMonthlyAverageRatings();

		assertThat(response.isSuccess()).isFalse();
		assertThat(response.getCode()).contains("FAILED");
		assertThat(response.getMessage()).contains("Failed to fetch Monthly Average Ratings");
	}

	@Test
	public void getTotalRatingsByCategory_Success() {
		List<Map<String, Object>> ratings = List.of(Map.of("rating", 5, "count", 10));
		when(reviewRepository.getTotalRatingsByCategory()).thenReturn(Optional.of(ratings));

		Response<List<Map<String, Object>>> response = reviewService.getTotalRatingsByCategory();

		assertThat(response.isSuccess()).isTrue();
		assertThat(response.getCode()).contains("Success");
		assertThat(response.getMessage()).isEqualTo("Succeeded in getting Total Ratings By Category");
		assertThat(response.getData()).isNotEmpty();
	}

	@Test
	public void getTotalRatingsByCategory_EmptyResult_Failure() {
		when(reviewRepository.getTotalRatingsByCategory()).thenReturn(Optional.empty());

		Response<List<Map<String, Object>>> response = reviewService.getTotalRatingsByCategory();

		assertThat(response.isSuccess()).isFalse();
		assertThat(response.getCode()).contains("FAILED");
		assertThat(response.getMessage()).contains("Failed to fetch Total Ratings By Category");
	}

	@AfterAll
	static void cleanup() throws IOException {
		Files.deleteIfExists(tempFile);
	}
}
