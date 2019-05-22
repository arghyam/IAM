package com.arghyam.backend.utils;


import org.keycloak.RSATokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.exceptions.TokenNotActiveException;
import org.keycloak.exceptions.TokenSignatureInvalidException;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeycloakUtil {

    protected static Logger logger = LoggerFactory.getLogger(KeycloakUtil.class);
    private static String publicKeyString = System.getenv("sso_publickey");
    //  private static String publicKeyString="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnPrKlAPXn57QWKwC9Xl5HSRBYPPdgN3RwrCKQN/qqnUVN6VvygohaI07kqg+NX46z0ksnA2C94GkWsQlz7OoMg3SljUr2FVBUw1UsSHcSH0IaGKGXmE9lVA9IpVSFXD51ZYOLOH7s0/wIR8iYWenh/BwgOaICC6uZdvZ3DUEqcErIhbkuN8cAGXyERRBkt32JBOXAyl3A6VphHvRkYpgHz8EdhwCcff7B6CwL/4M3P9PcbzceflKSw9f7i1ME3vF2MSdawmS7uJyMLKjdV8Q1VGGKyFfeG13PHhOMjGgDaASdORPWmiTTGF3KfurjmYYfc6pd3lE+0jk/+p9sGJ7qQIDAQAB";
    public static String fetchEmailIdFromToken(String accessToken, String baseUrl, String realm) throws VerificationException {
        try {
            PublicKey publicKey = toPublicKey(publicKeyString);
            AccessToken token = RSATokenVerifier.verifyToken(accessToken, publicKey, baseUrl + "realms/" + realm);
            return token.getEmail();
        }
        catch (TokenSignatureInvalidException exception){
            logger.error("Sinature of  access token is improper. Missed some content of Access Token : {}", exception.getLocalizedMessage());
            throw new TokenSignatureInvalidException(exception.getToken(), exception.getCause());

        }

        catch(TokenNotActiveException e) {
            logger.error("Inactive access token. Please try with fresh  access token : {}", e.getLocalizedMessage());
            throw new TokenNotActiveException(e.getToken(), e.getCause());

        }
        catch(VerificationException e) {
            logger.error("Invalid access token. Please verify the access token : {}", e.getLocalizedMessage());
            throw new VerificationException();
        }
    }

    public static String fetchUserIdFromToken(String accessToken, String baseUrl, String realm) throws VerificationException {
        try {
            PublicKey publicKey = toPublicKey(publicKeyString);
            AccessToken token = RSATokenVerifier.verifyToken(accessToken, publicKey, baseUrl + "realms/" + realm);
            return token.getSubject();
        }
        catch(VerificationException e) {
            logger.error("Invalid access token. Please verify the access token : {}", e.getLocalizedMessage());
            throw new VerificationException();
        }
    }

    public static String checkValidityOfToken(String accessToken, String baseUrl, String realm) throws VerificationException {
        try {
            PublicKey publicKey = toPublicKey(publicKeyString);
            AccessToken token = RSATokenVerifier.verifyToken(accessToken, publicKey, baseUrl + "realms/" + realm);
            return String.valueOf(token.getExpiration());
        }
        catch(VerificationException e) {
            logger.error("Invalid access token. Please verify the access token : {}", e.getLocalizedMessage());
            throw new VerificationException();
        }
    }

    private static PublicKey toPublicKey(String publicKeyString){
        try{
            byte[] bytes = Base64.getDecoder().decode(publicKeyString);
            X509EncodedKeySpec keySpecification = new X509EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpecification);
        }
        catch(Exception e){
            logger.error("Error Creating public key");
            return null;
        }
    }
}
