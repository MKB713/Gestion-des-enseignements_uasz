package com.uasz.daos.auth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;

/**
 * Configuration pour le service mail
 * Fournit un JavaMailSender mock pour le développement
 */
@Configuration
public class MailConfig {

    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender() {
        return new JavaMailSender() {
            @Override
            public MimeMessage createMimeMessage() {
                return null;
            }

            @Override
            public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
                return null;
            }

            @Override
            public void send(MimeMessage mimeMessage) throws MailException {
                System.out.println("📧 [MOCK MAIL] Email HTML non envoyé (mode développement)");
            }

            @Override
            public void send(MimeMessage... mimeMessages) throws MailException {
                System.out.println("📧 [MOCK MAIL] " + mimeMessages.length + " emails HTML non envoyés (mode développement)");
            }

            @Override
            public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
                System.out.println("📧 [MOCK MAIL] Email HTML non envoyé (mode développement)");
            }

            @Override
            public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
                System.out.println("📧 [MOCK MAIL] " + mimeMessagePreparators.length + " emails HTML non envoyés (mode développement)");
            }

            @Override
            public void send(SimpleMailMessage simpleMessage) throws MailException {
                System.out.println("📧 [MOCK MAIL] Email simulé :");
                System.out.println("   De      : " + (simpleMessage.getFrom() != null ? simpleMessage.getFrom() : "noreply@uasz.sn"));
                System.out.println("   À       : " + String.join(", ", simpleMessage.getTo()));
                System.out.println("   Sujet   : " + simpleMessage.getSubject());
                System.out.println("   Message : " + simpleMessage.getText());
                System.out.println("   ✅ Email logué avec succès (non envoyé réellement)");
            }

            @Override
            public void send(SimpleMailMessage... simpleMessages) throws MailException {
                for (SimpleMailMessage message : simpleMessages) {
                    send(message);
                }
            }
        };
    }
}