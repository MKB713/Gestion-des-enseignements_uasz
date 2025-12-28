package com.uasz.daos.emploitemps.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.uasz.daos.emploitemps.model.Seance;
import com.uasz.daos.emploitemps.model.TypeSeance;
import com.uasz.daos.emploitemps.repository.SeanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Service pour générer des emplois du temps en PDF
 * User Story: "Éditer et imprimer l'emploi du temps en PDF"
 */
@Service
public class PdfExportService {

    @Autowired
    private SeanceRepository seanceRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String[] JOURS_SEMAINE = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};

    /**
     * Génère un PDF pour un emploi du temps hebdomadaire
     * Génération asynchrone pour les gros volumes
     */
    @Async
    public CompletableFuture<byte[]> genererPdfHebdomadaire(LocalDate dateDebut,
                                                            String typeFiltreParam,
                                                            Long filtreId) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // En-tête
            ajouterEntete(document, "EMPLOI DU TEMPS HEBDOMADAIRE", dateDebut);

            // Récupérer les séances de la semaine
            LocalDate dateFin = dateDebut.plusDays(5); // Lundi à Samedi
            List<Seance> seances = recupererSeances(dateDebut, dateFin, typeFiltreParam, filtreId);

            // Générer le tableau
            genererTableauHebdomadaire(document, seances, dateDebut);

            // Légende
            ajouterLegende(document);

            document.close();
            return CompletableFuture.completedFuture(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    /**
     * Génère un PDF pour un emploi du temps semestriel
     */
    @Async
    public CompletableFuture<byte[]> genererPdfSemestriel(LocalDate dateDebut,
                                                          LocalDate dateFin,
                                                          String typeFiltreParam,
                                                          Long filtreId) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // En-tête
            ajouterEntete(document, "EMPLOI DU TEMPS SEMESTRIEL", dateDebut);

            // Récupérer les séances du semestre
            List<Seance> seances = recupererSeances(dateDebut, dateFin, typeFiltreParam, filtreId);

            // Grouper par mois
            Map<String, List<Seance>> seancesParMois = seances.stream()
                    .collect(Collectors.groupingBy(s ->
                            s.getDateSeance().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH))));

            // Générer un tableau par mois
            for (Map.Entry<String, List<Seance>> entry : seancesParMois.entrySet()) {
                document.add(new Paragraph(entry.getKey().toUpperCase())
                        .setFontSize(14)
                        .setBold()
                        .setMarginTop(20));
                genererTableauMensuel(document, entry.getValue());
            }

            // Légende
            ajouterLegende(document);

            document.close();
            return CompletableFuture.completedFuture(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    /**
     * Génère un PDF pour une salle
     */
    @Async
    public CompletableFuture<byte[]> genererPdfSalle(Long salleId,
                                                     LocalDate dateDebut,
                                                     LocalDate dateFin) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Récupérer les séances de la salle
            List<Seance> seances = seanceRepository.findBySalleIdAndDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(
                    salleId, dateDebut, dateFin);

            String salleName = seances.isEmpty() ? "Salle" : seances.get(0).getSalle().getLibelle();

            // En-tête
            ajouterEntete(document, "PLANNING SALLE - " + salleName, dateDebut);

            // Tableau chronologique
            genererTableauSalle(document, seances);

            document.close();
            return CompletableFuture.completedFuture(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    // ========== MÉTHODES PRIVÉES ==========

    private void ajouterEntete(Document document, String titre, LocalDate date) {
        // Logo/Titre université
        document.add(new Paragraph("UNIVERSITÉ ASSANE SECK DE ZIGUINCHOR")
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5));

        document.add(new Paragraph("UFR Sciences et Technologies")
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Titre du document
        document.add(new Paragraph(titre)
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10));

        // Période
        document.add(new Paragraph("Période: à partir du " + date.format(DATE_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));
    }

    private void genererTableauHebdomadaire(Document document, List<Seance> seances, LocalDate dateDebut) {
        // Créer tableau avec colonnes: Heure | Lundi | Mardi | ... | Samedi
        float[] columnWidths = {1.5f, 2, 2, 2, 2, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // En-tête
        table.addHeaderCell(creerCelluleEntete("Horaire"));
        for (int i = 0; i < 6; i++) {
            LocalDate jour = dateDebut.plusDays(i);
            String headerText = JOURS_SEMAINE[i] + "\n" + jour.format(DATE_FORMATTER);
            table.addHeaderCell(creerCelluleEntete(headerText));
        }

        // Grouper séances par jour et heure
        Map<String, Map<LocalDate, List<Seance>>> seancesParCreneauEtJour = grouperSeancesParCreneauEtJour(seances);

        // Créneaux horaires (8h-18h)
        List<String> creneaux = genererCreneaux();

        for (String creneau : creneaux) {
            table.addCell(creerCellule(creneau));

            Map<LocalDate, List<Seance>> seancesDuCreneau = seancesParCreneauEtJour.getOrDefault(creneau, new HashMap<>());

            for (int i = 0; i < 6; i++) {
                LocalDate jour = dateDebut.plusDays(i);
                List<Seance> seancesDuJour = seancesDuCreneau.getOrDefault(jour, Collections.emptyList());

                if (seancesDuJour.isEmpty()) {
                    table.addCell(creerCellule(""));
                } else {
                    table.addCell(creerCelluleSeance(seancesDuJour.get(0)));
                }
            }
        }

        document.add(table);
    }

    private void genererTableauMensuel(Document document, List<Seance> seances) {
        float[] columnWidths = {2, 2, 2, 3, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // En-tête
        table.addHeaderCell(creerCelluleEntete("Date"));
        table.addHeaderCell(creerCelluleEntete("Horaire"));
        table.addHeaderCell(creerCelluleEntete("Type"));
        table.addHeaderCell(creerCelluleEntete("Matière"));
        table.addHeaderCell(creerCelluleEntete("Salle"));

        // Trier par date et heure
        seances.sort(Comparator.comparing(Seance::getDateSeance)
                .thenComparing(Seance::getHeureDebut));

        for (Seance seance : seances) {
            table.addCell(creerCellule(seance.getDateSeance().format(DATE_FORMATTER)));
            table.addCell(creerCellule(seance.getHeureDebut().format(TIME_FORMATTER) + "-" +
                    seance.getHeureFin().format(TIME_FORMATTER)));
            table.addCell(creerCelluleType(seance.getTypeSeance()));
            table.addCell(creerCellule("EC " + seance.getEcId())); // TODO: Récupérer nom EC
            table.addCell(creerCellule(seance.getSalle() != null ? seance.getSalle().getLibelle() : "N/A"));
        }

        document.add(table);
    }

    private void genererTableauSalle(Document document, List<Seance> seances) {
        float[] columnWidths = {2, 2, 2, 3, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // En-tête
        table.addHeaderCell(creerCelluleEntete("Date"));
        table.addHeaderCell(creerCelluleEntete("Horaire"));
        table.addHeaderCell(creerCelluleEntete("Type"));
        table.addHeaderCell(creerCelluleEntete("Matière"));
        table.addHeaderCell(creerCelluleEntete("Enseignant"));

        for (Seance seance : seances) {
            table.addCell(creerCellule(seance.getDateSeance().format(DATE_FORMATTER)));
            table.addCell(creerCellule(seance.getHeureDebut().format(TIME_FORMATTER) + "-" +
                    seance.getHeureFin().format(TIME_FORMATTER)));
            table.addCell(creerCelluleType(seance.getTypeSeance()));
            table.addCell(creerCellule("EC " + seance.getEcId()));
            table.addCell(creerCellule("ENS " + seance.getEnseignantId()));
        }

        document.add(table);
    }

    private void ajouterLegende(Document document) {
        document.add(new Paragraph("\nLégende:")
                .setFontSize(10)
                .setBold()
                .setMarginTop(20));

        Table legendeTable = new Table(4);
        legendeTable.setWidth(UnitValue.createPercentValue(60));

        for (TypeSeance type : TypeSeance.values()) {
            Cell cell = new Cell()
                    .add(new Paragraph(type.name()))
                    .setBackgroundColor(hexToRgb(type.getCouleur()))
                    .setFontSize(8)
                    .setPadding(5);
            legendeTable.addCell(cell);
        }

        document.add(legendeTable);

        // Pied de page
        document.add(new Paragraph("\nDocument généré le " + LocalDate.now().format(DATE_FORMATTER))
                .setFontSize(8)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(20));
    }

    private Cell creerCelluleEntete(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setBackgroundColor(new DeviceRgb(52, 73, 94))
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(9)
                .setPadding(5);
    }

    private Cell creerCellule(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setFontSize(8)
                .setPadding(3);
    }

    private Cell creerCelluleSeance(Seance seance) {
        String text = String.format("%s\nEC %d\n%s-%s",
                seance.getTypeSeance(),
                seance.getEcId(),
                seance.getHeureDebut().format(TIME_FORMATTER),
                seance.getHeureFin().format(TIME_FORMATTER));

        return new Cell()
                .add(new Paragraph(text))
                .setBackgroundColor(hexToRgb(seance.getTypeSeance().getCouleur()))
                .setFontSize(7)
                .setPadding(3);
    }

    private Cell creerCelluleType(TypeSeance type) {
        return new Cell()
                .add(new Paragraph(type.name()))
                .setBackgroundColor(hexToRgb(type.getCouleur()))
                .setFontSize(8)
                .setPadding(3);
    }

    private DeviceRgb hexToRgb(String hex) {
        hex = hex.replace("#", "");
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new DeviceRgb(r, g, b);
    }

    private List<Seance> recupererSeances(LocalDate dateDebut, LocalDate dateFin,
                                          String typeFiltreParam, Long filtreId) {
        if (typeFiltreParam == null || filtreId == null) {
            return seanceRepository.findByDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(dateDebut, dateFin);
        }

        switch (typeFiltreParam.toLowerCase()) {
            case "classe":
                return seanceRepository.findByClasseIdAndDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(
                        filtreId, dateDebut, dateFin);
            case "enseignant":
                return seanceRepository.findByEnseignantIdAndDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(
                        filtreId, dateDebut, dateFin);
            case "salle":
                return seanceRepository.findBySalleIdAndDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(
                        filtreId, dateDebut, dateFin);
            default:
                return seanceRepository.findByDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(dateDebut, dateFin);
        }
    }

    private Map<String, Map<LocalDate, List<Seance>>> grouperSeancesParCreneauEtJour(List<Seance> seances) {
        Map<String, Map<LocalDate, List<Seance>>> result = new HashMap<>();

        for (Seance seance : seances) {
            String creneau = getCreneau(seance.getHeureDebut());
            result.computeIfAbsent(creneau, k -> new HashMap<>())
                    .computeIfAbsent(seance.getDateSeance(), k -> new ArrayList<>())
                    .add(seance);
        }

        return result;
    }

    private List<String> genererCreneaux() {
        List<String> creneaux = new ArrayList<>();
        for (int h = 8; h < 18; h += 2) {
            creneaux.add(String.format("%02d:00-%02d:00", h, h + 2));
        }
        return creneaux;
    }

    private String getCreneau(LocalTime heure) {
        int h = heure.getHour();
        int debutCreneau = (h / 2) * 2;
        return String.format("%02d:00-%02d:00", debutCreneau, debutCreneau + 2);
    }
}