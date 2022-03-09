package com.openclassrooms.paymybuddy.controller;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

  private final OAuth2AuthorizedClientService authorizedClientService;

  public LoginController(OAuth2AuthorizedClientService authorizedClientService) {
    this.authorizedClientService = authorizedClientService;
  }

  @GetMapping("/login")
  public String login(){
    return "login";
  }

}
