package com.apb.review.impl;

import com.apb.review.dto.ReviewRequest;
import com.apb.review.dto.SalonDTO;
import com.apb.review.dto.UserDTO;
import com.apb.review.model.Review;
import com.apb.review.repository.ReviewRepository;
import com.apb.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

    @Override
    public Review createReview(ReviewRequest request, UserDTO user, SalonDTO salon) {
        Review review = new Review();
        review.setReviewText(request.getReviewText());
        review.setRating(request.getRating());
        review.setUserId(user.getId());
        review.setSalonId(salon.getId());

        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getReviewsBySalonId(Long salonId) {
        return reviewRepository.findBySalonId(salonId);
    }

    private Review getReviewById(Long reviewId) throws Exception {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new Exception("Review not found"));
    }

    @Override
    public Review updateReview(ReviewRequest request, Long reviewId, Long userId) throws Exception {
        Review review = getReviewById(reviewId);
        if(!review.getUserId().equals(userId)) {
            throw new Exception("You don't have permission to update this review");
        }
        review.setReviewText(request.getReviewText());
        review.setRating(request.getRating());
        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long reviewId, Long userId) throws Exception {
        Review review = getReviewById(reviewId);
        if(!review.getUserId().equals(userId)) {
            throw new Exception("You don't have permission to delete this review");
        }
        reviewRepository.delete(review);
    }
}
