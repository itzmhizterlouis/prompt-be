package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.Review;
import com.hsl.prompt_be.entities.requests.ReviewRequest;
import com.hsl.prompt_be.entities.responses.GenericResponse;
import com.hsl.prompt_be.exceptions.PrinthubException;
import com.hsl.prompt_be.exceptions.ReviewNotFoundException;
import com.hsl.prompt_be.exceptions.UnauthorizedException;
import com.hsl.prompt_be.repositories.ReviewRepository;
import com.hsl.prompt_be.utils.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderService orderService;

    public Review createReview(UUID printerId, ReviewRequest request) throws PrinthubException {

        orderService.throwErrorIfUserHasNoOrders(printerId);

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .printerId(printerId)
                .ownerId(UserUtil.getLoggedInUser().getUserId())
                .build();

        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByPrinterOrUser(UUID tag) {

        return reviewRepository.findAllByOwnerIdOrPrinterId(tag, tag); // tag can either be owner id or printer id
    }

    public Review getReviewById(UUID reviewId) throws ReviewNotFoundException {

        return reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
    }

    public Review updateReview(UUID reviewId, ReviewRequest request) throws ReviewNotFoundException {

        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review.setUpdatedAt(Instant.now());

        return reviewRepository.save(review);
    }

    @Transactional
    public GenericResponse deleteReview(UUID reviewId) throws PrinthubException {

        throwErrorIfNotUserReview(reviewId);

        reviewRepository.deleteById(reviewId);

        return GenericResponse.builder()
                .message("Review has been successfully deleted").build();
    }

    private void throwErrorIfNotUserReview(UUID reviewId) throws PrinthubException {

        if (!reviewRepository.existsByOwnerIdAndReviewId(UserUtil.getLoggedInUser().getUserId(), reviewId)) {
            throw new UnauthorizedException("you're not the owner of the review");
        }
    }
}
