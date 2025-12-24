package com.uasz.daos.auth.controllers;

import com.uasz.daos.auth.dto.DashboardStatsDTO;
import com.uasz.daos.auth.enums.Role;
import com.uasz.daos.auth.services.CustomUserDetails;
import com.uasz.daos.auth.services.DashboardService;
import com.uasz.daos.auth.services.FormationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private FormationService formationService;

    /**
     * Page d'accueil - Affiche welcome.html ou redirige vers le dashboard selon l'authentification
     */
    @GetMapping("/")
    public String index() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Si non authentifié, afficher la page d'accueil publique
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return "welcome";
        }

        // Si authentifié, rediriger vers le dashboard approprié selon le rôle
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Role userRole = userDetails.getRole();

        switch (userRole) {
            case ETUDIANT:
                return "redirect:/dashboard/etudiant";
            case ENSEIGNANT:
                return "redirect:/dashboard/enseignant";
            case RESPONSABLE_MASTER:
                return "redirect:/dashboard/responsable";
            case COORDONATEUR_DES_LICENCES:
                return "redirect:/dashboard/coordinateur";
            case ADMIN:
            case CHEF_DE_DEPARTEMENT:
                return "redirect:/dashboard/admin";
            default:
                return "redirect:/login";
        }
    }

    /**
     * Dashboard ÉTUDIANT - Voir uniquement les emplois du temps de toutes les formations
     */
    @GetMapping("/dashboard/etudiant")
    public String dashboardEtudiant(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        model.addAttribute("formations", formationService.getAllFormations());
        model.addAttribute("userRole", userDetails.getRole());
        model.addAttribute("userName", userDetails.getPrenom() + " " + userDetails.getNom());
        return "dashboard-etudiant";
    }

    /**
     * Dashboard ENSEIGNANT - Consultation uniquement (formations, maquettes, pédagogies, cahier de texte, EDT)
     */
    @GetMapping("/dashboard/enseignant")
    public String dashboardEnseignant(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        model.addAttribute("formations", formationService.getAllFormations());
        model.addAttribute("userRole", userDetails.getRole());
        model.addAttribute("userName", userDetails.getPrenom() + " " + userDetails.getNom());
        return "dashboard-enseignant";
    }

    /**
     * Dashboard RESPONSABLE MASTER - Gestion complète des masters
     */
    @GetMapping("/dashboard/responsable")
    public String dashboardResponsable(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        DashboardStatsDTO stats = dashboardService.getStats();
        model.addAttribute("stats", stats);
        model.addAttribute("formations", formationService.getAllFormations());
        model.addAttribute("userRole", userDetails.getRole());
        model.addAttribute("userName", userDetails.getPrenom() + " " + userDetails.getNom());
        return "dashboard-responsable";
    }

    /**
     * Dashboard COORDINATEUR LICENCE - Gestion complète des licences
     */
    @GetMapping("/dashboard/coordinateur")
    public String dashboardCoordinateur(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        DashboardStatsDTO stats = dashboardService.getStats();
        model.addAttribute("stats", stats);
        model.addAttribute("formations", formationService.getAllFormations());
        model.addAttribute("userRole", userDetails.getRole());
        model.addAttribute("userName", userDetails.getPrenom() + " " + userDetails.getNom());
        return "dashboard-coordinateur";
    }

    /**
     * Dashboard ADMINISTRATEUR - Accès complet à tout
     */
    @GetMapping("/dashboard/admin")
    public String dashboardAdmin(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        DashboardStatsDTO stats = dashboardService.getStats();
        model.addAttribute("stats", stats);
        model.addAttribute("userRole", userDetails.getRole());
        model.addAttribute("userName", userDetails.getPrenom() + " " + userDetails.getNom());
        return "index"; // Utilise le dashboard admin existant (index.html)
    }
}