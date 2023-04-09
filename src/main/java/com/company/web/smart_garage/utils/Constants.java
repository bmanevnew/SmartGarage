package com.company.web.smart_garage.utils;

public class Constants {
    public final static String USERNAME_INVALID_SIZE_MESSAGE = "Username must be between 2 and 20 symbols.";
    public final static String EMAIL_INVALID_MESSAGE = "Email must be a valid email.";
    public final static String PHONE_NUMBER_INVALID_MESSAGE = "Phone number must be a valid phone number.";
    public static final String DELETED_NOT_EMPLOYEES_OR_NOT_ADMINS_ERROR_MESSAGE =
            "Deleted users, not admins or not employees  can not modify a entity.";
    public static final String NOT_EMPLOYEES_OR_ADMINS_ERROR_MESSAGE = "Only employees or admin can perform this action.";

    public static final String MODIFY_ENTITY_ERROR_MESSAGE = "Only admin or entity creator can modify a entity.";
    public static final String NOT_ADMIN_ERROR_MESSAGE = "Only admins can perform this action.";
    public static final String USER_IS_ALREADY_DELETED = "This user is already deleted";
    public static final String DELETED = "deleted";
    public static final String USER_IS_NOT_EMPLOYED = "This user is not employed.";
    public static final String USER_IS_NOT_ADMIN = "This user is not an admin.";

    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    public static final String INVALID_AUTHENTICATION_ERROR = "Invalid authentication.";
    public static final String INVALID_LOGIN_ERROR = "Invalid username or password.";
    public static final String USERNAME_DELETED_IS_INVALID = "Username " + DELETED + " cannot be used.";
}
