-- =======================================================================================
-- DONNÉES DE DÉMONSTRATION - MAQUETTE L2I & MASTER 1 GL
-- =======================================================================================
-- Script multi-base de données. Les variables (@id_...) persistent durant la session.

-- ---------------------------------------------------------------------------------------
-- PARTIE 1 : MAQUETTES (Base : daos_maquette_db)
-- ---------------------------------------------------------------------------------------
USE daos_maquette_db;

-- 1. MAQUETTE : Maquette L2I - 2ième Année (Semestre 3 et 4)
-- =======================================================================================
INSERT INTO maquettes (actif, code, description, libelle, statut, version, date_creation, formation_id, maquette_parent_id)
VALUES (true, 'L2I-2', 'Licence 2 Informatique', 'Maquette L2I - 2ième Année', 'BROUILLON', 1, NOW(), NULL, NULL);

SET @id_maquette_l2i = LAST_INSERT_ID();

-- 1.1 SEMESTRE 3
INSERT INTO semestres (coefficients_totaux, credits_totaux, libelle, numero, maquette_id)
VALUES (15, 30, 'Semestre 3', 3, @id_maquette_l2i);
SET @id_sem_3 = LAST_INSERT_ID();

-- UE: Modelisation aléatoire
INSERT INTO ues (active, archive, code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, date_creation)
VALUES (true, false, 'INF231', 'Modelisation aléatoire(4)', 6, 3, 36, 18, 18, 120, @id_sem_3, NOW());
SET @id_ue_info231 = LAST_INSERT_ID();
    -- ECs
    INSERT INTO ecs (actif, archive, code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id, module_id)
    VALUES (true, false, 'INF2311', 'Probabilité', 18, 18, 0, 24, 60, 1, @id_ue_info231, NULL),
           (true, false, 'INF2312', 'Analyse de données', 18, 0, 18, 24, 60, 1, @id_ue_info231, NULL);

-- UE: Réseaux et Systèmes
INSERT INTO ues (active, archive, code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, date_creation)
VALUES (true, false, 'INF232', 'Réseaux et Systèmes(2)', 6, 3, 36, 20, 16, 120, @id_sem_3, NOW());
SET @id_ue_info232 = LAST_INSERT_ID();
    -- ECs
    INSERT INTO ecs (actif, archive, code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id, module_id)
    VALUES (true, false, 'INF2321', 'Principes des Systèmes d''exploitation', 18, 10, 8, 24, 60, 1, @id_ue_info232, NULL),
           (true, false, 'INF2322', 'Introduction aux Réseaux', 18, 10, 8, 24, 60, 1, @id_ue_info232, NULL);

-- UE: Conception des systèmes d'information
INSERT INTO ues (active, archive, code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, date_creation)
VALUES (true, false, 'INF233', 'Conception des systèmes d''information(3)', 8, 4, 42, 42, 12, 160, @id_sem_3, NOW());
SET @id_ue_info233 = LAST_INSERT_ID();
    -- ECs
    INSERT INTO ecs (actif, archive, code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id, module_id)
    VALUES (true, false, 'INF2331', 'Conception de BD Relationnelles', 18, 18, 12, 32, 80, 1, @id_ue_info233, NULL),
           (true, false, 'INF2332', 'Analyse et conception de systèmes', 24, 24, 0, 32, 80, 1, @id_ue_info233, NULL);

-- UE: Algorithmique et Programmation 3
INSERT INTO ues (active, archive, code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, date_creation)
VALUES (true, false, 'INF234', 'Algorithmique et Programmation 3 (1)', 8, 4, 42, 28, 26, 160, @id_sem_3, NOW());
SET @id_ue_info234 = LAST_INSERT_ID();
    -- ECs
    INSERT INTO ecs (actif, archive, code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id, module_id)
    VALUES (true, false, 'INF2341', 'Algorithmique & Structures de données', 24, 20, 16, 40, 100, 5, @id_ue_info234, NULL),
           (true, false, 'INF2351', 'Développement web Back-end', 18, 8, 10, 24, 60, 3, @id_ue_info234, NULL);

