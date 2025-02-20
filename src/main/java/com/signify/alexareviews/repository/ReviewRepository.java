package com.signify.alexareviews.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.signify.alexareviews.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Optional<List<Review>> findByReviewedDateBetween(LocalDate startDate, LocalDate endDate);
    
    Optional<List<Review>> findByReviewSource(String reviewSource);
    
    Optional<List<Review>> findByRating(int rating);

    @Query("SELECT r.reviewSource AS store, YEAR(r.reviewedDate) AS year, MONTH(r.reviewedDate) AS month, AVG(r.rating) AS avgRating FROM Review r GROUP BY r.reviewSource, YEAR(r.reviewedDate), MONTH(r.reviewedDate)")
    Optional<List<Map<String, Object>>> getMonthlyAverageRatings();

    @Query("SELECT r.rating AS rating, COUNT(r) AS count FROM Review r GROUP BY r.rating")
    Optional<List<Map<String, Object>>> getTotalRatingsByCategory();
}
