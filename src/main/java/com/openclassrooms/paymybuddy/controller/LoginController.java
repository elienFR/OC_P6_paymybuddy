package com.openclassrooms.paymybuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

  private final OAuth2AuthorizedClientService authorizedClientService;

  private static String authorizationRequestBaseUri = "oauth2/authorization";

  Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

  @Autowired
  private ClientRegistrationRepository clientRegistrationRepository;

  public LoginController(OAuth2AuthorizedClientService authorizedClientService) {
    this.authorizedClientService = authorizedClientService;
  }

  @GetMapping("/login")
  public String getLoginPage(){
    return "login";
  }

}
