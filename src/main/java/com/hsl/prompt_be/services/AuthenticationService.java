package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.Printer;
import com.hsl.prompt_be.entities.models.PrinterWallet;
import com.hsl.prompt_be.entities.models.User;
import com.hsl.prompt_be.entities.models.UserOtp;
import com.hsl.prompt_be.entities.requests.EmailDetails;
import com.hsl.prompt_be.entities.requests.LoginRequest;
import com.hsl.prompt_be.entities.requests.OtpRequest;
import com.hsl.prompt_be.entities.requests.PrinterRequest;
import com.hsl.prompt_be.entities.requests.UserRequest;
import com.hsl.prompt_be.entities.responses.GenericResponse;
import com.hsl.prompt_be.entities.responses.LoginResponse;
import com.hsl.prompt_be.entities.responses.UserResponse;
import com.hsl.prompt_be.exceptions.OtpNotFoundException;
import com.hsl.prompt_be.exceptions.PrinthubException;
import com.hsl.prompt_be.exceptions.UserNotFoundException;
import com.hsl.prompt_be.repositories.PrinterRepository;
import com.hsl.prompt_be.repositories.UserOtpRepository;
import com.hsl.prompt_be.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final UserOtpRepository userOtpRepository;
    private final PrinterRepository printerRepository;
    private final PrinterWalletService printerWalletService;
    private final PrinterService printerService;

    @Transactional
    public UserResponse signUp(UserRequest userRequest) {

        User user = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .build();

        userRepository.save(user).toDto();
        sendOtpToMail(generateOtp(), user.getEmail(), user.getUserId());

        return user.toDto();
    }

    public LoginResponse login(LoginRequest request) throws PrinthubException {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.generateToken(user);
        UUID printerId = null;

        if (user.isPrinter()) {

            printerId = printerService.getPrinterByUserId(user.getUserId()).get().getPrinterId();
        }

        return LoginResponse.builder()
                .token(token)
                .isPrinter(user.isPrinter())
                .isEmailVerified(user.isEmailVerified())
                .isEnabled(user.isEnabled())
                .userId(user.getUserId())
                .printerId(printerId)
                .build();
    }

    public UserResponse verifyOtp(OtpRequest request) throws OtpNotFoundException, UserNotFoundException {

        UserOtp userOtp = userOtpRepository.findByUserIdAndOtp(request.getUserId(), request.getOtp()).orElseThrow(OtpNotFoundException::new);
        throwErrorIfOtpIfExpired(userOtp);
        userOtpRepository.delete(userOtp);

        User user = userRepository.findByUserId(request.getUserId()).orElseThrow(UserNotFoundException::new);
        user.setEmailVerified(true);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        return user.toDto();
    }

    public UserResponse getLoggedInUser() throws PrinthubException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            return ((User) principal).toDto();
        }

        else {
            throw new PrinthubException("Error getting logged in user");
        }
    }

    @Transactional
    public Printer createPrinter(PrinterRequest request, UUID userId) throws UserNotFoundException {

        Printer printer = Printer.builder()
                .userId(userId)
                .name(request.getName().toUpperCase())
                .location(request.getLocation().toUpperCase())
                .description(request.getDescription())
                .colouredRate(request.getColouredRate())
                .uncolouredRate(request.getUncolouredRate())
                .bankName(request.getBankName())
                .accountName(request.getAccountName())
                .accountNumber(request.getAccountNumber())
                .weekdayClosing(request.getWeekdayClosing())
                .weekendOpening(request.getWeekendOpening())
                .weekdayOpening(request.getWeekdayOpening())
                .weekendClosing(request.getWeekendClosing())
                .build();

        PrinterWallet printerWallet = PrinterWallet.builder()
                .printerId(printer.getPrinterId()).build();

        User user = userRepository.findByUserId(userId).orElseThrow(UserNotFoundException::new);
        user.setPrinter(true);
        user.setUpdatedAt(Instant.now());

        printerRepository.save(printer);
        printerWalletService.save(printerWallet);
        userRepository.save(user);

        return printer;
    }

    @Transactional
    public GenericResponse sendOtpToMail(String email) throws PrinthubException {

        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        userOtpRepository.deleteByUserId(user.getUserId());
        sendOtpToMail(generateOtp(), user.getEmail(), user.getUserId());

        return GenericResponse.builder()
                .message("Otp has been sent to your mail successfully").build();
    }

    public int generateOtp() {

        Random random = new Random();

        return random.nextInt((1000000 - 100000) + 1) + 100000;
    }

    private void sendOtpToMail(int otp, String recipientEmail, UUID userId) {

        emailService.sendSimpleMail(
                EmailDetails.builder()
                        .messageBody("Your OTP for PRINTHUB is " + otp)
                        .subject("Your OTP for PRINTHUB")
                        .recipient(recipientEmail)
                        .build()
        );

        userOtpRepository.save(
                UserOtp.builder()
                        .otp(otp)
                        .userId(userId)
                        .build()
        );
    }

    @Transactional
    protected void throwErrorIfOtpIfExpired(UserOtp userOtp) throws OtpNotFoundException {

        Instant now = Instant.now();

        // Check if the given Instant is 15 minutes later than the current time
        if (userOtp.getCreatedAt().isAfter(now.plus(Duration.ofMinutes(15)))) {

            userOtpRepository.delete(userOtp);
            throw new OtpNotFoundException();
        }
    }
}
