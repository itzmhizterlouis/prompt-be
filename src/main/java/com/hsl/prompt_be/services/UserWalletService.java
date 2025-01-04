package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.UserWallet;
import com.hsl.prompt_be.exceptions.UserWalletNotFoundException;
import com.hsl.prompt_be.exceptions.PrinthubException;
import com.hsl.prompt_be.repositories.UserWalletRepository;
import com.hsl.prompt_be.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserWalletService {

    private final UserWalletRepository userWalletRepository;

    public UserWallet save(UserWallet userWallet) {

        return userWalletRepository.save(userWallet);
    }

    public UserWallet addAmountToPrinterWallet(UUID userId, int amount) throws UserWalletNotFoundException {

        UserWallet userWallet = userWalletRepository.findById(userId).orElseThrow(UserWalletNotFoundException::new);
        userWallet.setBalance(userWallet.getBalance() + amount);
        userWallet.setUpdatedAt(Instant.now());

        return userWalletRepository.save(userWallet);
    }

    public UserWallet getLoggedInUserWallet() throws PrinthubException {

        return userWalletRepository.findById(UserUtil.getLoggedInUser().getUserId()).get();
    }
}
