package com.company.web.smart_garage.controllers.rest;

import com.company.web.smart_garage.data_transfer_objects.VehicleDto;
import com.company.web.smart_garage.models.User;
import com.company.web.smart_garage.models.Vehicle;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.services.VehicleService;
import com.company.web.smart_garage.utils.mappers.VehicleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.company.web.smart_garage.utils.AuthorizationUtils.userIsAdminOrEmployee;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleMapper vehicleMapper;
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDto> getById(@PathVariable long id, Authentication authentication) {
        Vehicle vehicle = vehicleService.getById(id);
        if (!userIsAdminOrEmployee(authentication) &&
                !vehicle.getOwner().getId().equals(userService.getByUsernameOrEmail(authentication.getName()).getId())) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(vehicleMapper.vehicleToDto(vehicle));
    }

    @GetMapping(params = "license-plate")
    public ResponseEntity<VehicleDto> getByLicensePlate(@RequestParam(name = "license-plate") String licensePlate,
                                                        Authentication authentication) {
        Vehicle vehicle = vehicleService.getByLicensePlate(licensePlate);
        if (!userIsAdminOrEmployee(authentication) &&
                !vehicle.getOwner().getId().equals(userService.getByUsernameOrEmail(authentication.getName()).getId())) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(vehicleMapper.vehicleToDto(vehicle));
    }

    @GetMapping(params = "vin")
    public ResponseEntity<VehicleDto> getByVin(@RequestParam(name = "vin") String vin, Authentication authentication) {

        Vehicle vehicle = vehicleService.getByVin(vin);
        if (!userIsAdminOrEmployee(authentication) &&
                !vehicle.getOwner().getId().equals(userService.getByUsernameOrEmail(authentication.getName()).getId())) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(vehicleMapper.vehicleToDto(vehicle));
    }

    @GetMapping
    public List<VehicleDto> getAll(@RequestParam(required = false, name = "owner-id") Long ownerId,
                                   @RequestParam(required = false, name = "owner-username") String username,
                                   @RequestParam(required = false, name = "owner-phone-number") String phoneNumber,
                                   @RequestParam(required = false, name = "model") String model,
                                   @RequestParam(required = false, name = "brand") String brand,
                                   @RequestParam(required = false, name = "prod-from") Integer prodYearFrom,
                                   @RequestParam(required = false, name = "prod-to") Integer prodYearTo,
                                   Authentication authentication,
                                   Pageable pageable) {
        Long actualOwnerId;
        if (!userIsAdminOrEmployee(authentication)) {
            actualOwnerId = userService.getByUsernameOrEmail(authentication.getName()).getId();
        } else {
            actualOwnerId = getActualOwnerId(ownerId, username, phoneNumber);
        }
        return vehicleService.getAll(actualOwnerId, model, brand, prodYearFrom, prodYearTo, pageable)
                .map(vehicleMapper::vehicleToDto).getContent();
    }

    private Long getActualOwnerId(Long ownerId, String username, String phoneNumber) {
        Long actualId = null;
        if (username != null) {
            actualId = userService.getByUsername(username).getId();
        } else if (ownerId != null) {
            actualId = ownerId;
        } else if (phoneNumber != null) {
            actualId = userService.getByPhoneNumber(phoneNumber).getId();
        }
        return actualId;
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @PostMapping
    public VehicleDto create(@RequestBody VehicleDto dto,
                             @RequestParam("owner-email") String email,
                             @RequestParam("owner-phone-number") String phoneNumber) {
        Vehicle vehicle = vehicleMapper.dtoToVehicle(dto);
        User user = new User();
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        return vehicleMapper.vehicleToDto(vehicleService.create(vehicle, user));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @PutMapping("/{id}")
    public VehicleDto update(@PathVariable long id, @RequestBody VehicleDto dto) {
        Vehicle vehicle = vehicleMapper.dtoToVehicle(dto, id);
        return vehicleMapper.vehicleToDto(vehicleService.update(vehicle));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public VehicleDto delete(@PathVariable long id) {
        return vehicleMapper.vehicleToDto(vehicleService.delete(id));
    }
}
