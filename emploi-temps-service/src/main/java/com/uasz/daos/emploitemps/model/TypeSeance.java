package com.uasz.daos.emploitemps.model;

/**
 * Type de séance pour classification et colorisation dans l'emploi du temps
 */
public enum TypeSeance {
    CM("Cours Magistral", "#3B82F6"),      // Bleu
    TD("Travaux Dirigés", "#10B981"),       // Vert
    TP("Travaux Pratiques", "#F59E0B"),     // Orange
    COURS("Cours", "#8B5CF6"),              // Violet
    EXAMEN("Examen", "#EF4444"),            // Rouge
    CONTROLE("Contrôle Continu", "#EC4899"), // Rose
    SOUTENANCE("Soutenance", "#14B8A6"),    // Teal
    CONFERENCE("Conférence", "#6366F1");    // Indigo

    private final String libelle;
    private final String couleur; // Code couleur hexadécimal pour affichage

    TypeSeance(String libelle, String couleur) {
        this.libelle = libelle;
        this.couleur = couleur;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getCouleur() {
        return couleur;
    }
}