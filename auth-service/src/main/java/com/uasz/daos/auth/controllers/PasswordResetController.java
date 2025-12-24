package com.uasz.daos.auth.controllers;

import com.uasz.daos.auth.services.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // Page de demande de réinitialisation
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    // Traitement de la demande
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        passwordResetService.requestPasswordReset(email, ipAddress, userAgent);

        // Pour des raisons de sécurité, on affiche toujours le même message
        redirectAttributes.addFlashAttribute("success",
                "Si votre email est enregistré dans notre système, vous recevrez un lien de réinitialisation.");

        return "redirect:/forgot-password";
    }

    // Page de réinitialisation avec token
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        boolean isValid = passwordResetService.validateToken(token);

        if (!isValid) {
            model.addAttribute("error", "Le lien de réinitialisation est invalide ou a expiré.");
            return "reset-password-error";
        }

        model.addAttribute("token", token);
        model.addAttribute("resetForm", new ResetPasswordForm());
        return "reset-password";
    }

    // Traitement de la réinitialisation
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                       @Valid @ModelAttribute("resetForm") ResetPasswordForm form,
                                       BindingResult bindingResult,
                                       HttpServletRequest request,
                                       Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("token", token);
            return "reset-password";
        }

        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("error", "Les mots de passe ne correspondent pas.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        boolean success = passwordResetService.resetPassword(
                token, form.getNewPassword(), form.getConfirmPassword(), ipAddress, userAgent);

        if (success) {
            return "reset-password-success";
        } else {
            model.addAttribute("error", "La réinitialisation a échoué. Le lien a peut-être expiré.");
            return "reset-password-error";
        }
    }

    // API REST pour la réinitialisation
    @PostMapping("/api/auth/reset-password")
    @ResponseBody
    public ResponseEntity<?> apiResetPassword(@Valid @RequestBody ResetPasswordRequest request,
                                              HttpServletRequest httpRequest) {
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        boolean success = passwordResetService.resetPassword(
                request.getToken(), request.getNewPassword(), request.getConfirmPassword(),
                ipAddress, userAgent);

        if (success) {
            return ResponseEntity.ok().body("Mot de passe réinitialisé avec succès");
        } else {
            return ResponseEntity.badRequest().body("Échec de la réinitialisation");
        }
    }

    @PostMapping("/api/auth/forgot-password")
    @ResponseBody
    public ResponseEntity<?> apiForgotPassword(@RequestParam String email,
                                               HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        passwordResetService.requestPasswordReset(email, ipAddress, userAgent);

        // Toujours retourner le même message pour des raisons de sécurité
        return ResponseEntity.ok().body(
                "Si votre email est enregistré, vous recevrez un lien de réinitialisation.");
    }

    // Méthodes d'aide
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    // Classes DTO internes
    @Data
    public static class ResetPasswordForm {
        private String newPassword;
        private String confirmPassword;
    }

    @Data
    public static class ResetPasswordRequest {
        private String token;
        private String newPassword;
        private String confirmPassword;
    }
}