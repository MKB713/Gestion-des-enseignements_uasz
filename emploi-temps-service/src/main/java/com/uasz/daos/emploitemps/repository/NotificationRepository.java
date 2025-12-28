package com.uasz.daos.emploitemps.repository;

import com.uasz.daos.emploitemps.model.Notification;
import com.uasz.daos.emploitemps.model.TypeDestinataire;
import com.uasz.daos.emploitemps.model.TypeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Trouve toutes les notifications d'un destinataire
     */
    List<Notification> findByDestinataireIdAndTypeDestinataireOrderByDateEnvoiDesc(
            Long destinataireId, TypeDestinataire typeDestinataire);

    /**
     * Trouve les notifications non lues d'un destinataire
     */
    List<Notification> findByDestinataireIdAndTypeDestinataireAndLuOrderByDateEnvoiDesc(
            Long destinataireId, TypeDestinataire typeDestinataire, Boolean lu);

    /**
     * Compte les notifications non lues
     */
    long countByDestinataireIdAndTypeDestinataireAndLu(
            Long destinataireId, TypeDestinataire typeDestinataire, Boolean lu);

    /**
     * Trouve les notifications par type
     */
    List<Notification> findByTypeNotificationOrderByDateEnvoiDesc(TypeNotification type);

    /**
     * Trouve les notifications d'une séance
     */
    List<Notification> findBySeanceIdOrderByDateEnvoiDesc(Long seanceId);

    /**
     * Trouve les notifications non envoyées par email
     */
    List<Notification> findByEmailEnvoyeOrderByDateEnvoiAsc(Boolean emailEnvoye);

    /**
     * Trouve les notifications entre deux dates
     */
    List<Notification> findByDateEnvoiBetweenOrderByDateEnvoiDesc(
            LocalDateTime dateDebut, LocalDateTime dateFin);

    /**
     * Supprime les anciennes notifications (cleanup)
     */
    @Query("DELETE FROM Notification n WHERE n.dateEnvoi < :dateLimit AND n.lu = true")
    void supprimerNotificationsAnciennesLues(@Param("dateLimit") LocalDateTime dateLimit);
}