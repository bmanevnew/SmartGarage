package com.company.web.smart_garage.controllers.mvc;

import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.Vehicle;
import com.company.web.smart_garage.models.filters.VehicleFilterOptionsDto;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.services.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.company.web.smart_garage.utils.AuthorizationUtils.userIsAdminOrEmployee;

@RequiredArgsConstructor
@Controller
@RequestMapping("/vehicles")
public class VehiclesMvcController {

    public static final int DEFAULT_PAGE_SIZE = 5;
    private final VehicleService vehicleService;
    private final UserService userService;

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "notFound";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable long id, Authentication authentication, Model model) {
        Vehicle vehicle;

        vehicle = vehicleService.getById(id);
        if (!userIsAdminOrEmployee(authentication) &&
                !vehicle.getOwner().getId().equals(userService.getByUsernameOrEmail(authentication.getName()).getId())) {
            //Todo change view to show user was unauthorized to access resource
            return "notFound";
        }
        model.addAttribute("vehicle", vehicle);
        return "vehicle";
    }

    @GetMapping
    public String getAll(@ModelAttribute("vehicleFilterOptions") VehicleFilterOptionsDto filter,
                         @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable,
                         Authentication authentication, Model viewModel) {
        viewModel.addAttribute("pageSize", pageable.getPageSize());
        Long actualOwnerId = null;
        Page<Vehicle> vehicles;
        Integer prodYearFrom = null;
        Integer prodYearTo = null;
        try {
            if (!userIsAdminOrEmployee(authentication)) {
                actualOwnerId = userService.getByUsernameOrEmail(authentication.getName()).getId();
            } else {
                try {
                    actualOwnerId = Long.parseLong(filter.getOwner());
                } catch (NumberFormatException e) {
                    if (filter.getOwner() != null && !filter.getOwner().isBlank()) {
                        actualOwnerId = userService.getByUsernameOrEmail(filter.getOwner()).getId();
                    }
                }
            }
            if (filter.getProdYearFrom() != null && !filter.getProdYearFrom().isBlank())
                prodYearFrom = Integer.parseInt(filter.getProdYearFrom());
            if (filter.getProdYearTo() != null && !filter.getProdYearTo().isBlank())
                prodYearTo = Integer.parseInt(filter.getProdYearTo());
            vehicles = vehicleService.getAll(actualOwnerId, filter.getModel(), filter.getBrand(),
                    prodYearFrom, prodYearTo, pageable);
        } catch (InvalidParamException e) {
            viewModel.addAttribute("paramError", e.getMessage());
            return "allVehicles";
        } catch (NumberFormatException e) {
            viewModel.addAttribute("paramError", "Invalid year input.");
            return "allVehicles";
        }
        viewModel.addAttribute("vehicles", vehicles);
        return "allVehicles";
    }
}
