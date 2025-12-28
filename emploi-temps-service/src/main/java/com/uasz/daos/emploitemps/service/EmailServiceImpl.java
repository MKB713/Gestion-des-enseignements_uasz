package com.uasz.daos.emploitemps.service;

import com.uasz.daos.emploitemps.model.Notification;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendEmail(Notification notification) {
        // Placeholder for actual email sending logic
        // For now, just log the email content
        System.out.println("--- SIMULATING EMAIL SENDING ---");
        System.out.println("To: Destinataire ID " + notification.getDestinataireId() + " (" + notification.getTypeDestinataire() + ")");
        System.out.println("Subject: " + notification.getTitre());
        System.out.println("Body: " + notification.getMessage());
        System.out.println("---------------------------------");
    }
}
