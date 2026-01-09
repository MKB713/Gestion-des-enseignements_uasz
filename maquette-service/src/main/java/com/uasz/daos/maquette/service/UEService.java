package com.uasz.daos.maquette.service;

import com.uasz.daos.maquette.model.UE;
import com.uasz.daos.maquette.repository.UERepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class UEService {

    @Autowired
    private UERepository ueRepository;

    public List<UE> getAllUEs() {
        return ueRepository.findByArchive(false);
    }

    public List<UE> getArchivedUEs() {
        return ueRepository.findByArchive(true);
    }

    public UE getUEById(long id) {
        return ueRepository.findById(id).orElse(null);
    }

    @Transactional
    public UE saveUE(UE ue) {
        if (ue.getId() == null) {
            ue.setDateCreation(new Date());
            ue.setActive(true);
            ue.setArchive(false);
        }
        return ueRepository.save(ue);
    }

    @Transactional
    public void activer(long id) {
        UE ue = getUEById(id);
        if (ue != null) {
            ue.setActive(true);
            ueRepository.save(ue);
        }
    }

    @Transactional
    public void desactiver(long id) {
        UE ue = getUEById(id);
        if (ue != null) {
            ue.setActive(false);
            ueRepository.save(ue);
        }
    }

    @Transactional
    public void archiver(long id) {
        UE ue = getUEById(id);
        if (ue != null) {
            ue.setArchive(true);
            ue.setActive(false);
            ueRepository.save(ue);
        }
    }

    @Transactional
    public void restaurer(long id) {
        UE ue = getUEById(id);
        if (ue != null) {
            ue.setArchive(false);
            ue.setActive(true);
            ueRepository.save(ue);
        }
    }
}
