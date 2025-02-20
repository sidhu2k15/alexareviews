package com.signify.alexareviews.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "reviews", indexes = { @Index(name = "idx_reviewed_date", columnList = "reviewedDate"),
		@Index(name = "idx_review_source", columnList = "reviewSource"),
		@Index(name = "idx_rating", columnList = "rating") })
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID) // Use UUID generation strategy
	private UUID id;
	@JsonProperty("review")
	private String review;
	@JsonProperty("author")
	private String author;
	@JsonProperty("review_source")
	private String reviewSource; // Google Play or iTunes
	@JsonProperty("rating")
	private int rating;
	@JsonProperty("title")
	private String title;
	@JsonProperty("product_name")
	private String productName;
	@JsonProperty("reviewed_date")
	private LocalDate reviewedDate;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getReviewSource() {
		return reviewSource;
	}

	public void setReviewSource(String reviewSource) {
		this.reviewSource = reviewSource;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public LocalDate getReviewedDate() {
		return reviewedDate;
	}

	public void setReviewedDate(LocalDate reviewedDate) {
		this.reviewedDate = reviewedDate;
	}

	@Override
	public String toString() {
		return "Review [id=" + id + ", review=" + review + ", author=" + author + ", reviewSource=" + reviewSource
				+ ", rating=" + rating + ", title=" + title + ", productName=" + productName + ", reviewedDate="
				+ reviewedDate + "]";
	}

}
