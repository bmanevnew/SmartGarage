package com.company.web.smart_garage.controllers.mvc;

import com.company.web.smart_garage.data_transfer_objects.VehicleDto;
import com.company.web.smart_garage.data_transfer_objects.VehicleDtoCreate;
import com.company.web.smart_garage.data_transfer_objects.filters.VehicleFilterOptionsDto;
import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.exceptions.UnauthorizedOperationException;
import com.company.web.smart_garage.models.User;
import com.company.web.smart_garage.models.Vehicle;
import com.company.web.smart_garage.services.UserService;
import com.company.web.smart_garage.services.VehicleService;
import com.company.web.smart_garage.utils.mappers.VehicleMapper;
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

import static com.company.web.smart_garage.utils.AuthorizationUtils.userIsAdminOrEmployee;
import static com.company.web.smart_garage.utils.Constants.VEHICLE_PROD_YEAR_INVALID;

@RequiredArgsConstructor
@Controller
@RequestMapping("/vehicles")
public class VehiclesMvcController {

    public static final int DEFAULT_PAGE_SIZE = 5;
    private final VehicleService vehicleService;
    private final UserService userService;
    private final VehicleMapper vehicleMapper;

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
        Vehicle vehicle = vehicleService.getById(id);
        if (!userIsAdminOrEmployee(authentication) &&
                !vehicle.getOwner().getId().equals(userService.getByUsernameOrEmail(authentication.getName()).getId())) {
            throw new UnauthorizedOperationException("Access denied.");
        }
        model.addAttribute("vehicle", vehicle);
        return "vehicle";
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping("/create")
    public String getCreatePage(Model model) {
        model.addAttribute("vehicleDtoCreate", new VehicleDtoCreate());
        return "vehicleCreate";
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("vehicleDtoCreate") VehicleDtoCreate dto,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "vehicleCreate";
        }
        User owner;
        try {
            owner = userService.getByUsernameOrEmail(dto.getOwnerUsernameOrEmail());
        } catch (EntityNotFoundException e) {
            bindingResult.rejectValue("ownerUsernameOrEmail", "owner_invalid", e.getMessage());
            return "vehicleCreate";
        }
        Vehicle vehicle;
        try {
            Vehicle vehicleFromDto;
            try {
                vehicleFromDto = vehicleMapper.dtoToVehicle(dto);
            } catch (NumberFormatException e) {
                bindingResult.rejectValue("productionYear", "prod_year_invalid", "Year invalid.");
                return "vehicleCreate";
            }
            vehicle = vehicleService.create(vehicleFromDto, owner);
        } catch (InvalidParamException e) {
            bindingResult.rejectValue("productionYear", "prod_year_invalid", e.getMessage());
            return "vehicleCreate";
        }
        return "redirect:/vehicles/" + vehicle.getId();
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping("/{id}/update")
    public String getUpdatePage(@PathVariable long id, Authentication authentication, Model model) {
        Vehicle vehicle = vehicleService.getById(id);
        if (!userIsAdminOrEmployee(authentication) &&
                !vehicle.getOwner().getId().equals(userService.getByUsernameOrEmail(authentication.getName()).getId())) {
            //Todo change view to show user was unauthorized to access resource
            throw new UnauthorizedOperationException("Access denied.");
        }
        model.addAttribute("vehicleDto", vehicleMapper.vehicleToDto(vehicle));
        model.addAttribute("vehicle", vehicle);
        return "vehicleUpdate";
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @PostMapping("/{id}/update")
    public String update(@Valid @ModelAttribute("vehicleDto") VehicleDto dto,
                         BindingResult bindingResult,
                         @PathVariable long id,
                         Model model) {
        model.addAttribute("vehicle", vehicleService.getById(id));
        if (bindingResult.hasErrors()) {
            return "vehicleUpdate";
        }
        Vehicle vehicle = vehicleMapper.dtoToVehicle(dto, id);
        try {
            vehicleService.update(vehicle);
        } catch (InvalidParamException e) {
            if (e.getMessage().equals(VEHICLE_PROD_YEAR_INVALID)) {
                bindingResult.rejectValue("productionYear", "prod_year_error", e.getMessage());
                return "vehicleUpdate";
            }
        }
        return "redirect:/vehicles/" + id;
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable long id) {
        try {
            vehicleService.delete(id);
        } catch (InvalidParamException e) {
            throw new EntityNotFoundException("Vehicle", id);
        }
        return "redirect:/vehicles";
    }
}
