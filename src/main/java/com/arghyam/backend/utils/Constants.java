package com.arghyam.backend.utils;

public class Constants {

    public static final String OPENID_CLIENT_CREDENTIALS = "client_credentials";

    public static final String KEYCLOAK_REGISTER_API = "admin/realms/{realm}/users";

    public static final String LOGIN_API = "realms/{realm}/protocol/openid-connect/token";

    public static final String GET_USER_API = "/admin/realms/{realm}/users/{id}";

    public static final String LOGOUT_API = "/auth/realms/{realm}/protocol/openid-connect/logout";

    public static final String USER_LOGOUT_API = "/admin/realms/{realm}/users/{id}/logout";

    public static final String SEARCH_USERS = "admin/realms/{realm}/users";

    public static final String UPDATE_USER = "admin/realms/{realm}/users/{id}";

    public static final String X_HEADER__TOKEN_ID = "X-HEADER-TOKEN-ID";

    public static final String ROLE = "ROLE";

    public static final String URI = "URI";

    public static final String FIELD_INVALID = " is invalid";

    public static final String OPENID_SCOPE_VALUE = "openid profile";

    public static final String PHONE_NUMBER = "phoneNumber";

    public static final String KEYCLOAK_EMAIL_CODE = "EMAIL_CODE";

    public static final String MMJP_USER = "MMJP_USER";

    public static final String CACHE_ACCESS_TOKEN = "accessToken";

    public static final String GENERATE_ACCESS_TOKEN = "realms/{realm}/protocol/openid-connect/token";

    public static final String USER_DOES_NOT_EXIST = "User does not exist with this username";

    public static final String INVALID_PASSWORD = "Invalid Password";

    public static final String REGISRY_ADD_USER = "add";

    public static final String PASSWORD = "password";

    public static final String REFRESH_TOKEN = "refresh_token";



}
