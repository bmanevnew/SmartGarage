package com.company.web.smart_garage.utils;

import static com.company.web.smart_garage.utils.PasswordUtility.MAX_PASSWORD_LENGTH;
import static com.company.web.smart_garage.utils.PasswordUtility.MIN_PASSWORD_LENGTH;

public class Constants {

    //users
    public final static String USERNAME_IS_REQUIRED = "Username is a required field.";
    public final static String PASSWORD_IS_REQUIRED = "Password is a required field.";
    public final static String CONFIRM_PASSWORD_IS_REQUIRED = "Confirm password is a required field.";
    public final static String EMAIL_IS_REQUIRED = "Email is a required field.";
    public final static String PHONE_NUMBER_IS_REQUIRED = "Phone number is a required field.";
    public final static String FIRST_NAME_INVALID_SIZE = "First name must be between 2 and 20 symbols.";
    public final static String LAST_NAME_INVALID_SIZE = "Last name must be between 2 and 20 symbols.";
    public final static String PHONE_NUMBER_INVALID = "Phone number must be a valid phone number.";
    public static final String USER_EMAIL_INVALID = "This is not a valid email";
    public static final String USER_PHONE_INVALID = "This is not a valid phone number";
    public static final String USER_IS_ALREADY_EMPLOYEE = "This user is already an employee";
    public static final String USER_IS_ALREADY_ADMIN = "This user is already admin";
    public static final String USER_IS_NOT_EMPLOYED = "This user is not employed.";
    public static final String USER_IS_NOT_ADMIN = "This user is not an admin.";
    public static final String INVALID_LOGIN_ERROR = "Invalid username or password.";
    private static final String INVALID_AUTHENTICATION_ERROR = "Invalid authentication.";
    public static final String USER_WITH_EMAIL_S_ALREADY_EXISTS = "Email already registered";
    public static final String USER_WITH_PHONE_NUMBER_S_ALREADY_EXISTS = "Phone number is already registered";
    public static final String USER_WITH_EMAIL_S_DOES_NOT_EXIST = "User with email %s does not exist.";
    public static final String PASSWORD_DOES_NOT_MATCH = "Password does not match.";
    public static final String PASSWORD_TOO_WEAK = "Password too weak. Must include one lowercase character," +
            " one uppercase character, one digit and one special symbol. Length should be between " +
            MIN_PASSWORD_LENGTH + " and " + MAX_PASSWORD_LENGTH + " symbols.";
    public static final String FIRST_NAME_EMPTY = "First name can not be empty.";
    public static final String FIRST_NAME_LENGTH_INVALID = "First name should be between 4 and 32 symbols.";
    public static final String LAST_NAME_EMPTY = "Last name can not be empty.";
    public static final String LAST_NAME_LENGTH_INVALID = "Last name should be between 4 and 32 symbols.";
    public static final String EMAIL_EMPTY = "Email can not be empty.";
    public static final String EMAIL_INVALID = "Email should be a valid email.";
    public static final String PHONE_NUMBER_EMPTY = "Phone number can not be empty.";
    public static final String PHONE_NUMBER_LENGTH_INVALID = "Phone Number should be 10 symbols.";
    public static final String PASSWORD_LENGTH_INVALID = "Your password must be between 8 and 32 symbols";
    public static final String PHONE_NUMBER_REGEX = "^\\d{10}$";
    public static final String CURRENT_PASSWORD_BLANK = "Please enter your current password.";
    public static final String NEW_PASSWORD_BLANK = "Please enter your new password.";
    public static final String CONFIRM_PASSWORD_BLANK = "Please confirm your new password.";


    //roles
    public static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";


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
    public static final String INVALID_OWNER = "Invalid owner.";
    public static final int MIN_PROD_YEAR = 1886;
    public static final String VEHICLE_LICENSE_PLATE_REGEX =
            "^(E|A|B|BT|BH|BP|EB|TX|K|KH|OB|M|PA|PK|EH|PB|PP|P|CC|CH|CM|CO|C|CA|CB|CT|T|X|H|Y) \\d{4} [ABEKMHOPCTYX]{2}$";
    public static final String VEHICLE_VIN_REGEX = "^[A-Z\\d]{17}$";


