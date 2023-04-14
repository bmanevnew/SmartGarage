package com.company.web.smart_garage.controllers;

import com.company.web.smart_garage.data_transfer_objects.VisitDtoIn;
import com.company.web.smart_garage.data_transfer_objects.VisitDtoOut;
import com.company.web.smart_garage.models.Visit;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.services.VehicleService;
import com.company.web.smart_garage.services.VisitService;
import com.company.web.smart_garage.utils.helpers.VisitMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final VisitMapper visitMapper;


    @GetMapping("/{id}")
    public VisitDtoOut getById(@PathVariable long id) {
        return visitMapper.visitToDto(visitService.getById(id));
    }

    @GetMapping
    public List<VisitDtoOut> getAll(@RequestParam(required = false, name = "visitor-id") Long visitorId,
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

        return visitService.getAll(actualVisitorId, actualVehicleId, dateFrom, dateTo, pageable)
                .map(visitMapper::visitToDto)
                .getContent();
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER','ROLE_ADMIN')")
    @PostMapping
    public VisitDtoOut create(@Valid @RequestBody VisitDtoIn visitDto) {
        Visit visit = visitMapper.dtoToVisit(visitDto);
        return visitMapper.visitToDto(visitService.create(visit));
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER','ROLE_ADMIN')")
    @PutMapping("/{id}")
    public VisitDtoOut update(@PathVariable long id, @Valid @RequestBody VisitDtoIn visitDto) {
        Visit visit = visitMapper.dtoToVisit(visitDto, id);
        return visitMapper.visitToDto(visitService.update(visit));
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER','ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public VisitDtoOut delete(@PathVariable long id) {
        return visitMapper.visitToDto(visitService.delete(id));
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