-- UE: Langues et Humanités 3
INSERT INTO ues (active, archive, code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, date_creation)
VALUES (true, false, 'INF235', 'Langues et Humanités 3()', 2, 1, 24, 0, 0, 40, @id_sem_3, NOW());
SET @id_ue_info235 = LAST_INSERT_ID();
    -- ECs
    INSERT INTO ecs (actif, archive, code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id, module_id)
    VALUES (true, false, 'INF2352', 'Techniques de communication', 12, 0, 0, 8, 20, 1, @id_ue_info235, NULL),
           (true, false, 'INF2342', 'Anglais 3', 12, 0, 0, 8, 20, 1, @id_ue_info235, NULL);


-- 1.2 SEMESTRE 4 
INSERT INTO semestres (coefficients_totaux, credits_totaux, libelle, numero, maquette_id)
VALUES (15, 30, 'Semestre 4', 4, @id_maquette_l2i);
SET @id_sem_4 = LAST_INSERT_ID();

INSERT INTO ues (active, archive, code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, date_creation)
VALUES (true, false, 'INF241', 'Programmation Web dynamique / orientée objet', 6, 3, 36, 12, 24, 120, @id_sem_4, NOW());
SET @id_ue_inf241 = LAST_INSERT_ID();
    -- ECS
    INSERT INTO ecs (actif, archive, code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id, module_id)
    VALUES (true, false, 'INF2411', 'Développement web front-end', 12, 0, 12, 16, 40, 1, @id_ue_inf241, NULL),
           (true, false, 'INF2412', 'Programmation Orientée Objet', 24, 12, 12, 32, 80, 2, @id_ue_inf241, NULL);

INSERT INTO ues (active, archive, code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, date_creation)
VALUES (true, false, 'INF242', 'Optimisation(4) = Combinatoire et Algorithmes', 6, 3, 42, 18, 12, 120, @id_sem_4, NOW());
SET @id_ue_inf242 = LAST_INSERT_ID();
    INSERT INTO ecs (actif, archive, code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id, module_id)
    VALUES (true, false, 'INF2421', 'Optimisation combinatoire', 24, 0, 12, 24, 60, 1, @id_ue_inf242, NULL),
           (true, false, 'INF2422', 'Complexité Algorithmique', 18, 18, 0, 24, 60, 1, @id_ue_inf242, NULL);

INSERT INTO ues (active, archive, code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, date_creation)
VALUES (true, false, 'INF243', 'Administration Systèmes Informatiques(2)', 8, 4, 48, 0, 48, 160, @id_sem_4, NOW());
SET @id_ue_inf243 = LAST_INSERT_ID();
    INSERT INTO ecs (actif, archive, code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id, module_id)
    VALUES (true, false, 'INF2431', 'Administration Systèmes', 24, 0, 24, 32, 80, 1, @id_ue_inf243, NULL),
           (true, false, 'INF2432', 'Administration BD', 24, 0, 24, 32, 80, 1, @id_ue_inf243, NULL);

INSERT INTO ues (active, archive, code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, date_creation)
VALUES (true, false, 'INF244', 'Réseaux et Services(3)', 6, 3, 44, 12, 16, 120, @id_sem_4, NOW());
SET @id_ue_inf244 = LAST_INSERT_ID();
    INSERT INTO ecs (actif, archive, code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id, module_id)
    VALUES (true, false, 'INF2441', 'Services Réseaux', 20, 0, 16, 24, 60, 1, @id_ue_inf244, NULL),
           (true, false, 'INF2442', 'Réseaux Locaux', 24, 12, 0, 24, 60, 1, @id_ue_inf244, NULL);

