package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.configuration.SpringSecurityConfig;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.AuthorityService;
import com.openclassrooms.paymybuddy.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.transaction.Transactional;
import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

  @Autowired
  private UserService userService;
  @Autowired
  private SpringSecurityConfig springSecurityConfig;

  private static final Logger LOGGER = LogManager.getLogger(ProfileController.class);


  @GetMapping
  public String getProfile(Principal principal, Model model) throws Exception {

    User user = userService.getUserFromPrincipal(principal);

    model.addAttribute("userFirstName", user.getFirstName());
    model.addAttribute("userLastName", user.getLastName());
    model.addAttribute("userEmail", user.getEmail());
    model.addAttribute("userIsLocal",user.isFromLocal());

    return "profile";
  }

  @PostMapping
  public String postProfile(
    Principal principal,
    Model model,
    String firstName,
    String lastName,
    String email,
    String emailConfirmation,
    boolean changePassword,
    String currentPassword,
    String newPassword,
    String newPasswordConfirmation
  ) throws Exception {

    boolean modifiedSomething = false;

    User userToUpdate = userService.getUserFromPrincipal(principal);

    //The first name must never be blank nor empty !
    if (!firstName.isBlank()) {
      if (userToUpdate.getFirstName() == null) {
        userToUpdate.setFirstName(firstName);
        modifiedSomething = true;
      } else if (!userToUpdate.getFirstName().equals(firstName)) {
        userToUpdate.setFirstName(firstName);
        modifiedSomething = true;
      }
    }

    //We can make our last name blank
    if (userToUpdate.getLastName() == null) {
      if (!lastName.isBlank()) {
        userToUpdate.setLastName(lastName);
        modifiedSomething = true;
      }
    } else if (!userToUpdate.getLastName().equals(lastName)) {
      userToUpdate.setLastName(lastName);
      modifiedSomething = true;
    }

    //Email must never be blank in the form
    if (!email.isBlank()) {
      if (!userToUpdate.getEmail().equals(email)) {
        if (email.equals(emailConfirmation)) {
          if (!userService.existsByEmail(email)) {
            userToUpdate.setEmail(email);
            modifiedSomething = true;
          } else {
            model.addAttribute("emailAlreadyExists", true);
          }
        } else {
          model.addAttribute("emailMismatch", true);
        }
      }
    } else {
      model.addAttribute("emailMustNotBeBlank", true);
    }

    //Password treatment
    String currentPasswordFromDB = userToUpdate.getPassword();
    if (changePassword) {
      if (!newPassword.isBlank()) {
        if (newPassword.equals(newPasswordConfirmation)) {
          userToUpdate.setPassword(springSecurityConfig.passwordEncoder().encode(newPassword));
          modifiedSomething = true;
        }
      } else {
        model.addAttribute("newPassBlank", true);
      }
    }

    //We save user's update here
    if (modifiedSomething) {
      if (!userToUpdate.isFromLocal()
        || springSecurityConfig.passwordEncoder().matches(currentPassword, currentPasswordFromDB)) {
        userToUpdate = userService.save(userToUpdate);
        model.addAttribute("success", true);
      } else {
        model.addAttribute("wrongPass", true);
      }
    }

    model.addAttribute("userFirstName", userToUpdate.getFirstName());
    model.addAttribute("userLastName", userToUpdate.getLastName());
    model.addAttribute("userEmail", userToUpdate.getEmail());
    model.addAttribute("userIsLocal",userToUpdate.isFromLocal());

    return "/profile";

  }
}
