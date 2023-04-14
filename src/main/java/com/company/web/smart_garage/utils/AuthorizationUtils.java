package com.company.web.smart_garage.utils;


import com.company.web.smart_garage.models.User;

public class AuthorizationUtils {


    public static boolean userIsDeleted(User user) {
        return user.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("deleted"));
    }


//    public static <T extends Creatable> boolean userIsEntityCreator(T obj, User user) {
//        return obj.getCreatedBy().equals(user);
//    }

    public static boolean userIsAdmin(User user) {
        return user.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("admin"));
    }

    public static boolean userIsEmployee(User user) {
        return user.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("employee"));
    }
}
