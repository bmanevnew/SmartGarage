package com.company.web.smart_garage.controllers.rest;

import com.company.web.smart_garage.data_transfer_objects.VisitDtoIn;
import com.company.web.smart_garage.data_transfer_objects.VisitDtoOut;
import com.company.web.smart_garage.models.Visit;
import com.company.web.smart_garage.services.EmailSenderService;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.services.VehicleService;
import com.company.web.smart_garage.services.VisitService;
import com.company.web.smart_garage.utils.mappers.VisitMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static com.company.web.smart_garage.utils.AuthorizationUtils.userIsAdminOrEmployee;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;
    private final UserService userService;
    private final VehicleService vehicleService;
    private final VisitMapper visitMapper;
    private final EmailSenderService emailSenderService;


    @GetMapping("/{id}")
    public ResponseEntity<VisitDtoOut> getById(@PathVariable long id, Authentication authentication) {
        Visit visit = visitService.getById(id);
        if (!userIsAdminOrEmployee(authentication) &&
                !visit.getVisitor().getId().equals(userService.getByUsernameOrEmail(authentication.getName()).getId())) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(visitMapper.visitToDto(visit));
    }

    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<VisitDtoOut> getPdfReport(@PathVariable long id, Authentication authentication, HttpServletResponse response) throws IOException, MessagingException {
        Visit visit = visitService.getById(id);
        if (!userIsAdminOrEmployee(authentication) &&
                !visit.getVisitor().getId().equals(userService.getByUsernameOrEmail(authentication.getName()).getId())) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        visitService.generatePdf(response, visit);


        return ResponseEntity.ok(visitMapper.visitToDto(visit));
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
                                    Authentication authentication,
                                    Pageable pageable) {
        Long actualVisitorId;
        Long actualVehicleId;
        if (!userIsAdminOrEmployee(authentication)) {
            actualVisitorId = userService.getByUsernameOrEmail(authentication.getName()).getId();
        } else {
            actualVisitorId = getActualVisitorId(visitorId, username, phoneNumber);
        }
        actualVehicleId = getActualVehicleId(vehicleId, licensePlate, vin);
        return visitService.getAll(actualVisitorId, actualVehicleId, dateFrom, dateTo, pageable)
                .map(visitMapper::visitToDto)
                .getContent();
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @PostMapping
    public VisitDtoOut create(@Valid @RequestBody VisitDtoIn visitDto) {
        Visit visit = visitMapper.dtoToVisit(visitDto);
        return visitMapper.visitToDto(visitService.create(visit));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @PutMapping("/{id}")
    public VisitDtoOut update(@PathVariable long id, @Valid @RequestBody VisitDtoIn visitDto) {
        Visit visit = visitMapper.dtoToVisit(visitDto, id);
        return visitMapper.visitToDto(visitService.update(visit));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