INSERT INTO ues (active, archive, code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, date_creation)
VALUES (true, false, 'INF245', 'Langues et Humanités 4(5)', 4, 2, 32, 10, 6, 80, @id_sem_4, NOW());
SET @id_ue_inf245 = LAST_INSERT_ID();
    INSERT INTO ecs (actif, archive, code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id, module_id)
    VALUES (true, false, 'INF2451', 'Anglais 4', 12, 0, 0, 8, 20, 1, @id_ue_inf245, NULL),
           (true, false, 'INF2452', 'Gestion de Projets Informatiques', 20, 10, 6, 24, 60, 3, @id_ue_inf245, NULL);


-- =======================================================================================
-- 2. MAQUETTE : MASTER 1 GÉNIE LOGICIEL / R&S (Pour la Répartition)
-- =======================================================================================
INSERT INTO maquettes (actif, code, description, libelle, statut, version, date_creation, formation_id, maquette_parent_id)
VALUES (true, 'M1-GLRS', 'Master 1 GL et R&S', 'Master 1 Génie Logiciel / R&S', 'BROUILLON', 1, NOW(), NULL, NULL);

SET @id_maquette_m1 = LAST_INSERT_ID();
-- Semestre 8 (Semestre 2 du Master 1)
INSERT INTO semestres (coefficients_totaux, credits_totaux, libelle, numero, maquette_id)
VALUES (30, 30, 'Semestre 2', 8, @id_maquette_m1);
SET @id_sem_m1_s2 = LAST_INSERT_ID();

-- 1. Admin BD
INSERT INTO ues (code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, active, date_creation)
VALUES ('M1GL01', 'Administration BD (GL)', 2, 2, 10, 0, 10, 20, @id_sem_m1_s2, true, NOW());
SET @id_ue_m1_01 = LAST_INSERT_ID();
    INSERT INTO ecs (code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id)
    VALUES ('M1GL011', 'Administration BD (GL)', 10, 0, 10, 0, 20, 1, @id_ue_m1_01);
    SET @id_ec_adminbd = LAST_INSERT_ID();

-- 2. Admin Réseaux
INSERT INTO ues (code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, active, date_creation)
VALUES ('M1GL02', 'Administration Réseaux (GL-RS)', 4, 4, 20, 0, 20, 40, @id_sem_m1_s2, true, NOW());
SET @id_ue_m1_02 = LAST_INSERT_ID();
    INSERT INTO ecs (code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id)
    VALUES ('M1GL021', 'Administration Réseaux (GL-RS)', 20, 0, 20, 0, 40, 1, @id_ue_m1_02);
    SET @id_ec_adminres = LAST_INSERT_ID();

-- 3. Formats et manipulation de données
INSERT INTO ues (code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, active, date_creation)
VALUES ('M1GL03', 'Formats et manipulation de données (GL-RS)', 3, 3, 10, 10, 10, 30, @id_sem_m1_s2, true, NOW());
SET @id_ue_m1_03 = LAST_INSERT_ID();
    INSERT INTO ecs (code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id)
    VALUES ('M1GL031', 'remplace XML', 10, 10, 10, 0, 30, 1, @id_ue_m1_03);
    SET @id_ec_xml = LAST_INSERT_ID();

-- 4. Web services
INSERT INTO ues (code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, active, date_creation)
VALUES ('M1GL04', 'Web services (GL)', 2, 2, 10, 0, 10, 20, @id_sem_m1_s2, true, NOW());
SET @id_ue_m1_04 = LAST_INSERT_ID();
    INSERT INTO ecs (code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id)
    VALUES ('M1GL041', 'remplace e-commerce', 10, 0, 10, 0, 20, 1, @id_ue_m1_04);
    SET @id_ec_websev = LAST_INSERT_ID();

-- 5. Technologies du Web
INSERT INTO ues (code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, active, date_creation)
VALUES ('M1GL05', 'Technologies du Web (GL)', 4, 4, 10, 10, 20, 40, @id_sem_m1_s2, true, NOW());
SET @id_ue_m1_05 = LAST_INSERT_ID();
    INSERT INTO ecs (code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id)
    VALUES ('M1GL051', 'Technologies du Web (GL)', 10, 10, 20, 0, 40, 1, @id_ue_m1_05);
    SET @id_ec_techweb = LAST_INSERT_ID();

