package com.hsl.prompt_be.configurations;

import com.hsl.prompt_be.entities.models.OauthUser;
import com.hsl.prompt_be.entities.requests.UserRequest;
import com.hsl.prompt_be.services.AuthenticationService;
import com.hsl.prompt_be.services.OauthUserService;
import com.hsl.prompt_be.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final OauthUserService oauthUserService;

    @Value("${application.base-url}")
    private String BASE_URL;

    @Value("${application.oauth-login}")
    private String OAUTH_LOGIN_ROUTE;

    @SneakyThrows
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = authToken.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name").toString().substring(0, 1).toUpperCase() + oAuth2User.getAttribute("given_name").toString().substring(1);
        String lastName = oAuth2User.getAttribute("family_name").toString().substring(0, 1).toUpperCase() + oAuth2User.getAttribute("family_name").toString().substring(1);

        UserRequest userRequest = UserRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(UUID.randomUUID().toString())
                .emailVerified(true)
                .build();

        if (!userService.existsByEmail(email)) {
            authenticationService.signUp(userRequest);
        }

        if (!oauthUserService.existsByEmail(email)) {
            OauthUser oauthUser = OauthUser.builder()
                    .email(email)
                    .build();
            oauthUserService.save(oauthUser);
        }

        System.out.println(BASE_URL);
        response.sendRedirect(BASE_URL + OAUTH_LOGIN_ROUTE + "/" + email);
    }
}
