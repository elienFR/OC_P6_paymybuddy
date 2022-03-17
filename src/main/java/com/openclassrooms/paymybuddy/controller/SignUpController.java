package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.utils.Role;
import com.openclassrooms.paymybuddy.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping("/signUp")
public class SignUpController {

  private static final Logger LOGGER = LogManager.getLogger(SignUpController.class);
  @Autowired
  private UserService userService;

  @GetMapping
  public String getSignUpPage(Model model) {
    model.addAttribute("firstName", null);
    model.addAttribute("lastName", null);
    model.addAttribute("email", null);
    return "signUp";
  }

  @PostMapping
  public String postSignUpPage(
    Model model,
    @NotNull String firstName,
    @Nullable String lastName,
    @NotNull String email,
    @NotNull String emailConfirmation,
    @NotNull String password,
    @NotNull String passwordConfirmation
  ) {

    boolean errorOccurred = false;

    LOGGER.info("Creating new user Account.");

    if (!userService.existsByEmail(email)) {
      if (email.equals(emailConfirmation)) {
        if (password.equals(passwordConfirmation)) {
          userService.createAndSaveLocalUser(
            firstName, lastName, email, password, Role.ROLE_USER
          );
          return "redirect:/login?accountCreated";
        } else {
          model.addAttribute("passwordMismatch", true);
        }
      } else {
        model.addAttribute("emailMismatch", true);
      }
    } else {
      model.addAttribute("emailExists", true);
    }

    model.addAttribute("firstName", firstName);
    model.addAttribute("lastName", lastName);
    model.addAttribute("email", email);

    return "/signUp";
  }

}
