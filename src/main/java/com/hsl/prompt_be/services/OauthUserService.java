package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.OauthUser;
import com.hsl.prompt_be.exceptions.UserNotFoundException;
import com.hsl.prompt_be.repositories.OauthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OauthUserService {

    private final OauthUserRepository oauthUserRepository;

    public OauthUser findByEmail(String email) throws UserNotFoundException {
        return oauthUserRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    public void save(OauthUser oauthUser) {
        oauthUserRepository.save(oauthUser);
    }

    public boolean existsByEmail(String email) {
        return oauthUserRepository.existsByEmail(email);
    }
}