-- 6. Intelligence artificielle
INSERT INTO ues (code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, active, date_creation)
VALUES ('M1GL06', 'Intelligence artificielle (GL)', 4, 4, 15, 15, 10, 40, @id_sem_m1_s2, true, NOW());
SET @id_ue_m1_06 = LAST_INSERT_ID();
    INSERT INTO ecs (code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id)
    VALUES ('M1GL061', 'Intelligence artificielle (GL)', 15, 15, 10, 0, 40, 1, @id_ue_m1_06);
    SET @id_ec_ai = LAST_INSERT_ID();

-- 7. Programmation Fonctionnelle
INSERT INTO ues (code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, active, date_creation)
VALUES ('M1GL07', 'Programmation Fonctionnelle: LISP(GL)', 2, 2, 10, 0, 10, 20, @id_sem_m1_s2, true, NOW());
SET @id_ue_m1_07 = LAST_INSERT_ID();
    INSERT INTO ecs (code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id)
    VALUES ('M1GL071', 'Programmation Fonctionnelle: LISP(GL)', 10, 0, 10, 0, 20, 1, @id_ue_m1_07);
    SET @id_ec_lisp = LAST_INSERT_ID();

-- 8. Développement mobile
INSERT INTO ues (code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, active, date_creation)
VALUES ('M1GL08', 'Développement mobile (GL-RS)', 3, 3, 10, 0, 20, 30, @id_sem_m1_s2, true, NOW());
SET @id_ue_m1_08 = LAST_INSERT_ID();
    INSERT INTO ecs (code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id)
    VALUES ('M1GL081', 'Développement mobile (GL-RS)', 10, 0, 20, 0, 30, 1, @id_ue_m1_08);
    SET @id_ec_mobile = LAST_INSERT_ID();

-- 9. Programmation parallèle
INSERT INTO ues (code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, active, date_creation)
VALUES ('M1GL09', 'Programmation parallèle (RS-GL optionnel)', 2, 2, 10, 0, 10, 20, @id_sem_m1_s2, true, NOW());
SET @id_ue_m1_09 = LAST_INSERT_ID();
    INSERT INTO ecs (code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id)
    VALUES ('M1GL091', 'Programmation parallèle', 10, 0, 10, 0, 20, 1, @id_ue_m1_09);
    SET @id_ec_parall = LAST_INSERT_ID();

-- 10. Virtualisation
INSERT INTO ues (code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, active, date_creation)
VALUES ('M1GL10', 'Virtualisation (RS)', 2, 2, 10, 0, 10, 20, @id_sem_m1_s2, true, NOW());
SET @id_ue_m1_10 = LAST_INSERT_ID();
    INSERT INTO ecs (code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id)
    VALUES ('M1GL101', 'Virtualisation (RS)', 10, 0, 10, 0, 20, 1, @id_ue_m1_10);
    SET @id_ec_virtu = LAST_INSERT_ID();

-- 11. Qualité de service
INSERT INTO ues (code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, active, date_creation)
VALUES ('M1GL11', 'Qualité de service et Performance des réseaux (RS)', 3, 3, 20, 0, 10, 30, @id_sem_m1_s2, true, NOW());
SET @id_ue_m1_11 = LAST_INSERT_ID();
    INSERT INTO ecs (code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id)
    VALUES ('M1GL111', 'Qualité de service', 20, 0, 10, 0, 30, 1, @id_ue_m1_11);
    SET @id_ec_qos = LAST_INSERT_ID();

