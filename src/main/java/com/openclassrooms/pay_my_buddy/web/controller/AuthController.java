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

  @GetMapping("/")
  public String defaultPage() {
    return "redirect:/login";
  }

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

  @GetMapping("/login")
  public String loginPage(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()) {
      return "redirect:/transfer";
    }
    return "login";
  }

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