    //visits
    public static final String USER_ID_IS_REQUIRED = "User id is a required field.";
    public static final String USER_ID_MUST_BE_POSITIVE = "User id must be positive.";
    public static final String VEHICLE_ID_IS_REQUIRED = "Vehicle id is a required field.";
    public static final String VEHICLE_ID_MUST_BE_POSITIVE = "Vehicle id must be positive.";
    public static final String VISIT_DATE_IN_FUTURE = "Date cannot be in the future.";
    public static final String VISIT_DATE_INTERVAL_INVALID = "Date interval is invalid.";
    public static final String VISIT_ALREADY_HAS_REPAIR_FORMAT = "Visit %d already has repair %d.";
    public static final String VISIT_HAS_NO_REPAIR_FORMAT = "Visit %d has no repair %d.";


    //repairs
    public static final String NAME_IS_REQUIRED = "Name is a required field.";
    public static final String REPAIR_NAME_INVALID_SIZE = "Repair name must be between 2 and 32 symbols.";
    public static final String REPAIR_WITH_NAME_S_ALREADY_EXISTS = "Repair with name %s already exists.";
    public static final String PRICE_IS_REQUIRED = "Price is a required field.";
    public static final String PRICE_IS_INVALID = "Price is invalid.";
    public static final String PRICE_INTERVAL_IS_INVALID = "Price interval is invalid.";
    public static final String FILTER_NAME = "deletedRepairFilter";
    public static final String FILTER_PARAM = "isDeleted";


    //JWT
    public static final String TOKEN_INVALID = "Invalid JWT token.";
    public static final String TOKEN_EXPIRED = "Expired JWT token.";
    public static final String TOKEN_UNSUPPORTED = "Unsupported JWT token.";
    public static final String TOKEN_EMPTY = "JWT claims string is empty.";
    public static final String JWT_COOKIE_NAME = "JwtCookie";


    //email
    public static final String DATE_FORMAT_MS = "yyyy-MM-dd:hh:mm:ss";
    public static final String FILE_NAME = "visit_%d.pdf";
    public static final String REPORT_EMAIL_BODY_FORMAT = "Please find attached the report for visit #%d.";
    public static final String REPORT_EMAIL_SUBJECT_FORMAT = "Visit Report #%d";
    public static final String SUBJECT_INVALID_SIZE = "Subject must be between 2 and 32 symbols.";
    public static final String MESSAGE_INVALID_SIZE = "Message must be between 2 and 1000 symbols.";
    public static final String CONTACT_EMAIL_FORMAT = "Name: %s\nContact email: %s\n\n%s";
    public static final String PASSWORD_RESET_SUBJECT = "Password reset for Smart Garage.";
    public static final String PASSWORD_RESET_BODY_FORMAT = "Dear %s,\n\nUse the following url to reset your password:" +
            " %s\n\nBest regards,\nThe Smart Garage Team";


    //general
    public static final String SORT_PROPERTY_S_IS_INVALID = "Sort property %s is invalid.";
    public static final String ID_MUST_BE_POSITIVE = "Id must be positive.";
    public static final String ID_INVALID = "Id is invalid.";
    public static final String PAGE_IS_INVALID = "Page number is invalid.";
    public static final String HASH_ALGORITHM_NOT_FOUND = "Hashing algorithm not found.";
    public static final String SUCCESSFUL_RESET = "Successfully sent reset link.";
    public static final String PASSWORD_SUCCESSFULLY_CHANGED = "You have successfully changed your password.";
    public static final String REQUIRED_FIELD = "Required field.";
    public static final String CURRENCY_NOT_SUPPORTED = "Currency not supported.";
    public static final String SHA_256 = "SHA-256";
    public static final String NOT_ARCHIVED = "is_archived = false";


