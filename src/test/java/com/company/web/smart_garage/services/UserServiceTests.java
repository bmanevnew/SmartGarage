package com.company.web.smart_garage.services;

import com.company.web.smart_garage.exceptions.EntityDuplicationException;
import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.User;
import com.company.web.smart_garage.models.Vehicle;
import com.company.web.smart_garage.models.Visit;
import com.company.web.smart_garage.repositories.UserRepository;
import com.company.web.smart_garage.services.impl.EmailSenderServiceImpl;
import com.company.web.smart_garage.services.impl.UserServiceImpl;
import jakarta.persistence.EntityManager;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.company.web.smart_garage.Helpers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {


    @Mock
    private UserRepository mockRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleService roleService;
    @Mock
    private EmailSenderServiceImpl emailSenderService;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;
    private Vehicle mockVehicle;
    Pageable pageable;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    private User user;
    private User mockAdmin;
    private Visit mockVisit;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("0000000000");
        mockUser = createMockUser();
        mockAdmin = createMockAdmin();
        mockVehicle = createMockVehicle();
        mockVisit = createMockVisit();
        dateFrom = dateTime.toLocalDate().minusDays(7);
        dateTo = dateTime.toLocalDate();
        pageable = PageRequest.of(0, 20);
    }

    @Test
    void getById_Should_CallRepository() {
        mockUser = createMockUser();
        when(mockRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockUser));

        userService.getById(1L);

        verify(mockRepository, times(1)).findById(1L);
    }

    @Test
    public void getById_Should_ReturnUser_When_MatchExists() {
        Mockito.when(mockRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(mockUser));

        User result = userService.getById(1);

        assertEquals(result, mockUser);
    }

    @Test
    public void getById_Should_ThrowException_When_UserDoesNotExist() {
        when(mockRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getById(1));
    }

    @Test
    public void getById_Should_ThrowException_When_IdIsNegativeOrZero() {
        assertThrows(InvalidParamException.class, () -> userService.getById(-5));
    }


    @Test
    public void getByUsername_Should_ReturnUser_When_MatchExists() {
        Mockito.when(mockRepository.findFirstByUsername("Username"))
                .thenReturn(Optional.ofNullable(mockUser));

        User result = userService.getByUsername("Username");

        assertEquals(result, mockUser);
    }

    @Test
    public void getByUsername_Should_ThrowException_When_UserDoesNotExist() {
        when(mockRepository.findFirstByUsername("Username1")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getByUsername("Username1"));
    }

    @Test
    public void getByEmail_Should_ReturnUser_When_MatchExists() {
        Mockito.when(mockRepository.findFirstByEmail("FirstName_LastName@email.abc"))
                .thenReturn(Optional.ofNullable(mockUser));

        User result = userService.getByEmail("FirstName_LastName@email.abc");

        assertEquals(result, mockUser);
    }

    @Test
    public void getByEmail_Should_ThrowException_When_UserDoesNotExist() {
        when(mockRepository.findFirstByEmail("FirstName_Las1tName@email.abc")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getByEmail("FirstName_Las1tName@email.abc"));
    }

    @Test
    public void getByUsernameOrEmail_Should_ReturnUser_When_MatchExists() {
        Mockito.when(mockRepository.findFirstByUsernameOrEmail(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.ofNullable(mockUser));

        User result = userService.getByUsernameOrEmail("FirstName_LastName@email.abc");

        assertEquals(result, mockUser);
    }

    @Test
    public void getByUsernameOrEmail_Should_ThrowException_When_UserDoesNotExist() {
        String usernameOrEmail = "nonexistentuser@example.com";
        when(mockRepository.findFirstByUsernameOrEmail(usernameOrEmail, usernameOrEmail))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService
                .getByUsernameOrEmail("nonexistentuser@example.com"));
    }

    @Test
    void validateEmail_Should_NotThrowException_When_ValidEmail() {
        assertDoesNotThrow(() -> userService.validateEmail(mockUser.getEmail()));
    }

    @Test
    void validateEmail_Should_ThrowException_When_InValidEmail() {
        assertThrows(InvalidParamException.class, () -> userService
                .validateEmail("john.doe@"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"john.doe", "john.doe@", "@example.com", "john.doe@example.", "john.doe@.com",
            "john.doe@example.c"})
    void validateEmail_Should_ThrowException_When_InvalidEmail(String invalidEmail) {

        InvalidParamException exception = assertThrows(InvalidParamException.class, () -> userService
                .validateEmail(invalidEmail));
        assertEquals("This is not a valid email", exception.getMessage());
    }

    @Test
    public void getByPhoneNumber_Should_ReturnUser_When_MatchExists() {
        Mockito.when(mockRepository.findFirstByPhoneNumber("0888111111"))
                .thenReturn(Optional.ofNullable(mockUser));

        User result = userService.getByPhoneNumber("0888111111");

        assertEquals(result, mockUser);
    }

    @Test
    public void validatePhoneNumber_Should_ThrowException_When_PhoneNumberIsInvalid() {
        String invalidPhoneNumber = "12345";
        assertThrows(InvalidParamException.class, () -> userService.validatePhoneNumber(invalidPhoneNumber));
    }

    @Test
    public void getFilteredUsers_Should_ReturnFilteredUsers_When_ValidInputs() {
        String name = "John";
        String email = "testemail@test.com";
        String vehicleModel = "Ram";
        String vehicleMake = "Dodge";
        LocalDate visitFromDate = dateTime.toLocalDate().minusDays(7);
        LocalDate visitToDate = dateTime.toLocalDate();
        Pageable pageable = PageRequest.of(0, 10);

        List<User> userList = new ArrayList<>();

        Vehicle mockVehicle = createMockVehicle();
        Visit mockVisit = createMockVisit();
        userList.add(mockUser);
        Page<User> expectedPage = new PageImpl<>(userList, pageable, 1);
        doReturn(expectedPage).when(mockRepository).findByFilters(name, email, vehicleModel,
                vehicleMake, Date.valueOf(visitFromDate), Date.valueOf(visitToDate)
                , pageable);

        Page<User> result = userService.getFilteredUsers(name, email, vehicleModel, vehicleMake, visitFromDate,
                visitToDate, pageable);
        assertEquals(expectedPage, result);
    }

    @Test
    public void getFilteredUsers_Should_ThrowException_When_PageIsInvalid() {
        String name = "John";
        String email = "testemail@test.com";
        String vehicleModel = "Ram";
        String vehicleMake = "Dodge";
        LocalDate visitFromDate = dateTime.toLocalDate().minusDays(7);
        LocalDate visitToDate = dateTime.toLocalDate();
        Pageable pageable = PageRequest.of(1, 10);

        List<User> userList = new ArrayList<>();
        Vehicle mockVehicle = createMockVehicle();
        Visit mockVisit = createMockVisit();
        Page<User> expectedPage = new PageImpl<>(userList, pageable, 1);
        doReturn(expectedPage).when(mockRepository).findByFilters(name, email, vehicleModel,
                vehicleMake, Date.valueOf(visitFromDate), Date.valueOf(visitToDate), pageable);

        assertThrows(InvalidParamException.class, () -> userService.getFilteredUsers(name, email, vehicleModel,
                vehicleMake, visitFromDate, visitToDate, pageable));
    }


    @Test
    public void getFilteredUsers_Should_ThrowException_When_SortPropertiesAreInvalid() {
        pageable = PageRequest.of(0, 10, Sort.by("invalidProperty"));

        assertThrows(InvalidParamException.class, () ->
                userService.getFilteredUsers(mockUser.getFirstName(), mockUser.getEmail(), mockVehicle.getBrand(),
                        mockVehicle.getModel(), dateFrom, dateTo, pageable));
    }

    @Test
    public void getFilteredUsers_Should_ThrowException_When_DateIntervalIsInvalid() {
        assertThrows(InvalidParamException.class, () ->
                userService.getFilteredUsers(mockUser.getFirstName(), mockUser.getEmail(), mockVehicle.getBrand(),
                        mockVehicle.getModel(), dateTo, dateFrom, pageable));
    }

    @Test
    public void getFilteredUsers_Should_ThrowException_When_DateIsInvalid() {
        assertThrows(InvalidParamException.class, () ->
                userService.getFilteredUsers(mockUser.getFirstName(), mockUser.getEmail(), mockVehicle.getBrand(),
                        mockVehicle.getModel(), dateFrom,
                        LocalDate.now().plusDays(1), pageable));
    }

    @Test
    public void create_Should_CallRepository_When_EverythingIsValid() {
        Session session = mock(Session.class);

        when(mockRepository.findFirstByPhoneNumber(Mockito.anyString())).thenReturn(Optional.empty());
        when(mockRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        when(roleService.getByName(anyString())).thenReturn(createMockDefaultRole());

        doNothing().when(emailSenderService).sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        User user = createMockUser(); // create a mock user to use in the test
        when(mockRepository.save(user)).thenReturn(user);

        UserServiceImpl userService = new UserServiceImpl(emailSenderService, mockRepository, roleService, passwordEncoder);

        User createdUser = userService.create(user);

        verify(emailSenderService, times(1)).sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        verify(mockRepository, times(1)).save(user); // verify that the user is saved correctly
        assertEquals(user, createdUser); // check that the returned user is equal to the original mock user
    }

//    @Test
//    public void create_Should_ThrowException_When_EmailAlreadyExists() {
//
//        User user1 = new User();
//        user1.setEmail("unique.email@example.com");
//
//        User user2 = new User();
//        user2.setEmail("duplicate.email@example.com");
//
//        when(mockRepository.findFirstByEmail(user2.getEmail())).thenReturn(Optional.of(user1));
//
//        assertThrows(EntityDuplicationException.class, () -> {
//            userService.create(user2);
//        });
//
//        verify(mockRepository).findFirstByEmail(user2.getEmail());
//    }

    @Test
    public void create_Should_Throw_When_PhoneExists() {
        User user1 = new User();
        user1.setEmail("unique.email@example.com");
        user1.setPhoneNumber("0000000000");

        User user2 = new User();
        user2.setEmail("uniqu2e.email@example.com");
        user2.setPhoneNumber("0000000000");


        when(mockRepository.findFirstByPhoneNumber(user2.getPhoneNumber())).thenReturn(Optional.of(user1));

        assertThrows(EntityDuplicationException.class, () -> {
            userService.create(user2);
        });

        verify(mockRepository).findFirstByPhoneNumber(user2.getPhoneNumber());
    }

    @Test
    public void delete_Should_DeleteUserAndReturnIt_When_UserExists() {
        mockUser = createMockUser();
        mockUser.setId(1L);
        when(mockRepository.findByIdFetchAll(mockUser.getId())).thenReturn(Optional.of(mockUser));

        User deletedUser = userService.delete(mockUser.getId());

        verify(mockRepository).deleteById(mockUser.getId());
        assertEquals(mockUser, deletedUser);
    }

    @Test
    public void delete_Should_Throw_When_UserDoesntExist() {
        when(mockRepository.findByIdFetchAll(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.delete(1234L);
        });

        verify(mockRepository).findByIdFetchAll(1234L);
    }

    @Test
    public void makeAdmin_Should_AddAdminRole_When_UserIsNotAdmin() {
        mockUser = createMockUser();
        mockUser.setId(1L);
        Mockito.when(mockRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(mockUser));
        Mockito.when(roleService.getById(3L)).thenReturn((createMockEmployeeRole()));

        userService.makeAdmin(1L);

        Mockito.verify(mockRepository, Mockito.times(1)).save(mockUser);
    }

    @Test
    public void makeNotAdmin_Should_RemoveAdminRole_When_UserIsAdmin() {
        mockUser = createMockAdmin();
        mockUser.setId(1L);
        Mockito.when(mockRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(mockUser));

        userService.makeNotAdmin(1L);

        Mockito.verify(mockRepository, Mockito.times(1)).save(mockUser);
    }

    @Test
    public void makeEmployee_Should_AddAdminRole_When_UserIsNotEmployee() {
        mockUser = createMockUser();
        mockUser.setId(1L);
        // createMockAdminRole();
        Mockito.when(mockRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(mockUser));
        Mockito.when(roleService.getById(2L)).thenReturn((createMockEmployeeRole()));
        userService.makeEmployee(1L);

        Mockito.verify(mockRepository, Mockito.times(1)).save(mockUser);
    }

    @Test
    public void makeUnemployed_Should_RemoveEmployeeRole_When_UserIsEmployee() {
        mockUser = createMockEmployee();
        mockUser.setId(1L);
        Mockito.when(mockRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(mockUser));

        userService.makeUnemployed(1L);

        Mockito.verify(mockRepository, Mockito.times(1)).save(mockUser);
    }

    @Test
    void testUpdateUser() {
        User mockUser = createMockUser();
        mockUser.setId(1L);
        Mockito.when(mockRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockUser));
        Mockito.when(mockRepository.findFirstByEmail(Mockito.anyString())).thenReturn(Optional.of(mockUser));
        User updatedUser = createMockUser();
        updatedUser.setId(1L);
        updatedUser.setEmail("updated-email@example.com");
        updatedUser.setPhoneNumber("1234567890");
        updatedUser.setFirstName("New First Name");
        updatedUser.setLastName("New Last Name");
        updatedUser.setPassword("1321T#new_password");
        Mockito.when(mockRepository.save(Mockito.any())).thenReturn(updatedUser);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("new_password_hash");

        User result = userService.update(updatedUser);

        verify(mockRepository, times(1)).findById(Mockito.eq(updatedUser.getId()));
        Mockito.verify(mockRepository, Mockito.times(1)).findFirstByEmail(Mockito.eq(updatedUser.getEmail()));
        Mockito.verify(mockRepository, Mockito.times(1)).save(Mockito.eq(mockUser));
        assertEquals(updatedUser.getEmail(), result.getEmail());
        assertEquals(updatedUser.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(updatedUser.getFirstName(), result.getFirstName());
        assertEquals(updatedUser.getLastName(), result.getLastName());
        assertEquals("1321T#new_password", result.getPassword());
    }
}