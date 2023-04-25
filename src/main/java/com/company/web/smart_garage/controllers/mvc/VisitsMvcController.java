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
import com.company.web.smart_garage.services.RepairService;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.services.VehicleService;
import com.company.web.smart_garage.services.VisitService;
import com.posadskiy.currencyconverter.CurrencyConverter;
import com.posadskiy.currencyconverter.enums.Currency;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;
import java.util.stream.Collectors;

import static com.company.web.smart_garage.utils.AuthorizationUtils.userIsAdminOrEmployee;

@RequiredArgsConstructor
@Controller
@RequestMapping("/visits")
public class VisitsMvcController {

    public static final int DEFAULT_PAGE_SIZE = 5;
    private final VisitService visitService;
    private final UserService userService;
    private final VehicleService vehicleService;
    private final RepairService repairService;
    private final CurrencyConverter converter;

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("httpCode", "404 Not Found");
        return "error";
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public String handleUnauthorized(UnauthorizedOperationException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("httpCode", "401 Unauthorized");
        return "error";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable long id, Authentication authentication, Model model) {
        Visit visit;

        visit = visitService.getById(id);
        if (!userIsAdminOrEmployee(authentication) &&
                !visit.getVisitor().getId().equals(userService.getByUsernameOrEmail(authentication.getName()).getId())) {
            throw new UnauthorizedOperationException("Access denied.");
        }
        model.addAttribute("visit", visit);
        model.addAttribute("currency", new SimpleStringDto());
        return "visit";
    }

    @GetMapping
    public String getAll(@ModelAttribute("visitFilterOptions") VisitFilterOptionsDto filter,
                         @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable,
                         Authentication authentication, Model viewModel) {
        viewModel.addAttribute("pageSize", pageable.getPageSize());
        Long actualVisitorId = null;
        Long actualVehicleId = null;
        Page<Visit> visits;
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
                        actualVisitorId = userService.getByUsernameOrEmail(filter.getVisitor()).getId();
                    }
                }
            }

            try {
                actualVehicleId = Long.parseLong(filter.getVehicle());
            } catch (NumberFormatException e) {
                if (filter.getVehicle() != null && !filter.getVehicle().isBlank()) {
                    Vehicle vehicle = vehicleService.getByLicensePlateOrVin(filter.getVehicle());
                    actualVehicleId = vehicle.getId();
                }
            }
            if (actualVehicleId != null && !userIsAdminOrEmployee(authentication) &&
                    !vehicleService.getById(actualVehicleId).getOwner().getId().equals(authUserId)) {
                throw new UnauthorizedOperationException("Access denied.");
            }

            if (filter.getDateFrom() != null && !filter.getDateFrom().isBlank())
                dateFrom = LocalDate.parse(filter.getDateFrom(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            if (filter.getDateTo() != null && !filter.getDateTo().isBlank())
                dateTo = LocalDate.parse(filter.getDateTo(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            visits = visitService.getAll(actualVisitorId, actualVehicleId, dateFrom, dateTo, pageable);
        } catch (InvalidParamException e) {
            viewModel.addAttribute("paramError", e.getMessage());
            return "allVisits";
        } catch (DateTimeParseException e) {
            viewModel.addAttribute("paramError", "Invalid date input.");
            return "allVisits";
        }
        viewModel.addAttribute("visits", visits);
        return "allVisits";
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping("/create")
    public String getCreatePage(Model model) {
        model.addAttribute("visitDto", new VisitDtoSimple());
        return "visitCreate";
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("visitDto") VisitDtoSimple dto,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "visitCreate";
        }
        User visitor;
        try {
            visitor = userService.getByUsernameOrEmail(dto.getVisitor());
        } catch (EntityNotFoundException e) {
            bindingResult.rejectValue("visitor", "visitor_invalid", e.getMessage());
            return "visitCreate";
        }
        Vehicle vehicle;
        try {
            vehicle = vehicleService.getByLicensePlateOrVin(dto.getVehicle());
        } catch (EntityNotFoundException e) {
            bindingResult.rejectValue("vehicle", "vehicle_invalid", e.getMessage());
            return "visitCreate";
        }

        Visit visit = new Visit(null, null, vehicle, visitor, null);
        visit = visitService.create(visit);

        return "redirect:/visits/" + visit.getId();
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping("/{id}/update")
    public String getUpdatePage(@PathVariable long id, Model model) {
        Visit visit = visitService.getById(id);
        Set<Repair> repairs = repairService.getAll(null, null, null, Pageable.unpaged())
                .stream().filter(repair -> !visit.getRepairs().contains(repair)).collect(Collectors.toSet());
        model.addAttribute("allRepairs", repairs);
        model.addAttribute("addRepair", new SimpleStringDto());
        model.addAttribute("visit", visit);
        return "visitUpdate";
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
    public String addRepair(@ModelAttribute("addRepair") SimpleStringDto dto,
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
                          @PathVariable long visitId, HttpServletResponse response,
                          Model model) throws IOException, MessagingException {
        Visit visit = visitService.getById(visitId);
        model.addAttribute("visit", visit);
//        model.addAttribute("currency", new SimpleStringDto());
        double rate;
        try {
            rate = converter.rate(Currency.BGN, Currency.findByCode(currency.getString())
                    .orElseThrow(() -> new InvalidParamException("Currency not supported.")));
        } catch (InvalidParamException e) {
            model.addAttribute("response", e.getMessage());
            return "visit";
        }
        visitService.sendPdfToMail(visit, rate);
        model.addAttribute("response", "Successfully sent pdf.");
        return "visit";
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
