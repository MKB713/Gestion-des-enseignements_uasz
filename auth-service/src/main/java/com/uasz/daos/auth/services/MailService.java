package com.uasz.daos.auth.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom("noreply@uasz.sn");

            mailSender.send(message);
            System.out.println("✅ Email envoyé avec succès à : " + to);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email : " + e.getMessage());
            // En développement, on loggue mais on ne bloque pas
        }
    }

    public void sendWelcomeEmail(String email, String nom, String prenom, String matricule, String password) {
        String subject = "Bienvenue sur DAOS - Vos identifiants";
        String text = String.format("""
            Bonjour %s %s,
            
            Votre compte a été créé avec succès sur la plateforme DAOS.
            
            Vos identifiants de connexion :
            • Email : %s
            • Matricule : %s
            • Mot de passe temporaire : %s
            
            Pour votre sécurité, veuillez changer votre mot de passe dès votre première connexion.
            
            Lien de connexion : http://localhost:8081/login
            
            Cordialement,
            L'équipe DAOS
            """, prenom, nom, email, matricule, password);

        sendMail(email, subject, text);
    }

    public void sendPasswordResetEmail(String email, String resetToken) {
        String subject = "Réinitialisation de votre mot de passe DAOS";
        String resetLink = "http://localhost:8081/reset-password?token=" + resetToken;
        String text = String.format("""
            Bonjour,
            
            Vous avez demandé la réinitialisation de votre mot de passe.
            
            Pour définir un nouveau mot de passe, cliquez sur le lien ci-dessous :
            %s
            
            Ce lien est valide pendant 1 heure.
            
            Si vous n'êtes pas à l'origine de cette demande, ignorez simplement cet email.
            
            Cordialement,
            L'équipe DAOS
            """, resetLink);

        sendMail(email, subject, text);
    }

    public void sendAccountLockedEmail(String email, String nom, String prenom) {
        String subject = "Alerte sécurité - Votre compte DAOS a été verrouillé";
        String text = String.format("""
            Bonjour %s %s,
            
            Votre compte DAOS a été temporairement verrouillé après plusieurs tentatives de connexion échouées.
            
            Pour des raisons de sécurité, vous devez :
            1. Contacter l'administrateur pour déverrouiller votre compte
            2. Réinitialiser votre mot de passe
            
            Si vous êtes à l'origine de ces tentatives, veuillez vérifier vos identifiants.
            Sinon, veuillez nous contacter immédiatement.
            
            Cordialement,
            L'équipe sécurité DAOS
            """, prenom, nom);

        sendMail(email, subject, text);
    }

    public void sendPasswordChangedEmail(String email, String nom, String prenom) {
        String subject = "Confirmation - Votre mot de passe DAOS a été modifié";
        String text = String.format("""
            Bonjour %s %s,
            
            Votre mot de passe DAOS a été modifié avec succès.
            
            Si vous n'êtes pas à l'origine de cette modification, veuillez :
            1. Réinitialiser immédiatement votre mot de passe
            2. Contacter l'administrateur
            3. Vérifier l'activité récente sur votre compte
            
            Date de modification : %s
            
            Cordialement,
            L'équipe sécurité DAOS
            """, prenom, nom, java.time.LocalDateTime.now());

        sendMail(email, subject, text);
    }
}