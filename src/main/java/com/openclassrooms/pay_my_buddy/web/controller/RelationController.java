package com.openclassrooms.pay_my_buddy.web.controller;

import com.openclassrooms.pay_my_buddy.application.service.relation.RelationService;
import com.openclassrooms.pay_my_buddy.web.dto.relation.NewRelationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/relations")
public class RelationController {

    private final RelationService relationService;

    /**
     * Affiche le formulaire d’ajout d’une nouvelle relation bénéficiaire.
     * @param dto modèle de formulaire
     * @return vue du formulaire
     */
    @GetMapping("/new")
    public String showForm(@ModelAttribute("form") NewRelationRequest dto) {
        return "relations/new";
    }

    /**
     * Ajoute une relation bénéficiaire pour l’utilisateur courant.
     * @param dto requête contenant l’email cible
     * @param bindingResult erreurs de validation
     * @param auth authentification courante
     * @param ra attributs de redirection (messages flash)
     * @return redirection vers le formulaire
     */
    @PostMapping
    public String add(@Valid @ModelAttribute("form") NewRelationRequest dto,
        BindingResult bindingResult,
        Authentication auth,
        RedirectAttributes ra) {

        if (bindingResult.hasErrors()) {
            return "relations/new";
        }

        String currentUserEmail = auth.getName();
        try {
            relationService.addBeneficiary(currentUserEmail, dto.getEmail());
            ra.addFlashAttribute("success", "Relation ajoutée.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/relations/new";
    }

}
