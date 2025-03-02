package com.signify.alexareviews.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.signify.alexareviews.entity.Review;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class ReviewRepositoryTest {

	@Autowired
	private ReviewRepository reviewRepository;

	@BeforeEach
	void setUp() {
		Review review1 = new Review(UUID.randomUUID(), "Excellent product", "John Doe", "Google", 5, "Great!", "Alexa",
				LocalDate.of(2024, 2, 1));
		Review review2 = new Review(UUID.randomUUID(), "Average experience", "Jane Doe", "Apple", 3, "Okay", "Alexa",
				LocalDate.of(2024, 2, 2));
		Review review3 = new Review(UUID.randomUUID(), "Good", "Alice", "Google", 4, "Nice", "Alexa",
				LocalDate.of(2024, 2, 3));
		reviewRepository.saveAll(List.of(review1, review2, review3));
	}

	// Positive Test Cases
	@Test
	void testFindByReviewedDateBetween() {
		LocalDate startDate = LocalDate.of(2024, 2, 1);
		LocalDate endDate = LocalDate.of(2024, 2, 2);
		Optional<List<Review>> reviews = reviewRepository.findByReviewedDateBetween(startDate, endDate);
		assertThat(reviews).isPresent();
		assertThat(reviews.get()).hasSize(2);
	}

	@Test
	void testFindByReviewSource() {
		Optional<List<Review>> reviews = reviewRepository.findByReviewSource("Google");
		assertThat(reviews).isPresent();
		assertThat(reviews.get()).hasSize(2);
	}

	@Test
	void testFindByRating() {
		Optional<List<Review>> reviews = reviewRepository.findByRating(5);
		assertThat(reviews).isPresent();
		assertThat(reviews.get()).hasSize(1);
	}

	@Test
	void testGetMonthlyAverageRatings() {
		Optional<List<Map<String, Object>>> avgRatings = reviewRepository.getMonthlyAverageRatings();
		assertThat(avgRatings).isPresent();
		assertThat(avgRatings.get()).isNotEmpty();
	}

	@Test
	void testGetTotalRatingsByCategory() {
		Optional<List<Map<String, Object>>> ratingCounts = reviewRepository.getTotalRatingsByCategory();
		assertThat(ratingCounts).isPresent();
		assertThat(ratingCounts.get()).isNotEmpty();
	}

	// Negative Test Cases
	@Test
	void testFindByReviewedDateBetween_NoResults() {
		LocalDate startDate = LocalDate.of(2023, 1, 1);
		LocalDate endDate = LocalDate.of(2023, 12, 31);
		Optional<List<Review>> reviews = reviewRepository.findByReviewedDateBetween(startDate, endDate);
		assertThat(reviews).isPresent();
		assertThat(reviews.get()).isEmpty();
	}

	@Test
	void testFindByReviewSource_NoResults() {
		Optional<List<Review>> reviews = reviewRepository.findByReviewSource("Amazon");
		assertThat(reviews).isPresent();
		assertThat(reviews.get()).isEmpty();
	}

	@Test
	void testFindByRating_NoResults() {
		Optional<List<Review>> reviews = reviewRepository.findByRating(1);
		assertThat(reviews).isPresent();
		assertThat(reviews.get()).isEmpty();
	}

	@Test
	void testGetMonthlyAverageRatings_NoData() {
		reviewRepository.deleteAll();
		Optional<List<Map<String, Object>>> avgRatings = reviewRepository.getMonthlyAverageRatings();
		assertThat(avgRatings).isPresent();
		assertThat(avgRatings.get()).isEmpty();
	}

	@Test
	void testGetTotalRatingsByCategory_NoData() {
		reviewRepository.deleteAll();
		Optional<List<Map<String, Object>>> ratingCounts = reviewRepository.getTotalRatingsByCategory();
		assertThat(ratingCounts).isPresent();
		assertThat(ratingCounts.get()).isEmpty();
	}
}
