package com.signify.alexareviews.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.signify.alexareviews.entity.Review;
import com.signify.alexareviews.model.Response;
import com.signify.alexareviews.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/api/reviews")
@Tag(name = "Reviews", description = "Endpoints for managing Alexa app reviews") // API Tag for grouping in Swagger
public class ReviewController {

	private final ReviewService reviewService;

	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@PostMapping("/import")
	public ResponseEntity<String> importReviews() {
		try {
			reviewService.importReviewsFromFile("src/main/resources/alexa.json");
			return ResponseEntity.ok("Reviews imported successfully.");
		} catch (IOException e) {
			return ResponseEntity.internalServerError().body("Error importing reviews: " + e.getMessage());
		}
	}

	@PostMapping
	@Operation(summary = "Add a new review", description = "Stores a new review in the database")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Review added successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Review.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input data"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<Review> addReview(@RequestBody Review review) {
		return ResponseEntity.ok(reviewService.saveReview(review));
	}

	@GetMapping
	@Operation(summary = "Get all reviews", description = "Fetches reviews based on optional filters")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully fetched reviews"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<Response<List<Review>>> getReviews(
			@Parameter(description = "Start date for filtering") @RequestParam(required = false) LocalDate startDate,
			@Parameter(description = "End date for filtering") @RequestParam(required = false) LocalDate endDate,
			@Parameter(description = "Store type (Google Play or iTunes)") @RequestParam(required = false) String storeType,
			@Parameter(description = "Filter by rating") @RequestParam(required = false) Integer rating) {
		return ResponseEntity.ok(reviewService.getReviews(startDate, endDate, storeType, rating));
	}

	@GetMapping("/monthly-average")
	@Operation(summary = "Get monthly average ratings", description = "Fetches the average ratings per month")
	public ResponseEntity<Response<List<Map<String, Object>>>> getMonthlyAverageRatings() {
		return ResponseEntity.ok(reviewService.getMonthlyAverageRatings());
	}

	@GetMapping("/total-ratings")
	@Operation(summary = "Get total ratings by category", description = "Fetches the total number of ratings grouped by category")
	public ResponseEntity<Response<List<Map<String, Object>>>> getTotalRatingsByCategory() {
		return ResponseEntity.ok(reviewService.getTotalRatingsByCategory());
	}
}
