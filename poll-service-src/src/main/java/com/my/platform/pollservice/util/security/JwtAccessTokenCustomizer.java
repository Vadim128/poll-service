package com.my.platform.pollservice.util.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JwtAccessTokenCustomizer extends DefaultAccessTokenConverter implements JwtAccessTokenConverterConfigurer {
    public static final String USER_PROFILE = "user_profile";

    private static final String CLIENT_NAME_ELEMENT_IN_JWT = "resource_access";
    private static final String ROLE_BLOCK_NAME_ELEMENT_IN_JWT = "realm_access";

    private static final String CLIENT_ID_ELEMENT_IN_JWT = "azp";

    private static final String MOBILE_CLIENT_ID_PREFIX = "mobile_";

    private static final String ROLE_ELEMENT_IN_JWT = "roles";

    private final ObjectMapper mapper;

    @Override
    public void configure(JwtAccessTokenConverter converter) {
        converter.setAccessTokenConverter(this);
        log.info("Configured {}", JwtAccessTokenConverter.class.getSimpleName());
    }

    /**
     * Spring oauth2 expects roles under authorities element in tokenMap, but keycloak provides it under resource_access. Hence extractAuthentication
     * method is overriden to extract roles from resource_access.
     *
     * @return OAuth2Authentication with authorities for given application
     */
    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> tokenMap) {
        log.debug("Begin extractAuthentication: tokenMap = {}", tokenMap);
        JsonNode token = mapper.convertValue(tokenMap, JsonNode.class);
        List<GrantedAuthority> authorities = extractRoles(token); // extracting client roles

        OAuth2Authentication authentication = super.extractAuthentication(tokenMap);
        OAuth2Request oAuth2Request = authentication.getOAuth2Request();

        var userProfile = new KeycloakUserProfile()
                .setId((String) tokenMap.get("sub"))
                .setEmail((String) tokenMap.get("email"))
                .setFirstName((String) tokenMap.get("given_name"))
                .setLastName((String) tokenMap.get("family_name"))
                .setPreferredUsername((String) tokenMap.get("preferred_username"))
                .setRoles(
                        authorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList())
                );
        String clientId = token.get(CLIENT_ID_ELEMENT_IN_JWT).asText();
        userProfile.setCurrentCelebrityId(fillCurrentCelebrityIdIfMobile(userProfile.getRoles(), clientId));

        OAuth2Request request =
                new OAuth2Request(oAuth2Request.getRequestParameters(), oAuth2Request.getClientId(), authorities, true, oAuth2Request.getScope(),
                        oAuth2Request.getResourceIds(), null, null, Map.of(USER_PROFILE, userProfile));

        Authentication usernamePasswordAuthentication = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), "N/A", authorities);
        log.debug("End extractAuthentication");
        return new OAuth2Authentication(request, usernamePasswordAuthentication);
    }

    private List<GrantedAuthority> extractRoles(JsonNode jwt) {
        log.debug("Begin extractRoles: jwt = {}", jwt);
        Set<String> rolesWithPrefix = new HashSet<>();

        jwt.path(ROLE_BLOCK_NAME_ELEMENT_IN_JWT)
                .path(ROLE_ELEMENT_IN_JWT)
                .elements()
                .forEachRemaining(r -> rolesWithPrefix.add("ROLE_" + r.asText()))
        ;

        final List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(rolesWithPrefix.toArray(new String[0]));
        log.debug("End extractRoles: roles = {}", authorityList);
        return authorityList;
    }

    private Set<String> extractClients(JsonNode jwt) {
        log.debug("Begin extractClients: jwt = {}", jwt);
        if (jwt.has(CLIENT_NAME_ELEMENT_IN_JWT)) {
            JsonNode resourceAccessJsonNode = jwt.path(CLIENT_NAME_ELEMENT_IN_JWT);
            final Set<String> clientNames = new HashSet<>();
            resourceAccessJsonNode.fieldNames()
                    .forEachRemaining(clientNames::add);

            log.debug("End extractClients: clients = {}", clientNames);
            return clientNames;

        } else {
            throw new IllegalArgumentException("Expected element " + CLIENT_NAME_ELEMENT_IN_JWT + " not found in token");
        }
    }

    private String fillCurrentCelebrityIdIfMobile(List<String> roles, String clientId) {
        String currentCelebrityId = null;

        if (CollectionUtils.isEmpty(roles)) {
            return null;
        }
        clientId = clientId.trim();
        if (roles.stream().anyMatch(r -> r.equals(RoleConstants.ROLE_USER))) {
            currentCelebrityId = clientId.startsWith(MOBILE_CLIENT_ID_PREFIX)
                    ? clientId.replaceFirst(MOBILE_CLIENT_ID_PREFIX, "")
                    : null;
        }

        return currentCelebrityId;
    }
}
