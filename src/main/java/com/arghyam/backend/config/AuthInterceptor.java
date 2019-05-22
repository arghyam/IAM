//package com.arghyam.backend.config;
//
//
//import com.arghyam.backend.exceptions.UnAuthenticatedException;
//import com.arghyam.backend.utils.Constants;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import org.jose4j.jwt.JwtClaims;
//import org.jose4j.jwt.consumer.JwtConsumer;
//import org.jose4j.jwt.consumer.JwtConsumerBuilder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.security.PublicKey;
//
//@Component
//public class AuthInterceptor extends HandlerInterceptorAdapter {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(AuthInterceptor.class);
//
//    @Autowired
//    PublicKeyBeanConfig pbKey;
//
//    public static final String AUTH_HEADER = "Authorization";
//
//    @Override
//    public boolean preHandle(HttpServletRequest request,
//                             HttpServletResponse response, Object handler) throws Exception {
//
//
//        try {
//            String authHeader = request.getHeader(AUTH_HEADER);
//            String[] tokens = authHeader.split(" ");
//
//            if (tokens.length != 2) {
//                LOGGER.info("Invalid Token");
//                throw new RuntimeException("invalid token");
//            }
//
//            PublicKey publicKey = pbKey.publicKeybean();
//            String token = tokens[1];
//            JwtConsumer jwtConsumer = new JwtConsumerBuilder()
//                    .setVerificationKey(publicKey)
//                    .setExpectedAudience("account")
//                    .setRequireExpirationTime()
//                    .build();
//
//            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
//
//            String rawJson = jwtClaims.getRawJson();
//
//            JsonParser parser = new JsonParser();
//            JsonObject json = (JsonObject) parser.parse(rawJson);
//
//            String role = (json.getAsJsonObject("realm_access").getAsJsonObject().get("roles").getAsJsonArray().get(0)).getAsString();
//            String URI = request.getRequestURI();
//            String userId = json.get("sub").getAsString();
//
//
//            if (userId != null && role != null && URI != null) {
//                request.setAttribute(Constants.ROLE, role);
//                request.setAttribute(Constants.URI, URI);
//                request.setAttribute(Constants.X_HEADER__TOKEN_ID, userId);
//
//                return true;
//            }
//        } catch (Exception e) {
//            LOGGER.error("Invalid Token",e);
//            throw new UnAuthenticatedException("Invalid Token");
//        }
//        return false;
//    }
//}