    //authentication mvc
    public static final String CONFIRM_PASSWORD_FIELD = "confirmNewPassword";
    public static final String PASSWORD_ERROR = "password_error";
    public static final String AUTH_ERROR = "auth_error";
    public static final String PASSWORD_FIELD = "password";
    public static final String CHANGE_PASSWORD_ENDPOINT = "/auth/change_password";
    public static final String SAME_SITE = "SameSite";
    public static final String SAME_SITE_LAX = "lax";
    public static final String ROOT_PATH = "/";
    public static final String LOGIN_DTO_KEY = "login";
    public static final String LOGIN_DTO_EMAIL_KEY = "loginEmail";
    public static final String PASSWORD_DTO_KEY = "passwordDto";
    public static final String TOKEN_KEY = "token";
    public static final String RESPONSE_KEY = "response";
    public static final String CONTACT_DTO_KEY = "contactDto";

    //error handling mvc
    public static final String ERROR_MESSAGE_KEY = "errorMessage";
    public static final String HTTP_CODE_KEY = "httpCode";
    public static final String NOT_FOUND_HEADING = "404 Not Found";
    public static final String UNAUTHORIZED_HEADING = "401 Unauthorized";


    //repairs mvc
    public static final String REPAIR_FILTER_OPTIONS = "repairFilterOptions";
    public static final String REPAIRS_KEY = "repairs";
    public static final String PARAM_ERROR = "paramError";
    public static final String INVALID_PRICE = "Invalid price input.";
    public static final String REPAIR_DTO_CREATE = "repairDtoCreate";
    public static final String REPAIR_CREATE_VIEW = "repairCreate";
    public static final String REPAIR_UPDATE_VIEW = "repairUpdate";
    public static final String DUPLICATE_NAME = "duplicate_name";
    public static final String REPAIR_DTO_KEY = "repairDto";
    public static final String PRICE_ERROR = "price_error";
    public static final String PRICE_FIELD = "price";
    public static final String NAME_FIELD = "name";

    //vehicles mvc
    public static final String ACCESS_DENIED = "Access denied.";
    public static final String VEHICLE_KEY = "vehicle";
    public static final String VEHICLE_DTO_CREATE = "vehicleDtoCreate";
    public static final String OWNER_INVALID = "owner_invalid";
    public static final String OWNER_USERNAME_OR_EMAIL_FIELD = "ownerUsernameOrEmail";
    public static final String PRODUCTION_YEAR_FIELD = "productionYear";
    public static final String PROD_YEAR_INVALID = "prod_year_invalid";
    public static final String PROD_YEAR_INVALID_MESSAGE = "Production year is invalid.";
    public static final String VEHICLE_DTO = "vehicleDto";
    public static final String INVALID_YEAR = "Invalid year input.";
    public static final String VEHICLES_KEY = "vehicles";
    public static final String VEHICLE_FILTER_OPTIONS = "vehicleFilterOptions";

    //VISITS MVC
    public static final String VISIT_KEY = "visit";
    public static final String CURRENCY_KEY = "currency";
    public static final String VISIT_FILTER_OPTIONS = "visitFilterOptions";
    public static final String INVALID_DATE = "Invalid date input.";
    public static final String VISITS_KEY = "visits";
    public static final String VISIT_DTO = "visitDto";
    public static final String VISITOR_FIELD = "visitor";
    public static final String VEHICLE_FIELD = "vehicle";
    public static final String VISITOR_INVALID = "visitor_invalid";
    public static final String VEHICLE_INVALID = "vehicle_invalid";
    public static final String SUCCESSFULLY_SENT_PDF = "Successfully sent pdf.";
    public static final String FILE_NAME_FORMAT = "visit_pdf_%s";
    public static final String CACHE_CONTROL = "must-revalidate, post-check=0, pre-check=0";
    public static final String ALL_REPAIRS_KEY = "allRepairs";
    public static final String ADD_REPAIR_KEY = "addRepair";
    public static final String DATE_FORMAT = "MM/dd/yyyy";

    //mvc general
    public static final String ERROR_VIEW = "error";
}
