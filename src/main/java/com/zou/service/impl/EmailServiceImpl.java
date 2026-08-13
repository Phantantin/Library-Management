package com.zou.service.impl;

import com.zou.service.EmailService;
import jakarta.mail.MessageRemovedException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {

        try {
            MimeMessage mineMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mineMessage, "utf-8");

            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setTo(to);
            javaMailSender.send(mineMessage);
        }catch (
                MailException e
        ){
            throw new MailSendException("Failed to send email");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
}
