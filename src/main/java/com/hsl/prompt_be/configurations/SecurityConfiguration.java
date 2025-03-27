package com.hsl.prompt_be.configurations;

import com.hsl.prompt_be.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000", "https://localhost:3000",
                "https://prompt-2-0-preview.vercel.app", "http://prompt-2-0-preview.vercel.app",
                "https://printhub.ng", "http://printhub.ng"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, OAuth2LoginFailureHandler failureHandler,
            OAuth2LoginSuccessHandler successHandler
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF
                .cors(httpSecurityCorsConfigurer -> corsConfigurationSource())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/authentication/signup", "/authentication/login", "/authentication/generate-otp/**",
                                "/authentication/verify-otp", "/authentication/{userId}/printers",
                                "/swagger-ui/**", "/v3/api-docs/**", "/orders/payments/confirm",
                                "/printers/cron/open_status/**", "/user_wallet/webhook", "/authentication/oauth-login/**"
                        ).permitAll() // Allow access to "/auth/**"

                        .requestMatchers(
                                HttpMethod.GET,
                                // printers endpoints should be non protected
                                "/printers/**"
                        ).permitAll()
                        .anyRequest().authenticated() // Require authentication for all other requests
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Set session policy to stateless
                )
                .oauth2Login(
                        oauth2 -> oauth2
                                .successHandler(successHandler)
                                .failureHandler(failureHandler)
                )
                .authenticationProvider(authenticationProvider) // Add custom authentication provider
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter before UsernamePasswordAuthenticationFilter

        return http.build();
    }
}