-- 12. Sécurité des réseaux
INSERT INTO ues (code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, active, date_creation)
VALUES ('M1GL12', 'Sécurité des réseaux (RS)', 3, 3, 20, 10, 10, 40, @id_sem_m1_s2, true, NOW());
SET @id_ue_m1_12 = LAST_INSERT_ID();
    INSERT INTO ecs (code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id)
    VALUES ('M1GL121', 'Sécurité des réseaux (RS)', 20, 10, 10, 0, 40, 1, @id_ue_m1_12);
    SET @id_ec_secu = LAST_INSERT_ID();

-- 13. Supports de transmission
INSERT INTO ues (code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, active, date_creation)
VALUES ('M1GL13', 'Supports de transmission (RS)', 2, 2, 10, 0, 10, 20, @id_sem_m1_s2, true, NOW());
SET @id_ue_m1_13 = LAST_INSERT_ID();
    INSERT INTO ecs (code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id)
    VALUES ('M1GL131', 'Supports de transmission (RS)', 10, 0, 10, 0, 20, 1, @id_ue_m1_13);
    SET @id_ec_trans = LAST_INSERT_ID();

-- 14. Bases des télécommunication
INSERT INTO ues (code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, active, date_creation)
VALUES ('M1GL14', 'Bases des télécommunication (RS)', 2, 2, 10, 10, 0, 20, @id_sem_m1_s2, true, NOW());
SET @id_ue_m1_14 = LAST_INSERT_ID();
    INSERT INTO ecs (code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id)
    VALUES ('M1GL141', 'Bases des télécommunication (RS)', 10, 10, 0, 0, 20, 1, @id_ue_m1_14);
    SET @id_ec_telecom = LAST_INSERT_ID();

-- 15. Réseaux sans fil
INSERT INTO ues (code, libelle, credits, coefficientue, cm, td, tp, vht, semestre_id, active, date_creation)
VALUES ('M1GL15', 'Réseaux sans fil (RS)', 2, 2, 10, 0, 10, 20, @id_sem_m1_s2, true, NOW());
SET @id_ue_m1_15 = LAST_INSERT_ID();
    INSERT INTO ecs (code, libelle, cm, td, tp, tpe, vht, coefficient, ue_id)
    VALUES ('M1GL151', 'Réseaux sans fil (RS)', 10, 0, 10, 0, 20, 1, @id_ue_m1_15);
    SET @id_ec_wifi = LAST_INSERT_ID();

-- ---------------------------------------------------------------------------------------
-- PARTIE 2 : ENSEIGNANTS (Base : daos_enseignant_db)
-- ---------------------------------------------------------------------------------------
USE daos_enseignant_db;

INSERT INTO enseignants (matricule, nom, prenom, email, date_creation, date_modification, est_actif) VALUES 
(1001, 'DIAGNE', 'Serigne', 'sdiagne@uasz.sn', NOW(), NOW(), true),
(1002, 'FAYE', 'Youssou', 'yfaye@uasz.sn', NOW(), NOW(), true),
(1003, 'DIOP', 'Ibrahima', 'idiop@uasz.sn', NOW(), NOW(), true),
(1004, 'DRAME', 'Khadim', 'kdrame@uasz.sn', NOW(), NOW(), true),
(1005, 'GAYE', 'Mouhamadou', 'mgaye@uasz.sn', NOW(), NOW(), true),
(1006, 'SECK', 'Assane', 'aseck@uasz.sn', NOW(), NOW(), true),
(1007, 'DIALLO', 'Thierno Ahmadou', 'tadiallo@uasz.sn', NOW(), NOW(), true),
(1008, 'DASYLVA', 'Marius', 'mdasylva@uasz.sn', NOW(), NOW(), true),
(1009, 'FAYE', 'Aladji', 'afaye@uasz.sn', NOW(), NOW(), true);

