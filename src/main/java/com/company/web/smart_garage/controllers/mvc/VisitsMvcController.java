package com.company.web.smart_garage.controllers.mvc;

import com.company.web.smart_garage.data_transfer_objects.SimpleStringDto;
import com.company.web.smart_garage.data_transfer_objects.VisitDtoSimple;
import com.company.web.smart_garage.data_transfer_objects.filters.VisitFilterOptionsDto;
import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.exceptions.UnauthorizedOperationException;
import com.company.web.smart_garage.models.Repair;
import com.company.web.smart_garage.models.User;
import com.company.web.smart_garage.models.Vehicle;
import com.company.web.smart_garage.models.Visit;
import com.company.web.smart_garage.services.*;
import com.company.web.smart_garage.utils.Constants;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static com.company.web.smart_garage.utils.AuthorizationUtils.userIsAdminOrEmployee;
import static com.company.web.smart_garage.utils.Constants.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/visits")
public class VisitsMvcController {

    public static final int DEFAULT_PAGE_SIZE = 5;
    public static final String VISIT_VIEW = "visit";
    public static final String ALL_VISITS_VIEW = "allVisits";
    public static final String VISIT_CREATE_VIEW = "visitCreate";
    public static final String VISIT_UPDATE_VIEW = "visitUpdate";
    private final VisitService visitService;
    private final UserService userService;
    private final VehicleService vehicleService;
    private final RepairService repairService;
    private final VisitPdfExporterService pdfExporter;

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException e, Model model) {
        model.addAttribute(ERROR_MESSAGE_KEY, e.getMessage());
        model.addAttribute(HTTP_CODE_KEY, NOT_FOUND_HEADING);
        return ERROR_VIEW;
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public String handleUnauthorized(UnauthorizedOperationException e, Model model) {
        model.addAttribute(ERROR_MESSAGE_KEY, e.getMessage());
        model.addAttribute(HTTP_CODE_KEY, UNAUTHORIZED_HEADING);
        return ERROR_VIEW;
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable long id, Authentication authentication, Model model) {
        Visit visit;

        visit = visitService.getById(id);
        if (!userIsAdminOrEmployee(authentication) &&
                !visit.getVisitor().getId().equals(userService.getByUsernameOrEmail(authentication.getName()).getId())) {
            throw new UnauthorizedOperationException(ACCESS_DENIED);
        }
        model.addAttribute(VISIT_KEY, visit);
        model.addAttribute(CURRENCY_KEY, new SimpleStringDto());
        return VISIT_VIEW;
    }

    @GetMapping
    public String getAll(@ModelAttribute(VISIT_FILTER_OPTIONS) VisitFilterOptionsDto filter,
                         @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable,
                         Authentication authentication, Model viewModel) {
        viewModel.addAttribute("pageSize", pageable.getPageSize());
        Page<Visit> visits;
        Long actualVisitorId = null;
        Long actualVehicleId = null;
        LocalDate dateFrom = null;
        LocalDate dateTo = null;
        try {
            Long authUserId = userService.getByUsernameOrEmail(authentication.getName()).getId();
            if (!userIsAdminOrEmployee(authentication)) {
                actualVisitorId = authUserId;
            } else {
                try {
                    actualVisitorId = Long.parseLong(filter.getVisitor());
                } catch (NumberFormatException e) {
                    if (filter.getVisitor() != null && !filter.getVisitor().isBlank()) {
                        try {
                            actualVisitorId = userService.getByUsernameOrEmail(filter.getVisitor()).getId();
                        } catch (EntityNotFoundException ex) {
                            viewModel.addAttribute(PARAM_ERROR, ex.getMessage());
                            return ALL_VISITS_VIEW;
                        }
                    }
                }
            }

            try {
                actualVehicleId = Long.parseLong(filter.getVehicle());
            } catch (NumberFormatException e) {
                if (filter.getVehicle() != null && !filter.getVehicle().isBlank()) {
                    try {
                        Vehicle vehicle = vehicleService.getByLicensePlateOrVin(filter.getVehicle());
                        actualVehicleId = vehicle.getId();
                    } catch (EntityNotFoundException ex) {
                        viewModel.addAttribute(PARAM_ERROR, ex.getMessage());
                        return ALL_VISITS_VIEW;
                    }
                }
            }
            if (actualVehicleId != null && !userIsAdminOrEmployee(authentication) &&
                    !vehicleService.getById(actualVehicleId).getOwner().getId().equals(authUserId)) {
                viewModel.addAttribute(PARAM_ERROR, ACCESS_DENIED);
                return ALL_VISITS_VIEW;
            }

            if (filter.getDateFrom() != null && !filter.getDateFrom().isBlank())
                dateFrom = LocalDate.parse(filter.getDateFrom(), DateTimeFormatter.ofPattern(DATE_FORMAT));
            if (filter.getDateTo() != null && !filter.getDateTo().isBlank())
                dateTo = LocalDate.parse(filter.getDateTo(), DateTimeFormatter.ofPattern(DATE_FORMAT));
            visits = visitService.getAll(actualVisitorId, actualVehicleId, dateFrom, dateTo, pageable);
        } catch (InvalidParamException e) {
            viewModel.addAttribute(PARAM_ERROR, e.getMessage());
            return ALL_VISITS_VIEW;
        } catch (DateTimeParseException e) {
            viewModel.addAttribute(PARAM_ERROR, INVALID_DATE);
            return ALL_VISITS_VIEW;
        }
        viewModel.addAttribute(VISITS_KEY, visits);
        return ALL_VISITS_VIEW;
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping("/create")
    public String getCreatePage(Model model) {
        model.addAttribute(VISIT_DTO, new VisitDtoSimple());
        return VISIT_CREATE_VIEW;
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute(VISIT_DTO) VisitDtoSimple dto,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return VISIT_CREATE_VIEW;
        }
        User visitor;
        try {
            visitor = userService.getByUsernameOrEmail(dto.getVisitor());
        } catch (EntityNotFoundException e) {
            bindingResult.rejectValue(VISITOR_FIELD, VISITOR_INVALID, e.getMessage());
            return VISIT_CREATE_VIEW;
        }
        Vehicle vehicle;
        try {
            vehicle = vehicleService.getByLicensePlateOrVin(dto.getVehicle());
        } catch (EntityNotFoundException e) {
            bindingResult.rejectValue(VEHICLE_FIELD, VEHICLE_INVALID, e.getMessage());
            return VISIT_CREATE_VIEW;
        }

        Visit visit = new Visit(null, null, vehicle, visitor, null);
        visit = visitService.create(visit);

        return "redirect:/visits/" + visit.getId() + "/update";
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping("/{id}/update")
    public String getUpdatePage(@PathVariable long id, Model model) {
        Visit visit = visitService.getById(id);
        Set<Repair> repairs = repairService.getAll(null, null, null, Pageable.unpaged())
                .stream().filter(repair -> !visit.getRepairs().contains(repair)).collect(Collectors.toSet());
        model.addAttribute(ALL_REPAIRS_KEY, repairs);
        model.addAttribute(ADD_REPAIR_KEY, new SimpleStringDto());
        model.addAttribute(VISIT_KEY, visit);
        return VISIT_UPDATE_VIEW;
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping("/{visitId}/addRepair/{repairId}")
    public String addRepair(@PathVariable long visitId, @PathVariable long repairId) {
        try {
            visitService.addRepair(visitId, repairId);
        } catch (InvalidParamException e) {
            throw new EntityNotFoundException("Visit", visitId);
        }
        return "redirect:/visits/" + visitId + "/update";
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping("/{visitId}/addRepair")
    public String addRepair(@ModelAttribute(ADD_REPAIR_KEY) SimpleStringDto dto,
                            @PathVariable long visitId) {
        return addRepair(visitId, Long.parseLong(dto.getString()));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping("/{visitId}/removeRepair/{repairId}")
    public String removeRepair(@PathVariable long visitId, @PathVariable long repairId) {
        try {
            visitService.removeRepair(visitId, repairId);
        } catch (InvalidParamException e) {
            throw new EntityNotFoundException("Visit", visitId);
        }
        return "redirect:/visits/" + visitId + "/update";
    }

    @GetMapping("/{visitId}/sendPdf")
    public String sendPdf(@ModelAttribute("currency") SimpleStringDto currency,
                          @PathVariable long visitId, Model model,
                          Authentication authentication) throws IOException, MessagingException {
        Visit visit = visitService.getById(visitId);

        if (!userIsAdminOrEmployee(authentication) &&
                !visit.getVisitor().getId().equals(userService.getByUsernameOrEmail(authentication.getName()).getId())) {
            throw new UnauthorizedOperationException(ACCESS_DENIED);
        }

        model.addAttribute(VISIT_KEY, visit);
        try {
            visitService.sendPdfToMail(visit, currency.getString().toUpperCase());
        } catch (InvalidParamException e) {
            model.addAttribute(RESPONSE_KEY, e.getMessage());
            return VISIT_VIEW;
        }
        model.addAttribute(RESPONSE_KEY, SUCCESSFULLY_SENT_PDF);
        return VISIT_VIEW;
    }

    @GetMapping("/{visitId}/downloadPdf")
    public Object downloadPdf(@ModelAttribute(CURRENCY_KEY) SimpleStringDto currency,
                              @PathVariable long visitId, Model model,
                              Authentication authentication) {

        Visit visit = visitService.getById(visitId);

        if (!userIsAdminOrEmployee(authentication) &&
                !visit.getVisitor().getId().equals(userService.getByUsernameOrEmail(authentication.getName()).getId())) {
            throw new UnauthorizedOperationException(ACCESS_DENIED);
        }

        model.addAttribute(VISIT_KEY, visit);
        byte[] contents;
        try {
            contents = pdfExporter.export(visit, currency.getString().toUpperCase()).toByteArray();
        } catch (InvalidParamException e) {
            model.addAttribute(RESPONSE_KEY, e.getMessage());
            return VISIT_VIEW;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_MS);
        String currentDateTime = dateFormat.format(new Date());
        String filename = String.format(FILE_NAME_FORMAT, currentDateTime);
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl(CACHE_CONTROL);

        return new ResponseEntity<>(contents, headers, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable long id) {
        try {
            visitService.delete(id);
        } catch (InvalidParamException e) {
            throw new EntityNotFoundException("Visit", id);
        }
        return "redirect:/visits";
    }
}
