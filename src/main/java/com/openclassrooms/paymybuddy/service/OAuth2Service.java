package com.openclassrooms.paymybuddy.service;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OAuth2Service {

  private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

  public OAuth2Service(OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
    this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
  }

  /**
   * This method get the IdToken of a principal user, if the instance of this token is instance of
   * DefaultOidcUser.
   *
   * @param principal is the instance of principal User.
   * @return the IdToken of th principal user's instanciation.
   */
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

}