SET @id_ens_diagne = (SELECT id FROM enseignants WHERE email='sdiagne@uasz.sn');
SET @id_ens_youssou = (SELECT id FROM enseignants WHERE email='yfaye@uasz.sn');
SET @id_ens_ibrahima = (SELECT id FROM enseignants WHERE email='idiop@uasz.sn');
SET @id_ens_khadim = (SELECT id FROM enseignants WHERE email='kdrame@uasz.sn');
SET @id_ens_mouhamadou = (SELECT id FROM enseignants WHERE email='mgaye@uasz.sn');
SET @id_ens_assane = (SELECT id FROM enseignants WHERE email='aseck@uasz.sn');
SET @id_ens_thierno = (SELECT id FROM enseignants WHERE email='tadiallo@uasz.sn');
SET @id_ens_marius = (SELECT id FROM enseignants WHERE email='mdasylva@uasz.sn');
SET @id_ens_aladji = (SELECT id FROM enseignants WHERE email='afaye@uasz.sn');

-- ---------------------------------------------------------------------------------------
-- PARTIE 3 : CHOIX/ASSIGNATIONS (Base : daos_choix_enseignement_db)
-- ---------------------------------------------------------------------------------------
USE daos_choix_enseignement_db;

-- Assignations
INSERT INTO choix (id_enseignant, id_enseignement, date_choix, date_creation, date_modification)
VALUES (@id_ens_diagne, @id_ec_adminbd, NOW(), NOW(), NOW());

INSERT INTO choix (id_enseignant, id_enseignement, date_choix, date_creation, date_modification)
VALUES (@id_ens_youssou, @id_ec_adminres, NOW(), NOW(), NOW());

INSERT INTO choix (id_enseignant, id_enseignement, date_choix, date_creation, date_modification)
VALUES (@id_ens_ibrahima, @id_ec_xml, NOW(), NOW(), NOW());

INSERT INTO choix (id_enseignant, id_enseignement, date_choix, date_creation, date_modification)
VALUES (@id_ens_ibrahima, @id_ec_websev, NOW(), NOW(), NOW());

INSERT INTO choix (id_enseignant, id_enseignement, date_choix, date_creation, date_modification)
VALUES (@id_ens_ibrahima, @id_ec_techweb, NOW(), NOW(), NOW());

INSERT INTO choix (id_enseignant, id_enseignement, date_choix, date_creation, date_modification)
VALUES (@id_ens_khadim, @id_ec_ai, NOW(), NOW(), NOW());

INSERT INTO choix (id_enseignant, id_enseignement, date_choix, date_creation, date_modification)
VALUES (@id_ens_mouhamadou, @id_ec_lisp, NOW(), NOW(), NOW());

INSERT INTO choix (id_enseignant, id_enseignement, date_choix, date_creation, date_modification)
VALUES (@id_ens_assane, @id_ec_mobile, NOW(), NOW(), NOW());

INSERT INTO choix (id_enseignant, id_enseignement, date_choix, date_creation, date_modification)
VALUES (@id_ens_thierno, @id_ec_parall, NOW(), NOW(), NOW());

INSERT INTO choix (id_enseignant, id_enseignement, date_choix, date_creation, date_modification)
VALUES (@id_ens_thierno, @id_ec_virtu, NOW(), NOW(), NOW());

INSERT INTO choix (id_enseignant, id_enseignement, date_choix, date_creation, date_modification)
VALUES (@id_ens_marius, @id_ec_qos, NOW(), NOW(), NOW());

INSERT INTO choix (id_enseignant, id_enseignement, date_choix, date_creation, date_modification)
VALUES (@id_ens_youssou, @id_ec_secu, NOW(), NOW(), NOW());

INSERT INTO choix (id_enseignant, id_enseignement, date_choix, date_creation, date_modification)
VALUES (@id_ens_aladji, @id_ec_trans, NOW(), NOW(), NOW());

INSERT INTO choix (id_enseignant, id_enseignement, date_choix, date_creation, date_modification)
VALUES (@id_ens_aladji, @id_ec_telecom, NOW(), NOW(), NOW());

INSERT INTO choix (id_enseignant, id_enseignement, date_choix, date_creation, date_modification)
VALUES (@id_ens_aladji, @id_ec_wifi, NOW(), NOW(), NOW());