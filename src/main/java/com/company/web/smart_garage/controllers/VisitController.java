package com.company.web.smart_garage.controllers;

import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.Visit;
import com.company.web.smart_garage.models.user.User;
import com.company.web.smart_garage.models.vehicle.Vehicle;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.services.VehicleService;
import com.company.web.smart_garage.services.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;
    private final UserService userService;
    private final VehicleService vehicleService;


    @GetMapping("/{id}")
    public Visit getById(@PathVariable long id) {
        return visitService.getById(id);
    }

    @GetMapping
    public List<Visit> getAll(@RequestParam(required = false, name = "visitor-id") Long visitorId,
                              @RequestParam(required = false, name = "visitor-username") String username,
                              @RequestParam(required = false, name = "visitor-phone-number") String phoneNumber,
                              @RequestParam(required = false, name = "vehicle-id") Long vehicleId,
                              @RequestParam(required = false, name = "vehicle-license-plate") String licensePlate,
                              @RequestParam(required = false, name = "vehicle-vin") String vin,
                              @RequestParam(required = false, name = "date-from") LocalDate dateFrom,
                              @RequestParam(required = false, name = "date-to") LocalDate dateTo,
                              Pageable pageable) {
        Long actualVisitorId = getActualVisitorId(visitorId, username, phoneNumber);
        Long actualVehicleId = getActualVehicleId(vehicleId, licensePlate, vin);

        return visitService.getAll(actualVisitorId, actualVehicleId, dateFrom, dateTo, pageable).getContent();
    }

    @PostMapping
    public Visit create(@RequestParam(required = false, name = "visitor-id") Long visitorId,
                        @RequestParam(required = false, name = "visitor-username") String username,
                        @RequestParam(required = false, name = "visitor-phone-number") String phoneNumber,
                        @RequestParam(required = false, name = "vehicle-id") Long vehicleId,
                        @RequestParam(required = false, name = "vehicle-license-plate") String licensePlate,
                        @RequestParam(required = false, name = "vehicle-vin") String vin) {
        Long actualVisitorId = getActualVisitorId(visitorId, username, phoneNumber);
        Long actualVehicleId = getActualVehicleId(vehicleId, licensePlate, vin);
        if (actualVisitorId == null || actualVehicleId == null)
            throw new InvalidParamException("Invalid parameters for creation.");
        User visitor = userService.getUserById(actualVisitorId);
        Vehicle vehicle = vehicleService.getById(actualVehicleId);
        return visitService.create(visitor, vehicle);
    }

    @DeleteMapping("/{id}")
    public Visit delete(@PathVariable long id) {
        Visit visit = getById(id);
        visitService.delete(id);
        return visit;
    }

    private Long getActualVehicleId(Long vehicleId, String licensePlate, String vin) {
        Long actualVehicleId = null;
        if (licensePlate != null) {
            actualVehicleId = vehicleService.getByLicensePlate(licensePlate).getId();
        } else if (vin != null) {
            actualVehicleId = vehicleService.getByVin(vin).getId();
        } else if (vehicleId != null) {
            actualVehicleId = vehicleId;
        }
        return actualVehicleId;
    }

    private Long getActualVisitorId(Long visitorId, String username, String phoneNumber) {
        Long actualVisitorId = null;
        if (username != null) {
            actualVisitorId = userService.getByUsername(username).getId();
        } else if (phoneNumber != null) {
            actualVisitorId = userService.getByPhoneNumber(phoneNumber).getId();
        } else if (visitorId != null) {
            actualVisitorId = visitorId;
        }
        return actualVisitorId;
    }
}
