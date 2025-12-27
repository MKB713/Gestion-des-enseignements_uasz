package com.uasz.daos.auth.enums;

public enum Role {
    ADMIN("Administrateur système", 100),
    CHEF_DE_DEPARTEMENT("Chef de département", 80),
    RESPONSABLE_MASTER("Responsable de master", 70),
    COORDONATEUR_DES_LICENCES("Coordonateur des licences", 60),
    ENSEIGNANT("Enseignant", 50),
    ETUDIANT("Étudiant", 10);

    private final String libelle;
    private final int niveau;

    Role(String libelle, int niveau) {
        this.libelle = libelle;
        this.niveau = niveau;
    }

    public String getLibelle() {
        return libelle;
    }

    public int getNiveau() {
        return niveau;
    }

    public boolean hasHigherOrEqualAuthority(Role other) {
        return this.niveau >= other.niveau;
    }

    public boolean hasHigherAuthority(Role other) {
        return this.niveau > other.niveau;
    }

    public boolean isAdmin() {
        return this == ADMIN || this == CHEF_DE_DEPARTEMENT;
    }
}