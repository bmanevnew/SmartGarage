package com.company.web.smart_garage.utils;

import static com.company.web.smart_garage.utils.PasswordUtility.MAX_PASSWORD_LENGTH;
import static com.company.web.smart_garage.utils.PasswordUtility.MIN_PASSWORD_LENGTH;

public class Constants {

    //users
    public final static String EMAIL_IS_REQUIRED = "Email is a required field.";
    public final static String PHONE_NUMBER_IS_REQUIRED = "Phone number is a required field.";
    public final static String FIRST_NAME_INVALID_SIZE_MESSAGE = "First name must be between 2 and 20 symbols.";
    public final static String LAST_NAME_INVALID_SIZE_MESSAGE = "Last name must be between 2 and 20 symbols.";
    public final static String USERNAME_INVALID_SIZE_MESSAGE = "Username must be between 2 and 20 symbols.";
    public final static String EMAIL_INVALID_MESSAGE = "Email must be a valid email.";
    public final static String USERNAME_IS_REQUIRED = "Username is a required field.";
    public final static String PASSWORD_IS_REQUIRED = "Password is a required field.";
    public final static String CONFIRM_PASSWORD_IS_REQUIRED = "Confirm password is a required field.";
    public final static String PHONE_NUMBER_INVALID_MESSAGE = "Phone number must be a valid phone number.";
    public static final String NOT_ADMIN_ERROR_MESSAGE = "Only admins can perform this action.";
    public static final String USER_IS_ALREADY_EMPLOYEE = "This user is already an employee";
    public static final String USER_IS_ALREADY_ADMIN = "This user is already admin";
    public static final String USER_IS_NOT_EMPLOYED = "This user is not employed.";
    public static final String USER_IS_NOT_ADMIN = "This user is not an admin.";
    public static final String INVALID_LOGIN_ERROR = "Invalid username or password.";
    public static final String USER_EMAIL_INVALID = "This is not a valid email";
    public static final String USER_PHONE_INVALID = "This is not a valid phone number";
    public static final String USER_WITH_EMAIL_S_ALREADY_EXISTS = "User with email %s already exists.";
    public static final String USER_WITH_PHONE_NUMBER_S_ALREADY_EXISTS = "User with phone number %s already exists.";
    public static final String PASSWORD_TOO_WEAK = "Password too weak. Must include one lowercase character," +
            " one uppercase character, one digit and one special symbol. Length should be between " +
            MIN_PASSWORD_LENGTH + " and " + MAX_PASSWORD_LENGTH + " symbols.";


    //vehicles
    public static final String VEHICLE_PLATE_REQUIRED = "Vehicle license plate is a required field.";
    public static final String VEHICLE_VIN_REQUIRED = "Vehicle vin number is a required field.";
    public static final String VEHICLE_PROD_YEAR_REQUIRED = "Vehicle production year is a required field.";
    public static final String VEHICLE_MODEL_REQUIRED = "Vehicle model is a required field.";
    public static final String VEHICLE_BRAND_REQUIRED = "Vehicle brand is a required field.";
    public static final String VEHICLE_PLATE_INVALID_FORMAT = "Vehicle license plate must be in the format " +
            "\"X(X) NNNN YY\".";
    public static final String VEHICLE_VIN_INVALID_FORMAT = "Vehicle vin number must consist of 17 character/digits.";
    public static final String VEHICLE_MODEL_INVALID_SIZE = "Vehicle model must be between 2 and 32 symbols.";
    public static final String VEHICLE_BRAND_INVALID_SIZE = "Vehicle brand must be between 2 and 32 symbols.";
    public static final String VEHICLE_PROD_YEAR_INVALID = "Production year must be between 1886 and the present year.";
    public static final String VEHICLE_PROD_YEAR_INTERVAL_INVALID = "Production year interval is invalid.";
    public static final String VEHICLE_PLATE_INVALID_AREA_CODE = "License plate area code is invalid.";
    public static final String VEHICLE_VALID_AREA_CODES = "E,A,B,BT,BH,BP,EB,TX,K,KH,OB,M,PA,PK,EH,PB,PP,P,CC,CH,CM,CO," +
            "C,CA,CB,CT,T,X,H,Y";


    //visits
    public static final String USER_ID_IS_REQUIRED = "User id is a required field.";
    public static final String USER_ID_MUST_BE_POSITIVE = "User id must be positive.";
    public static final String VEHICLE_ID_IS_REQUIRED = "Vehicle id is a required field.";
    public static final String VEHICLE_ID_MUST_BE_POSITIVE = "Vehicle id must be positive.";
    public static final String VISIT_DATE_IN_FUTURE = "Date cannot be in the future.";
    public static final String VISIT_DATE_INTERVAL_INVALID = "Date interval is invalid.";


    //repairs
    public static final String REPAIR_NAME_INVALID_SIZE = "Repair name must be between 2 and 32 symbols.";
    public static final String PRICE_IS_INVALID = "Price is invalid.";
    public static final String PRICE_IS_REQUIRED = "Price is a required field.";
    public static final String NAME_IS_REQUIRED = "Name is a required field.";
    public static final String PRICE_INTERVAL_IS_INVALID = "Price interval is invalid.";
    public static final String REPAIR_WITH_NAME_S_ALREADY_EXISTS = "Repair with name %s already exists.";

    //general
    public static final String SORT_PROPERTY_S_IS_INVALID = "Sort property %s is invalid.";
    public static final String ID_MUST_BE_POSITIVE = "Id must be positive.";
    public static final String PAGE_IS_INVALID = "Page number is invalid.";
    public static final String TOKEN_EXPIRED = "Token has expired.";
    public static final String HASH_ALGORITHM_NOT_FOUND = "Hashing algorithm not found.";
    public static final String JWT_COOKIE_NAME = "JwtCookie";
}
