package com.uasz.daos.deroulement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.uasz.daos.deroulement.dto.NoteCahierTexteDTO;
import com.uasz.daos.deroulement.model.NoteCahierTexte;
import com.uasz.daos.deroulement.repository.HistoriqueModificationNoteRepository;
import com.uasz.daos.deroulement.repository.NoteCahierTexteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class NoteCahierTexteServiceTest {

    @Mock
    private NoteCahierTexteRepository noteRepository;

    @Mock
    private HistoriqueModificationNoteRepository historiqueRepository;

    @InjectMocks
    private NoteCahierTexteService noteService;

    private NoteCahierTexte noteInvalide;
    private NoteCahierTexte noteValide;

    @BeforeEach
    void setUp() {
        noteInvalide = new NoteCahierTexte();
        noteInvalide.setId(1L);
        noteInvalide.setEstValide(false);
        noteInvalide.setTitre("Titre Original");

        noteValide = new NoteCahierTexte();
        noteValide.setId(2L);
        noteValide.setEstValide(true);
        noteValide.setTitre("Titre Validé");
    }

    @Test
    @SuppressWarnings("null")
    void testModifierNoteNonValidee_Succes() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(noteInvalide));
        when(noteRepository.save(any())).thenReturn(noteInvalide);

        NoteCahierTexteDTO dto = new NoteCahierTexteDTO();
        dto.setTitre("Nouveau Titre");

        NoteCahierTexte result = noteService.modifierNote(1L, dto);

        assertNotNull(result);
        assertEquals("Nouveau Titre", result.getTitre());
        verify(noteRepository).save(any());
        verify(historiqueRepository, atLeastOnce()).save(any());
    }

    @Test
    @SuppressWarnings("null")
    void testModifierNoteValidee_Echec() {
        when(noteRepository.findById(2L)).thenReturn(Optional.of(noteValide));

        NoteCahierTexteDTO dto = new NoteCahierTexteDTO();
        dto.setTitre("Tentative Modification");

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            noteService.modifierNote(2L, dto);
        });

        assertEquals("Cette note a été validée et ne peut plus être modifiée.", exception.getMessage());
        verify(noteRepository, never()).save(any());
    }
}
