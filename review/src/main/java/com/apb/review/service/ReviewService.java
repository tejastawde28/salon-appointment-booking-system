package com.apb.review.service;


import com.apb.review.dto.ReviewRequest;
import com.apb.review.dto.SalonDTO;
import com.apb.review.dto.UserDTO;
import com.apb.review.model.Review;

import java.util.List;

public interface ReviewService {
    Review createReview(ReviewRequest request, UserDTO user, SalonDTO salon);
    List<Review> getReviewsBySalonId(Long salonId);
    Review updateReview(ReviewRequest request, Long reviewId, Long userId) throws Exception;
    void deleteReview(Long reviewId, Long userId) throws Exception;
}
