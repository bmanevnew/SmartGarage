package com.company.web.smart_garage.services;

import com.company.web.smart_garage.Helpers;
import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.models.Role;
import com.company.web.smart_garage.repositories.RoleRepository;
import com.company.web.smart_garage.services.impl.RoleServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.company.web.smart_garage.Helpers.*;
import static org.mockito.Mockito.when;

public class RoleServiceTests {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService = new RoleServiceImpl(roleRepository);

    @BeforeEach
    public void setup() {

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAll() {
        Role customer = createMockDefaultRole();
        Role employee = createMockEmployeeRole();
        Role admin = createMockAdminRole();
        List<Role> roles = Arrays.asList(customer, employee, admin);

        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> result = roleService.getAll();

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(customer, result.get(0));
        Assertions.assertEquals(employee, result.get(1));
        Assertions.assertEquals(admin, result.get(2));
    }

    @Test
    public void testGetById() {
        Role customer = createMockDefaultRole();
        long id = 1L;

        when(roleRepository.findById(id)).thenReturn(Optional.of(customer));

        Role result = roleService.getById(id);

        Assertions.assertEquals(customer, result);
    }

    @Test
    public void testGetByIdWithNonExistingId() {
        long id = 1L;

        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            roleService.getById(id);
        });
    }

    @Test
    public void testGetByName() {
        Role customer = createMockDefaultRole();
        String name = "ROLE_CUSTOMER";

        when(roleRepository.findByName(name)).thenReturn(Optional.of(customer));

        Role result = roleService.getByName(name);

        Assertions.assertEquals(customer, result);
    }

    @Test
    public void testGetByNameWithNonExistingName() {
        String name = "ROLE_NON_EXISTING";

        when(roleRepository.findByName(name)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            roleService.getByName(name);
        });
    }
}
