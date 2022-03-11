package com.openclassrooms.paymybuddy.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Service
public class OAuth2Service {

  private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

  public OAuth2Service(OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
    this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
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

      Map<String, Object> claims = getOidcClaims(authToken);

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

  /**
   * This method retrieve claims from an OAuth2AuthenticationToken instance.
   *
   * @param authToken OAuth2AuthenticationToken instance.
   * @return return a Map that contains all the claims contained by the token.
   */
  public Map<String,Object> getOidcClaims(OAuth2AuthenticationToken authToken) {
    OAuth2User principal = authToken.getPrincipal();
    OidcIdToken idToken = getIdToken(principal);
    Map<String, Object> claims = idToken.getClaims();
    return claims;
  }

  public String getUserInfo(Principal principal) {
    StringBuffer userInfo = new StringBuffer();
    if (principal instanceof UsernamePasswordAuthenticationToken) {
      userInfo.append(getUsernamePasswordLoginInfo(principal));
    } else if (principal instanceof OAuth2AuthenticationToken) {
      userInfo.append(getOAuth2LoginUserInfo(principal));
    }
    return userInfo.toString();
  }
}
