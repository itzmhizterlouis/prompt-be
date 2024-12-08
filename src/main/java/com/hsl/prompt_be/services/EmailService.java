package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.requests.EmailDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("{spring.mail.username}")
    private String sender;

    public void sendSimpleMail(EmailDetails emailDetails){

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(sender);
        mailMessage.setTo(emailDetails.getRecipient());
        mailMessage.setText(emailDetails.getMessageBody());
        mailMessage.setSubject(emailDetails.getSubject());

        javaMailSender.send(mailMessage);
    }
}
