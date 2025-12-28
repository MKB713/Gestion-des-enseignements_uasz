package com.uasz.daos.auth.dto;

import com.uasz.daos.auth.enums.Role;
import com.uasz.daos.auth.validation.MinimumAge;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String password; // CHANGÉ DE motDePasse À password

    @NotNull(message = "La date de naissance est obligatoire")
    @MinimumAge(value = 18, message = "L'utilisateur doit avoir au moins 18 ans")
    private LocalDate dateNaissance;

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;

    private String matricule;

    private String telephone;

    private String adresse;
}