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
    private String defaultUserPassword;

    @Value("${default-user-role}")
    private String defaultUserRole;

    @Value("${arghyam.app.client.id}")
    private String arghyamKeyCloakAppClient;

    @Value("${arghyam.app.client.secret}")
    private String arghyamKeyCloakAppClientSecret;

    @Value("${registry-base-url}")
    private String registryBaseUrl;

    @Value("${spring.data.neo4j.uri}")
    private String neo4jBaseUri;

    @Value("${spring.data.neo4j.username}")
    private String neo4jUsername;

    @Value("${spring.data.neo4j.password}")
    private String neo4jPassword;

    public String getNeo4jBaseUri() {
        return neo4jBaseUri;
    }

    public void setNeo4jBaseUri(String neo4jBaseUri) {
        this.neo4jBaseUri = neo4jBaseUri;
    }

    public String getNeo4jUsername() {
        return neo4jUsername;
    }

    public void setNeo4jUsername(String neo4jUsername) {
        this.neo4jUsername = neo4jUsername;
    }

    public String getNeo4jPassword() {
        return neo4jPassword;
    }

    public void setNeo4jPassword(String neo4jPassword) {
        this.neo4jPassword = neo4jPassword;
    }

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
