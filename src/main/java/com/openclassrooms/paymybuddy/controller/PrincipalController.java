package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.service.OAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/principal")
public class PrincipalController {

  @Autowired
  private OAuth2Service oAuth2Service;


  @GetMapping
  public Principal retrievePrincipal(Principal principal) {
    return principal;
  }

  @GetMapping("/getUserInfo")
  public String getUserInfo(Principal principal) {
    return oAuth2Service.getUserInfo(principal);
  }




}
