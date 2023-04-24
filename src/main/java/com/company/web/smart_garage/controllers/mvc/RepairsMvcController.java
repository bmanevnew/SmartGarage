package com.company.web.smart_garage.controllers.mvc;

import com.company.web.smart_garage.data_transfer_objects.RepairDto;
import com.company.web.smart_garage.data_transfer_objects.RepairDtoStrings;
import com.company.web.smart_garage.data_transfer_objects.filters.RepairFilterOptionsDto;
import com.company.web.smart_garage.exceptions.EntityDuplicationException;
import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.exceptions.UnauthorizedOperationException;
import com.company.web.smart_garage.models.Repair;
import com.company.web.smart_garage.services.RepairService;
import com.company.web.smart_garage.utils.mappers.RepairMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/repairs")
public class RepairsMvcController {

    public static final int DEFAULT_PAGE_SIZE = 5;
    private final RepairService repairService;
    private final RepairMapper repairMapper;

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
    public String getById(@PathVariable long id, Model model) {
        Repair repair = repairService.getById(id);
        model.addAttribute("repair", repair);
        return "repair";
    }


    @GetMapping
    public String getAll(@ModelAttribute("repairFilterOptions") RepairFilterOptionsDto filter,
                         @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable, Model viewModel) {
        viewModel.addAttribute("pageSize", pageable.getPageSize());
        Page<Repair> repairs;
        try {
            String name = null;
            Double priceFrom = null;
            Double priceTo = null;
            if (filter.getPriceFrom() != null && !filter.getPriceFrom().isBlank())
                priceFrom = Double.parseDouble(filter.getPriceFrom());
            if (filter.getPriceTo() != null && !filter.getPriceTo().isBlank())
                priceTo = Double.parseDouble(filter.getPriceTo());
            if (filter.getName() != null && !filter.getName().isBlank())
                name = filter.getName();

            repairs = repairService.getAll(name, priceFrom, priceTo, pageable);
        } catch (InvalidParamException e) {
            viewModel.addAttribute("paramError", e.getMessage());
            return "allRepairs";
        } catch (NumberFormatException e) {
            viewModel.addAttribute("paramError", "Invalid price input.");
            return "allRepairs";
        }
        viewModel.addAttribute("repairs", repairs);
        return "allRepairs";
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping("/create")
    public String getCreatePage(Model model) {
        model.addAttribute("repairDtoCreate", new RepairDtoStrings());
        return "repairCreate";
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("repairDtoCreate") RepairDtoStrings dto,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "repairCreate";
        }
        Repair repair;
        Repair repairFromDto;
        try {
            repairFromDto = repairMapper.dtoToRepair(dto);
        } catch (NumberFormatException e) {
            bindingResult.rejectValue("price", "invalid_price", "Price is invalid.");
            return "repairCreate";
        }
        try {
            repair = repairService.create(repairFromDto);
        } catch (InvalidParamException e) {
            bindingResult.rejectValue("price", "price_invalid", e.getMessage());
            return "repairCreate";
        } catch (EntityDuplicationException e) {
            bindingResult.rejectValue("name", "duplicate_name", e.getMessage());
            return "repairCreate";
        }
        return "redirect:/repairs/" + repair.getId();
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping("/{id}/update")
    public String getUpdatePage(@PathVariable long id, Model model) {
        Repair repair = repairService.getById(id);
        model.addAttribute("repairDto", repairMapper.repairToDto(repair));
        model.addAttribute("repair", repair);
        return "repairUpdate";
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @PostMapping("/{id}/update")
    public String update(@Valid @ModelAttribute("repairDto") RepairDto dto,
                         BindingResult bindingResult,
                         @PathVariable long id,
                         Model model) {
        model.addAttribute("repair", repairService.getById(id));
        if (bindingResult.hasErrors()) {
            return "repairUpdate";
        }
        Repair repair = repairMapper.dtoToRepair(dto, id);
        Repair afterUpdate;
        try {
            afterUpdate = repairService.update(repair);
        } catch (InvalidParamException e) {
            bindingResult.rejectValue("price", "price_error", e.getMessage());
            return "repairUpdate";
        } catch (EntityDuplicationException e) {
            bindingResult.rejectValue("name", "duplicate_name", e.getMessage());
            return "repairUpdate";
        }
        return "redirect:/repairs/" + afterUpdate.getId();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable long id) {
        try {
            repairService.delete(id);
        } catch (InvalidParamException e) {
            throw new EntityNotFoundException("Repair", id);
        }
        return "redirect:/repairs";
    }

}
