package org.forwater.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties
public class LoginResponseDTO {

    @JsonProperty("access_token")
    public String accessToken;

    @JsonProperty("expires_in")
    public long expiresIn;

    @JsonProperty("refresh_expires_in")
    public long refreshRxpiresIn;

    @JsonProperty("refresh_token")
    public String refreshToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("not-before-policy")
    private String notBeforePolicy;

    @JsonProperty("session_state")
    private String sessionState;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("id_token")
    private String id_token;

    public String getId_token() {
        return id_token;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSessionState() {
        return sessionState;
    }

    public void setSessionState(String sessionState) {
        this.sessionState = sessionState;
    }

    public String getNotBeforePolicy() {
        return notBeforePolicy;
    }

    public void setNotBeforePolicy(String notBeforePolicy) {
        this.notBeforePolicy = notBeforePolicy;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public long getRefreshRxpiresIn() {
        return refreshRxpiresIn;
    }

    public void setRefreshRxpiresIn(long refreshRxpiresIn) {
        this.refreshRxpiresIn = refreshRxpiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
