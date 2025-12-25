package com.uasz.daos.maquette.service;

import com.uasz.daos.maquette.exception.MatriculeAlreadyExistsException;
import com.uasz.daos.maquette.model.Classe;
import com.uasz.daos.maquette.repository.ClasseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClasseService {
    @Autowired
    private ClasseRepository classeRepository;

    public List<Classe> listerTout() {
        return classeRepository.findAll();
    }

    public Classe ajouterClasse(Classe classe) {
        if (classeRepository.existsByNom(classe.getNom())) {
            throw new MatriculeAlreadyExistsException("Une classe avec ce nom existe déjà : " + classe.getNom());
        }
        classe.setDateCreation(new Date());
        return classeRepository.save(classe);
    }

    public Classe modifierClasse(Long id, Classe classeModifiee) {
        return classeRepository.findById(id).map(classe -> {
            if (!classe.getNom().equals(classeModifiee.getNom())
                    && classeRepository.existsByNom(classeModifiee.getNom())) {
                throw new MatriculeAlreadyExistsException(
                        "Une classe avec ce nom existe déjà : " + classeModifiee.getNom());
            }
            classe.setNom(classeModifiee.getNom());
            classe.setSemestre(classeModifiee.getSemestre());
            classe.setDescription(classeModifiee.getDescription());
            classe.setFormation(classeModifiee.getFormation());
            return classeRepository.save(classe);
        }).orElseThrow(() -> new RuntimeException("Classe non trouvée avec l'ID : " + id));
    }

    public void supprimerClasse(Long id) {
        if (!classeRepository.existsById(id)) {
            throw new RuntimeException("Classe non trouvée !");
        }
        classeRepository.deleteById(id);
    }

    public Classe detailsClasse(Long id) {
        return classeRepository.findById(id).orElse(null);
    }
}
