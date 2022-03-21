package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/contact")
public class ContactController {

  @Autowired
  private UserService userService;

  @GetMapping
  public String getContact(Principal principal, Model model) throws Exception {
    model.addAttribute("userEmail", userService.getUserFromPrincipal(principal).getEmail());
    return "contact";
  }

  @PostMapping
  public String postContactForm(
    Principal principal,
    Model model,
    String contactEmail,
    String subject,
    String contentOfEmail
  ) {
    StringBuffer errorString = new StringBuffer();

    if(contactEmail==null || contactEmail.isBlank()) {
      errorString.append("&blankEmail");
    }
    if(subject==null || subject.isBlank()) {
      errorString.append("&blankSubject");
    }
    if(contentOfEmail==null || contentOfEmail.isBlank()) {
      errorString.append("&blankContent");
    }

    if(errorString.toString().isBlank()){

      // TODO : Will be implemented when we will have a mail server.
      // TODO : create a service to send mails thanks to contact form.
      // TODO : create a contactSuccessful.html page
//      return "redirect:/contact/successful";

      return "redirect:/contact";
    }

    return "redirect:/contact?" + errorString;
  }

}
