package com.company.web.smart_garage;

import com.company.web.smart_garage.models.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Helpers {

    public static LocalDateTime dateTime = LocalDateTime.of(2021, 4, 23, 22, 11, 34);

    public static User createMockUser() {
        User mockUser = new User();
        mockUser.setUsername("Username");
        mockUser.setPassword("Password1234");
        mockUser.setEmail("FirstName_LastName@email.abc");
        mockUser.setPhoneNumber("0888111111");
        Set<Role> mockRoles = new HashSet<>();
        mockRoles.add(createMockDefaultRole());
        mockUser.setRoles(mockRoles);
        return mockUser;
    }

    public static User createMockEmployee() {
        User mockEmployee = new User();
        mockEmployee.setUsername("EmployeeUsername");
        mockEmployee.setPassword("EmployeePassword1234");
        mockEmployee.setEmail("Employee_FirstName_LastName@email.abc");
        mockEmployee.setPhoneNumber("0888222222");
        Set<Role> mockRoles = new HashSet<>();
        mockRoles.add(createMockEmployeeRole());
        mockEmployee.setRoles(mockRoles);
        return mockEmployee;
    }

    public static User createMockAdmin() {
        User mockAdmin = new User();
        mockAdmin.setUsername("AdminUsername");
        mockAdmin.setPassword("AdminPassword1234");
        mockAdmin.setEmail("Admin_FirstName_LastName@email.abc");
        mockAdmin.setPhoneNumber("0888333333");
        Set<Role> mockRoles = new HashSet<>();
        mockRoles.add(createMockAdminRole());
        mockAdmin.setRoles(mockRoles);
        return mockAdmin;
    }


    public static Role createMockDefaultRole() {
        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_CUSTOMER");
        return role;
    }

    public static Role createMockEmployeeRole() {
        Role role = new Role();
        role.setId(2);
        role.setName("ROLE_EMPLOYEE");
        return role;
    }

    public static Role createMockAdminRole() {
        Role role = new Role();
        role.setId(3);
        role.setName("ROLE_ADMIN");
        return role;
    }

    public static Visit createMockVisit() {
        Visit mockVisit = new Visit();
        mockVisit.setVisitor(createMockUser());
        mockVisit.setVehicle(createMockVehicle());
        mockVisit.setDate(dateTime);
        return mockVisit;
    }

    public static Vehicle createMockVehicle() {
        Vehicle mockVehicle = new Vehicle();
        mockVehicle.setLicensePlate("CA 2131 KH");
        mockVehicle.setVin("5YJSA1DG9DFP14705");
        mockVehicle.setOwner(createMockUser());
        return mockVehicle;
    }

    public static Repair createMockRepair() {
        Repair repair = new Repair();
        repair.setName("Oil change");
        repair.setPrice(29.99);
        return repair;
    }
}
