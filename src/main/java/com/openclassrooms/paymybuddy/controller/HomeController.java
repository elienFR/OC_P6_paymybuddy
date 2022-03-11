package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

  @Autowired
  private UserService userService;

  @GetMapping("/home")
  public String getHome(Principal principal, Model model) throws Exception {
    User userFromDB = userService.getUserFromPrincipal(principal);
    model.addAttribute("userEmail", userFromDB.getEmail());

    return "home";
  }

}
