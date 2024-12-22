package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.User;
import com.hsl.prompt_be.exceptions.UserNotFoundException;
import com.hsl.prompt_be.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public User findByUserId(UUID userId) throws UserNotFoundException {

        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }
}
