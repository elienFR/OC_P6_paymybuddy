package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/userDelete")
public class UserDeleteController {

  private static final Logger LOGGER = LogManager.getLogger(UserDeleteController.class);
  @Autowired
  private UserService userService;

  @GetMapping
  public String getUserDelete(Principal principal) {
    return "userDelete";
  }

  @PostMapping
  public String proceedUserDelete(Principal principal) {
    try {
      User userToDelete = userService.getUserFromPrincipal(principal);
      userService.delete(userToDelete);
    } catch (Exception e) {
      LOGGER.error("No such user to delete !");
      e.printStackTrace();
    } finally {
      return "redirect:/logoutAfterDelete";
    }
  }
}
