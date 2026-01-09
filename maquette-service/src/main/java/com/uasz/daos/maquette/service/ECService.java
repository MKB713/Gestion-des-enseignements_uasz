package com.uasz.daos.maquette.service;

import com.uasz.daos.maquette.model.EC;
import com.uasz.daos.maquette.repository.ECRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ECService {

    @Autowired
    private ECRepository ecRepository;

    public List<EC> getAllECs() {
        return ecRepository.findByArchive(false);
    }

    public List<EC> getArchivedECs() {
        return ecRepository.findByArchive(true);
    }

    public EC getECById(long id) {
        return ecRepository.findById(id).orElse(null);
    }

    @Transactional
    public EC addEC(EC ec) {
        ec.setArchive(false);
        ec.setActif(true);
        return ecRepository.save(ec);
    }

    @Transactional
    public EC updateEC(long id, EC ecDetails) {
        EC ec = getECById(id);
        if (ec != null) {
            ec.setCode(ecDetails.getCode());
            ec.setLibelle(ecDetails.getLibelle());
            ec.setCm(ecDetails.getCm());
            ec.setTd(ecDetails.getTd());
            ec.setTp(ecDetails.getTp());
            ec.setTpe(ecDetails.getTpe());
            ec.setCoefficient(ecDetails.getCoefficient());
            return ecRepository.save(ec);
        }
        return null;
    }

    @Transactional
    public void activateEC(long id) {
        EC ec = getECById(id);
        if (ec != null) {
            ec.setActif(true);
            ecRepository.save(ec);
        }
    }

    @Transactional
    public void deactivateEC(long id) {
        EC ec = getECById(id);
        if (ec != null) {
            ec.setActif(false);
            ecRepository.save(ec);
        }
    }

    @Transactional
    public void archiveEC(long id) {
        EC ec = getECById(id);
        if (ec != null) {
            ec.setArchive(true);
            ec.setActif(false);
            ecRepository.save(ec);
        }
    }

    @Transactional
    public void unarchiveEC(long id) {
        EC ec = getECById(id);
        if (ec != null) {
            ec.setArchive(false);
            ec.setActif(true);
            ecRepository.save(ec);
        }
    }
}
