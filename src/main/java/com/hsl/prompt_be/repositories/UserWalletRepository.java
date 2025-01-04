package com.hsl.prompt_be.repositories;

import com.hsl.prompt_be.entities.models.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserWalletRepository extends JpaRepository<UserWallet, UUID> {

}
