package com.openclassrooms.pay_my_buddy.web.controller;

import com.openclassrooms.pay_my_buddy.web.dto.auth.SignupRequest;
import com.openclassrooms.pay_my_buddy.application.service.authentication.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  /**
   * Page par défaut: redirige vers la page de connexion.
   * @return redirection /login
   */
  @GetMapping("/")
  public String defaultPage() {
    return "redirect:/login";
  }

  /**
   * Affiche la page d’inscription.
   * @param model modèle vue
   * @param error indicateur d’erreur de duplication email
   * @return vue signup
   */
  @GetMapping("/signup")
  public String signupPage(Model model,
      @RequestParam(value = "error", required = false) String error) {
    model.addAttribute("signupDTO", new SignupRequest());
    if (error != null) {
      model.addAttribute("errorMessage",
          "L'adresse e-mail que vous avez saisie est déjà utilisée.");
    }
    return "signup";
  }

  /**
   * Traite l’inscription utilisateur.
   * @param signupRequest données d’inscription
   * @param redirectAttributes attributs de redirection
   * @return redirection vers /login en cas de succès, sinon /signup?error
   */
  @PostMapping("/signup")
  public String signup(SignupRequest signupRequest, RedirectAttributes redirectAttributes) {
    boolean success = authService.signup(signupRequest);
    if (success) {
      return "redirect:/login";
    } else {
      redirectAttributes.addAttribute("error", true);
      return "redirect:/signup";
    }
  }

  /**
   * Affiche la page de connexion, ou redirige vers /transfer si déjà connecté.
   */
  @GetMapping("/login")
  public String loginPage(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()) {
      return "redirect:/transfer";
    }
    return "login";
  }

  /**
   * Déconnecte l’utilisateur en vidant les cookies d’authentification.
   * @param response réponse HTTP pour définir les cookies expirés
   * @return redirection /login
   */
  @PostMapping("/logout")
  public String logout(HttpServletResponse response) {
    Cookie accessTokenCookie = new Cookie("accessToken", "");
    accessTokenCookie.setHttpOnly(true);
    accessTokenCookie.setPath("/");
    accessTokenCookie.setMaxAge(0);
    accessTokenCookie.setSecure(true);

    Cookie refreshTokenCookie = new Cookie("refreshToken", "");
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(0);
    refreshTokenCookie.setSecure(true);

    response.addCookie(accessTokenCookie);
    response.addCookie(refreshTokenCookie);

    return "redirect:/login";
  }

}
