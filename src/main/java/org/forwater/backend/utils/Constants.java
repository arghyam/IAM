package org.forwater.backend.utils;

public class Constants {

    public static final String OPENID_CLIENT_CREDENTIALS = "client_credentials";

    public static final String KEYCLOAK_REGISTER_API = "admin/realms/{realm}/users";

    public static final String LOGIN_API = "realms/{realm}/protocol/openid-connect/token";

    public static final String GET_USER_API = "/admin/realms/{realm}/users/{id}";

    public static final String LOGOUT_API = "/auth/realms/{realm}/protocol/openid-connect/logout";

    public static final String USER_LOGOUT_API = "/admin/realms/{realm}/users/{id}/logout";

    public static final String SEARCH_USERS = "admin/realms/{realm}/users";

    public static final String SEARCH_USER_BY_ID = "admin/realms/{realm}/users/{id}";

    public static final String GET_USERS_BASED_ON_ROLE_NAME = "admin/realms/{realm}/roles/{role_name}/users";

    public static final String GET_ROLES_FROM_USERID = "admin/realms/{realm}/users/{id}/role-mappings/realm";

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

    public static final String REGISRY_SEARCH_USER = "search";

    public static final String REGISRY_DELETE_ENTRY = "delete";

    public static final String REG_ENTRY_CREATED = "registry_entry_created";

    public static final String REGISTRY_UPDATE_USER = "update";

    public static final String REGISTRY_READ_USER = "read/{id}";

    public static final String PASSWORD = "password";

    public static final String REFRESH_TOKEN = "refresh_token";

    public static final String USER_CREATEDIN_REGISTRY = "You successfully created user in registry";

    public static final String ERROR_WHILE_CREATING_USER = "Failed to create user in registry";

    public static final String ARGHYAM_S3_FOLDER_LOCATION = "arghyam/";

    public static final String NOTIFICATION_DISCHARGE="Spring discharge data was submitted by ";

    public static final String NOTIFICATION_ACCEPTED="Spring discharge data was approved by ";

    public static final String NOTIFICATION_REJECTED="Spring discharge data was rejected by ";

    public static final String IMAGE_UPLOAD_SUCCESS_MESSAGE="Uploaded successfully";

    public static final String ARGHYAM_USERS="arghyam-user";

    public static final String MAP_MY_INDIA_LICENCE="286bdsg9s3n89e7fqgfb9m1grv4bqpar";

    public static final String MAP_MY_INDIA_CODE="rev_geocode";

    public static final String REVERSE_GEO_CODING_API="https://apis.mapmyindia.com/advancedmaps/v1/";

    public static final String ID_SEARCH_STATE = "org.forwater.create";

    public static final String ADD_ROLE= "{realm}/groups/{id}/role-mappings/realm";


}
