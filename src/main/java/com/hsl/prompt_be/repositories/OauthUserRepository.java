package com.hsl.prompt_be.repositories;

import com.hsl.prompt_be.entities.models.OauthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OauthUserRepository extends JpaRepository<OauthUser, UUID> {

    Optional<OauthUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
