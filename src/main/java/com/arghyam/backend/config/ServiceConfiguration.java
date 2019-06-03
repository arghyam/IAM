package com.arghyam.backend.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.arghyam.backend.dao.KeycloakDAO;
import com.arghyam.backend.dao.RegistryDAO;
import com.arghyam.backend.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class ServiceConfiguration {

    @Autowired
    AppContext appContext;

    @Bean
    public KeycloakDAO getKeycloak() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(appContext.getKeyCloakServiceUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit.create(KeycloakDAO.class);

    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }


    @Bean
    public Keycloak getKeycloakClient() {
        Keycloak keycloak = KeycloakBuilder
                .builder()
                .serverUrl(appContext.getKeyCloakServiceUrl())
                .clientId(appContext.getClientId())
                .clientSecret(appContext.getClientSecret())
                .realm(appContext.getRealm())
                .grantType(Constants.OPENID_CLIENT_CREDENTIALS)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();

        return keycloak;
    }

    @Bean
    public RegistryDAO getRegistryDao() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(appContext.getRegistryBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit.create(RegistryDAO.class);
    }

    @Bean
    public AmazonS3 buildAmazonS3(){
        AWSCredentials awsCredentials = new BasicAWSCredentials(appContext.getAccessKey(), appContext.getSecretKey());

        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.AP_SOUTH_1)
                .build();
        return amazonS3;
    }
}
