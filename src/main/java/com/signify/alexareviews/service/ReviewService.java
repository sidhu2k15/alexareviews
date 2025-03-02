package com.signify.alexareviews.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.signify.alexareviews.entity.Review;
import com.signify.alexareviews.model.Response;
import com.signify.alexareviews.model.ResponseCode;
import com.signify.alexareviews.repository.ReviewRepository;

@Service
public class ReviewService {

	private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
	private final ReviewRepository reviewRepository;
	private final ObjectMapper objectMapper;

	public ReviewService(ReviewRepository reviewRepository, ObjectMapper objectMapper) {
		this.reviewRepository = reviewRepository;
		this.objectMapper = objectMapper;
	}

	public void importReviewsFromFile(String filePath) throws IOException {
		Logger logger = LoggerFactory.getLogger(getClass()); // Logger instance

		logger.info("Starting import of reviews from file: {}", filePath);

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				try {
					// Convert each line to a Review object
					Review review = objectMapper.readValue(line, Review.class);
					logger.debug("Parsed review: {}", review);

					review.setId(UUID.randomUUID()); // Assign a UUID if not present
					reviewRepository.save(review);

					logger.info("Successfully saved review with ID: {}", review.getId());
				} catch (Exception e) {
					logger.warn("Skipping invalid JSON line: {}", line, e);
				}
			}
		} catch (IOException ex) {
			logger.error("Failed to read file: {}", filePath, ex);
			throw ex; // Rethrow to let caller handle it
		}

		logger.info("Completed import of reviews from file: {}", filePath);
	}

	public Review saveReview(Review review) {
		Objects.requireNonNull(review, "Review cannot be null");
		logger.info("Saving review: {}", review);
		Review savedReview = reviewRepository.save(review);
		logger.info("Review saved successfully with ID: {}", savedReview.getId());
		return savedReview;
	}

	public Response<List<Review>> getReviews(LocalDate startDate, LocalDate endDate, String storeType, Integer rating) {
		logger.info("Fetching reviews with filters - startDate: {}, endDate: {}, storeType: {}, rating: {}", startDate,
				endDate, storeType, rating);

		if (startDate != null && endDate != null) {
			return reviewRepository.findByReviewedDateBetween(startDate, endDate).filter(reviews -> !reviews.isEmpty()) // Ensures
																														// we
																														// return
																														// only
																														// non-empty
																														// lists
					.map(reviews -> {
						logger.info("Successfully fetched {} reviews for date range {} to {}", reviews.size(),
								startDate, endDate);
						return new Response<>(true, "Success",
								"Fetch reviews with start and end date as filter succeeded", reviews);
					}).orElseGet(() -> {
						logger.warn("Failed to fetch reviews with start and end date as filter");
						return new Response<>(false, ResponseCode.FAILED.toString(),
								"Failed to fetch reviews with start and end date as filter");
					});
		}

		else if (storeType != null) {
			return reviewRepository.findByReviewSource(storeType).filter(reviews -> !reviews.isEmpty()) // Ensure only
																										// non-empty
																										// lists return
																										// success
					.map(reviews -> {
						logger.info("Successfully fetched {} reviews for storeType: {}", reviews.size(), storeType);
						return new Response<>(true, "Success", "Fetch reviews with storeType as filter succeeded",
								reviews);
					}).orElseGet(() -> {
						logger.warn("Failed to fetch reviews with storeType: {}", storeType);
						return new Response<>(false, ResponseCode.FAILED.toString(),
								"Failed to fetch reviews with storeType as parameter");
					});
		}

		else if (rating != null) {
			return reviewRepository.findByRating(rating).filter(ratings -> !ratings.isEmpty()) // Ensure only non-empty
																								// lists return success
					.map(ratings -> {
						logger.info("Successfully fetched {} reviews with rating: {}", ratings.size(), rating);
						return new Response<>(true, "Success", "Fetch reviews with rating as filter succeeded",
								ratings);
					}).orElseGet(() -> {
						logger.warn("Failed to fetch reviews with rating: {}", rating);
						return new Response<>(false, ResponseCode.FAILED.toString(),
								"Failed to fetch reviews with rating as parameter");
					});
		}

		else {
			List<Review> allReviews = reviewRepository.findAll();
			logger.info("Fetched all reviews, total count: {}", allReviews.size());
			return new Response<>(true, "Success", "Fetching all reviews succeeded", allReviews);
		}
	}

	public Response<List<Map<String, Object>>> getMonthlyAverageRatings() {
		logger.info("Fetching Monthly Average Ratings");
		return reviewRepository.getMonthlyAverageRatings().map(ratings -> {
			logger.info("Successfully fetched Monthly Average Ratings");
			return new Response<>(true, "Success", "Succeeded in getting Monthly Average Ratings", ratings);
		}).orElseGet(() -> {
			logger.warn("Failed to fetch Monthly Average Ratings");
			return new Response<>(false, ResponseCode.FAILED.toString(), "Failed to fetch Monthly Average Ratings");
		});
	}

	public Response<List<Map<String, Object>>> getTotalRatingsByCategory() {
		logger.info("Fetching Total Ratings by Category");
		return reviewRepository.getTotalRatingsByCategory().map(ratings -> {
			logger.info("Successfully fetched Total Ratings by Category");
			return new Response<>(true, "Success", "Succeeded in getting Total Ratings By Category", ratings);
		}).orElseGet(() -> {
			logger.warn("Failed to fetch Total Ratings by Category");
			return new Response<>(false, ResponseCode.FAILED.toString(), "Failed to fetch Total Ratings By Category");
		});
	}
}
