package com.nexushr.performance.repository;

import com.nexushr.performance.entity.PerformanceRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PerformanceRatingRepository extends JpaRepository<PerformanceRating, UUID> {
    List<PerformanceRating> findByReviewId(UUID reviewId);
}
