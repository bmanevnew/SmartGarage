package com.company.web.smart_garage.services;

import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.User;
import com.company.web.smart_garage.models.Vehicle;
import com.company.web.smart_garage.repositories.VehicleRepository;
import com.company.web.smart_garage.services.impl.VehicleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static com.company.web.smart_garage.Helpers.createMockUser;
import static com.company.web.smart_garage.Helpers.createMockVehicle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTests {

    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private Vehicle vehicle;
    private User user;

    @BeforeEach
    public void beforeEach() {
        vehicle = createMockVehicle();
        user = createMockUser();
    }

    @Test
    public void getById_Should_ReturnVisit_When_VehicleExists() {
        when(vehicleRepository.findById(anyLong())).thenReturn(Optional.of(vehicle));

        Vehicle actualVehicle = vehicleService.getById(1L);

        verify(vehicleRepository).findById(1L);
        assertEquals(vehicle, actualVehicle);
    }

    @Test
    public void getById_Should_ThrowException_When_IdIsNegativeOrZero() {
        assertThrows(InvalidParamException.class, () -> vehicleService.getById(-5));
    }

    @Test
    public void getById_Should_ThrowException_When_VehicleDoesNotExist() {
        when(vehicleRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> vehicleService.getById(1));
    }

    @Test
    public void getByLicensePlate_Should_ReturnVisit_When_VehicleExists() {
        when(vehicleRepository.findFirstByLicensePlate(anyString())).thenReturn(Optional.of(vehicle));

        Vehicle actualVehicle = vehicleService.getByLicensePlate("C 1231 KH");

        verify(vehicleRepository).findFirstByLicensePlate("C 1231 KH");
        assertEquals(vehicle, actualVehicle);
    }

    @Test
    public void getByLicensePlate_ThrowException_When_LicensePlateWrongFormat() {
        assertThrows(InvalidParamException.class, () -> vehicleService.getByLicensePlate("Invalid plate"));
    }

    @Test
    public void getByLicensePlate_ThrowException_When_LicensePlateWrongAreaCode() {
        assertThrows(InvalidParamException.class, () -> vehicleService.getByLicensePlate("SA 1233 KP"));
    }

    @Test
    public void getByLicensePlate_Should_ThrowException_When_VehicleDoesNotExist() {
        when(vehicleRepository.findFirstByLicensePlate(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> vehicleService.getByLicensePlate("C 1231 KH"));
    }

    @Test
    public void getByVin_Should_ReturnVisit_When_VehicleExists() {
        when(vehicleRepository.findFirstByVin(anyString())).thenReturn(Optional.of(vehicle));

        Vehicle actualVehicle = vehicleService.getByVin("K12S3123ASSD78ADP");

        verify(vehicleRepository).findFirstByVin("K12S3123ASSD78ADP");
        assertEquals(vehicle, actualVehicle);
    }

    @Test
    public void getByVin_ThrowException_When_VinWrongFormat() {
        assertThrows(InvalidParamException.class, () -> vehicleService.getByVin("Invalid vin."));
    }

    @Test
    public void getByVin_Should_ThrowException_When_VehicleDoesNotExist() {
        when(vehicleRepository.findFirstByVin(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> vehicleService.getByVin("K12S3123ASSD78ADP"));
    }

    @Test
    public void getAll_Should_ReturnAllVehicles_When_ParametersAreNull() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Vehicle> page = new PageImpl<>(List.of(vehicle));
        when(vehicleRepository.findByParameters(
                null, null, null, null, null, pageable))
                .thenReturn(page);

        Page<Vehicle> result = vehicleService.getAll(
                null, null, null, null, null, pageable);

        verify(vehicleRepository).findByParameters(
                null, null, null, null, null, pageable);
        assertEquals(page, result);
    }

    @Test
    public void getAll_Should_ReturnVehicles_When_ParametersAreValid() {
        Long ownerId = 3L;
        String model = "Ibisa";
        String brand = "Seat";
        Integer prodYearFrom = 2006;
        Integer prodYearTo = 2012;
        Pageable pageable = PageRequest.of(0, 10);

        Page<Vehicle> page = new PageImpl<>(List.of(vehicle));
        when(vehicleRepository.findByParameters(ownerId, model, brand, prodYearFrom, prodYearTo, pageable))
                .thenReturn(page);

        Page<Vehicle> result = vehicleService.getAll(ownerId, model, brand, prodYearFrom, prodYearTo, pageable);

        verify(vehicleRepository).findByParameters(ownerId, model, brand, prodYearFrom, prodYearTo, pageable);
        assertEquals(page, result);
    }

    @Test
    public void getAll_Should_ThrowException_When_OwnerIdIsNegativeOrZero() {
        Pageable pageable = PageRequest.of(0, 10);
        assertThrows(InvalidParamException.class, () -> vehicleService.getAll(
                -5L, null, null, null, null, pageable));
    }

    //currently valid range is 1886 - current year
    @Test
    public void getAll_Should_ThrowException_When_ProdYearIsInvalid() {
        Pageable pageable = PageRequest.of(0, 10);
        assertThrows(InvalidParamException.class, () -> vehicleService.getAll(
                null, null, null, 1600, 1997, pageable));
    }

    @Test
    public void getAll_Should_ThrowException_When_ProdYearIntervalIsInvalid() {
        Pageable pageable = PageRequest.of(0, 10);
        assertThrows(InvalidParamException.class, () -> vehicleService.getAll(
                null, null, null, 1976, 1961, pageable));
    }

    //requested page 3 is empty thus is invalid
    @Test
    public void getAll_Should_ThrowException_When_PageIsInvalid() {
        Pageable pageable = PageRequest.of(3, 10);

        Page<Vehicle> page = Page.empty();
        when(vehicleRepository.findByParameters(
                null, null, null, null, null, pageable))
                .thenReturn(page);

        assertThrows(InvalidParamException.class, () -> vehicleService.getAll(
                null, null, null, null, null, pageable));
    }

    @Test
    public void getAll_Should_ThrowException_When_SortingPropertyInvalid() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("InvalidProperty"));
        assertThrows(InvalidParamException.class, () -> vehicleService.getAll(
                null, null, null, null, null, pageable));
    }

    @Test
    public void create_Should_CreateVehicle_When_PropertiesValidAndOwnerExists() {
        when(userService.getByEmail(anyString())).thenReturn(user);
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);

        Vehicle returnedVehicle = vehicleService.create(vehicle, user);

        assertEquals(returnedVehicle.getOwner(), user);
        verify(vehicleRepository).save(vehicle);
        assertEquals(returnedVehicle, vehicle);
    }

    @Test
    public void create_Should_CreateOwner_When_PropertiesValidAndOwnerDoesNotExist() {
        when(userService.getByEmail(anyString())).thenThrow(EntityNotFoundException.class);
        when(userService.create(user)).thenReturn(user);
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);

        Vehicle returnedVehicle = vehicleService.create(vehicle, user);

        verify(userService).create(user);
        assertEquals(returnedVehicle.getOwner(), user);
        verify(vehicleRepository).save(vehicle);
        assertEquals(returnedVehicle, vehicle);
    }

    @Test
    public void create_Should_ThrowException_When_LicensePlateInvalid() {
        when(userService.getByEmail(anyString())).thenReturn(user);
        vehicle.setLicensePlate("Invalid plate.");
        assertThrows(InvalidParamException.class, () -> vehicleService.create(vehicle, user));
    }

    @Test
    public void create_Should_ThrowException_When_ProductionYearInvalid() {
        when(userService.getByEmail(anyString())).thenReturn(user);
        vehicle.setProductionYear(1100);
        assertThrows(InvalidParamException.class, () -> vehicleService.create(vehicle, user));
    }

    @Test
    public void update_Should_UpdateVehicle_When_VehicleIsValid() {
        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);

        Vehicle updatedVehicle = vehicleService.update(vehicle);

        verify(vehicleRepository).findById(vehicle.getId());
        verify(vehicleRepository).save(vehicle);
        assertEquals(vehicle, updatedVehicle);
    }

    @Test
    public void update_Should_ThrowException_When_VehicleWithIdDoesNotExist() {
        when(vehicleRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> vehicleService.update(vehicle));
    }

    @Test
    public void update_Should_ThrowException_When_IdIsNegativeOrZero() {
        vehicle.setId(-5L);
        assertThrows(InvalidParamException.class, () -> vehicleService.update(vehicle));
    }

    @Test
    public void update_Should_ThrowException_When_LicensePlateInvalid() {
        when(vehicleRepository.findById(anyLong())).thenReturn(Optional.of(vehicle));
        vehicle.setLicensePlate("Invalid license plate.");
        assertThrows(InvalidParamException.class, () -> vehicleService.update(vehicle));
    }

    @Test
    public void update_Should_ThrowException_When_VinInvalid() {
        when(vehicleRepository.findById(anyLong())).thenReturn(Optional.of(vehicle));
        vehicle.setVin("Invalid vin.");
        assertThrows(InvalidParamException.class, () -> vehicleService.update(vehicle));
    }

    @Test
    public void update_Should_ThrowException_When_ProductionYearInvalid() {
        when(vehicleRepository.findById(anyLong())).thenReturn(Optional.of(vehicle));
        vehicle.setProductionYear(1200);
        assertThrows(InvalidParamException.class, () -> vehicleService.update(vehicle));
    }

    @Test
    public void delete_Should_DeleteVehicleAndReturnIt_When_VehicleExists() {
        when(vehicleRepository.findByIdFetchAll(vehicle.getId())).thenReturn(Optional.of(vehicle));

        Vehicle deletedVehicle = vehicleService.delete(vehicle.getId());

        verify(vehicleRepository).deleteById(vehicle.getId());
        assertEquals(vehicle, deletedVehicle);
    }

    @Test
    public void delete_Should_ThrowException_When_IdIsNegativeOrZero() {
        assertThrows(InvalidParamException.class, () -> vehicleService.delete(-5));
    }

    @Test
    public void delete_Should_ThrowException_When_VehicleDoesNotExist() {
        when(vehicleRepository.findByIdFetchAll(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vehicleService.delete(1L));
        verify(vehicleRepository, never()).deleteById(1L);
    }
}
