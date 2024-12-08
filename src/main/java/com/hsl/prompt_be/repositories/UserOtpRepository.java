package com.hsl.prompt_be.repositories;

import com.hsl.prompt_be.entities.models.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserOtpRepository extends JpaRepository<UserOtp, Integer> {

    Optional<UserOtp> findByUserIdAndOtp(UUID userId, int otp);
    void deleteByUserId(UUID userId);
}
