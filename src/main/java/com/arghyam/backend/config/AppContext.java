package com.arghyam.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppContext {

    @Value("${keycloak.auth-server-url}")
    private String keyCloakServiceUrl;

    @Value("${admin-user-username}")
    private String adminUserName;

    @Value("${admin-user-password}")
    private String adminUserpassword;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak-client-id}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Value("${client.granttype}")
    private String grantType;

    @Value("${default-user-password}")
    public String defaultUserPassword;

    @Value("${default-user-role}")
    public String defaultUserRole;

    @Value("${arghyam.app.client.id}")
    public String arghyamKeyCloakAppClient;

    @Value("${arghyam.app.client.secret}")
    public String arghyamKeyCloakAppClientSecret;

    @Value("${registry-base-url}")
    private String registryBaseUrl;

    public String getArghyamKeyCloakAppClient() {
        return arghyamKeyCloakAppClient;
    }

    public void setArghyamKeyCloakAppClient(String arghyamKeyCloakAppClient) {
        this.arghyamKeyCloakAppClient = arghyamKeyCloakAppClient;
    }


    public String getArghyamKeyCloakAppClientSecret() {
        return arghyamKeyCloakAppClientSecret;
    }

    public void setArghyamKeyCloakAppClientSecret(String arghyamKeyCloakAppClientSecret) {
        this.arghyamKeyCloakAppClientSecret = arghyamKeyCloakAppClientSecret;
    }

    public String getKeyCloakServiceUrl() {
        return keyCloakServiceUrl;
    }

    public void setKeyCloakServiceUrl(String keyCloakServiceUrl) {
        this.keyCloakServiceUrl = keyCloakServiceUrl;
    }

    public String getAdminUserName() {
        return adminUserName;
    }

    public void setAdminUserName(String adminUserName) {
        this.adminUserName = adminUserName;
    }

    public String getAdminUserpassword() {
        return adminUserpassword;
    }

    public void setAdminUserpassword(String adminUserpassword) {
        this.adminUserpassword = adminUserpassword;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getDefaultUserPassword() {
        return defaultUserPassword;
    }

    public void setDefaultUserPassword(String defaultUserPassword) {
        this.defaultUserPassword = defaultUserPassword;
    }

    public String getDefaultUserRole() {
        return defaultUserRole;
    }

    public void setDefaultUserRole(String defaultUserRole) {
        this.defaultUserRole = defaultUserRole;
    }

    public String getRegistryBaseUrl() {
        return registryBaseUrl;
    }

    public void setRegistryBaseUrl(String registryBaseUrl) {
        this.registryBaseUrl = registryBaseUrl;
    }
}
