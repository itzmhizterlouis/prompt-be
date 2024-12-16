package com.hsl.prompt_be.utils;

import com.hsl.prompt_be.entities.models.User;
import com.hsl.prompt_be.exceptions.PrinthubException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {

    public static User getLoggedInUser() throws PrinthubException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {

            Object principal = authentication.getPrincipal();
            return ((User) principal);
        }
        else {
            throw new PrinthubException("Error getting logged in user");
        }
    }
}
