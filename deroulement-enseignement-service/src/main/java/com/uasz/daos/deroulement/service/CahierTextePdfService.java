package com.uasz.daos.deroulement.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.uasz.daos.deroulement.model.NoteCahierTexte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CahierTextePdfService {

    @Autowired
    private NoteCahierTexteService noteCahierTexteService;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] genererPdfCahierTexte(Long enseignantId) {
        List<NoteCahierTexte> notes;
        if (enseignantId != null) {
            notes = noteCahierTexteService.getNotesByEnseignant(enseignantId)
                    .stream().filter(NoteCahierTexte::isEstValide).toList();
        } else {
            notes = noteCahierTexteService.getAllNotes()
                    .stream().filter(NoteCahierTexte::isEstValide).toList();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);

            // En-tête
            document.add(new Paragraph("UNIVERSITE ASSANE SECK DE ZIGUINCHOR")
                    .setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("CAHIER DE TEXTE")
                    .setBold().setFontSize(14).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Généré le: " + LocalDateTime.now().format(DATETIME_FORMATTER))
                    .setFontSize(10).setTextAlignment(TextAlignment.RIGHT));

            if (enseignantId != null) {
                document.add(new Paragraph("Enseignant ID: " + enseignantId).setFontSize(11));
            }

            // Tableau
            Table table = new Table(UnitValue.createPointArray(new float[]{80, 100, 150, 150}));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Séance")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Titre")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Contenu")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Objectifs")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            for (NoteCahierTexte note : notes) {
                table.addCell(new Cell().add(new Paragraph(note.getSeanceId() != null ? note.getSeanceId().toString() : "-")));
                table.addCell(new Cell().add(new Paragraph(note.getTitre())));
                table.addCell(new Cell().add(new Paragraph(note.getContenu())));
                table.addCell(new Cell().add(new Paragraph(note.getObjectifsPedagogiques() != null ? note.getObjectifsPedagogiques() : "-")));
            }
            document.add(table);

            // Zone de signatures (GDEP2-84)
            document.add(new Paragraph("\n\n"));
            Table signatures = new Table(2).setWidth(UnitValue.createPercentValue(100));
            signatures.addCell(new Cell().add(new Paragraph("Signature de l'Enseignant\n\n\n\nDate: ..../..../......."))
                    .setBorder(null).setTextAlignment(TextAlignment.CENTER));
            signatures.addCell(new Cell().add(new Paragraph("Visa du Chef de Département\n\n\n\nDate: ..../..../......."))
                    .setBorder(null).setTextAlignment(TextAlignment.CENTER));
            document.add(signatures);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }

        return baos.toByteArray();
    }
}
