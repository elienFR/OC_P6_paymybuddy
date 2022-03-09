package com.openclassrooms.paymybuddy.controller;

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
import java.util.Map;

@RestController
@RequestMapping("/principal")
public class PrincipalController {

  private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

  public PrincipalController(OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
    this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
  }

  @GetMapping
  public Principal retrievePrincipal(Principal principal) {
    return principal;
  }

  @GetMapping("/github")
  public String getUserInfo(Principal user) {
    StringBuffer userInfo = new StringBuffer();

    if (user instanceof UsernamePasswordAuthenticationToken) {
      userInfo.append(getUsernamePasswordLoginInfo(user));
    } else if (user instanceof OAuth2AuthenticationToken) {
      userInfo.append(getOAuth2LoginUserInfo(user));
    }

    return userInfo.toString();
  }

  private StringBuffer getOAuth2LoginUserInfo(Principal user) {
    StringBuffer protectedInfo = new StringBuffer();
    OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) user;
    OAuth2User principal = ((OAuth2AuthenticationToken)user).getPrincipal();

    OAuth2AuthorizedClient authClient = this.oAuth2AuthorizedClientService
      .loadAuthorizedClient(authToken.getAuthorizedClientRegistrationId(), authToken.getName());

    Map<String, Object> userDetails = ((DefaultOAuth2User) authToken
      .getPrincipal()).getAttributes();

    String userToken = authClient.getAccessToken().getTokenValue();

    protectedInfo.append("Welcome, " + userDetails.get("name") + "<br><br>");
    protectedInfo.append("email : " + userDetails.get("email") + "<br><br>");
    protectedInfo.append("token : " + userToken + "<br><br>");

    OidcIdToken idToken = getIdToken(principal);

    if (idToken != null) {
      protectedInfo.append("idTokenValue : " + idToken.getTokenValue() + "<br><br>");
      protectedInfo.append("Token mapped values <br><br>");

      Map<String, Object> claims = idToken.getClaims();

      for (String key : claims.keySet()) {
        protectedInfo.append("    " + key + ":    " + claims.get(key) + "<br>");
      }
    }

    return protectedInfo;
  }

  private StringBuffer getUsernamePasswordLoginInfo(Principal user) {
    StringBuffer usernameInfo = new StringBuffer();
    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) user;

    if (token.isAuthenticated()) {
      User u = (User) token.getPrincipal();
      usernameInfo.append("Welcome, " + u.getUsername());
    } else {
      usernameInfo.append("NA");
    }

    return usernameInfo;
  }

  private OidcIdToken getIdToken(OAuth2User principal) {
    if (principal instanceof DefaultOidcUser) {
      DefaultOidcUser oidcUser = (DefaultOidcUser) principal;
      return oidcUser.getIdToken();
    }
    return null;
  }

}
