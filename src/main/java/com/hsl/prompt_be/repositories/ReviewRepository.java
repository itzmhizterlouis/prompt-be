package com.hsl.prompt_be.repositories;

import com.hsl.prompt_be.entities.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findAllByOwnerIdOrPrinterId(UUID ownerId, UUID printerId);
    boolean existsByOwnerIdAndReviewId(UUID ownerId, UUID reviewId);
}
