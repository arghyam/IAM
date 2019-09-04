package org.forwater.backend.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.forwater.backend.dao.KeycloakDAO;
import org.forwater.backend.dao.RegistryDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
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
                .username(appContext.getAdminUserName())
                .password(appContext.getAdminUserpassword())
                .grantType(OAuth2Constants.PASSWORD)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();

        return keycloak;
    }



    @Bean
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Arghyam API(s)")
                .description("Provides management for arghyam api(s)")
                .build();
    }



    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/api/**"))
                .build();
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

    /*@Bean
    public MapMyIndiaDAO getMapMyIndiaInstance(){
        return new MapMyIndiaDAO();
    }*/

}
