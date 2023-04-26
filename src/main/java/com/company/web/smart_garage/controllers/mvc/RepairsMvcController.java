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

import static com.company.web.smart_garage.utils.Constants.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/repairs")
public class RepairsMvcController {

    public static final int DEFAULT_PAGE_SIZE = 5;
    public static final String REPAIR_KEY = "repair";
    public static final String REPAIR_VIEW = "repair";
    public static final String ALL_REPAIRS_VIEW = "allRepairs";
    private final RepairService repairService;
    private final RepairMapper repairMapper;

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
    public String getById(@PathVariable long id, Model model) {
        Repair repair = repairService.getById(id);
        model.addAttribute(REPAIR_KEY, repair);
        return REPAIR_VIEW;
    }

    @GetMapping
    public String getAll(@ModelAttribute(REPAIR_FILTER_OPTIONS) RepairFilterOptionsDto filter,
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
            viewModel.addAttribute(PARAM_ERROR, e.getMessage());
            return ALL_REPAIRS_VIEW;
        } catch (NumberFormatException e) {
            viewModel.addAttribute(PARAM_ERROR, INVALID_PRICE);
            return ALL_REPAIRS_VIEW;
        }
        viewModel.addAttribute(REPAIRS_KEY, repairs);
        return ALL_REPAIRS_VIEW;
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping("/create")
    public String getCreatePage(Model model) {
        model.addAttribute(REPAIR_DTO_CREATE, new RepairDtoStrings());
        return REPAIR_CREATE_VIEW;
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute(REPAIR_DTO_CREATE) RepairDtoStrings dto,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return REPAIR_CREATE_VIEW;
        }
        Repair repair;
        Repair repairFromDto;
        try {
            repairFromDto = repairMapper.dtoToRepair(dto);
        } catch (NumberFormatException e) {
            bindingResult.rejectValue(PRICE_FIELD, PRICE_ERROR, PRICE_IS_INVALID);
            return REPAIR_CREATE_VIEW;
        }
        try {
            repair = repairService.create(repairFromDto);
        } catch (InvalidParamException e) {
            bindingResult.rejectValue(PRICE_FIELD, PRICE_ERROR, e.getMessage());
            return REPAIR_CREATE_VIEW;
        } catch (EntityDuplicationException e) {
            bindingResult.rejectValue(NAME_FIELD, DUPLICATE_NAME, e.getMessage());
            return REPAIR_CREATE_VIEW;
        }
        return "redirect:/repairs/" + repair.getId();
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping("/{id}/update")
    public String getUpdatePage(@PathVariable long id, Model model) {
        Repair repair = repairService.getById(id);
        model.addAttribute(REPAIR_DTO_KEY, repairMapper.repairToDto(repair));
        model.addAttribute(REPAIR_KEY, repair);
        return REPAIR_UPDATE_VIEW;
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @PostMapping("/{id}/update")
    public String update(@Valid @ModelAttribute(REPAIR_DTO_KEY) RepairDto dto,
                         BindingResult bindingResult,
                         @PathVariable long id,
                         Model model) {
        model.addAttribute(REPAIR_KEY, repairService.getById(id));
        if (bindingResult.hasErrors()) {
            return REPAIR_UPDATE_VIEW;
        }
        Repair repair = repairMapper.dtoToRepair(dto, id);
        Repair afterUpdate;
        try {
            afterUpdate = repairService.update(repair);
        } catch (InvalidParamException e) {
            bindingResult.rejectValue(PRICE_FIELD, PRICE_ERROR, e.getMessage());
            return REPAIR_UPDATE_VIEW;
        } catch (EntityDuplicationException e) {
            bindingResult.rejectValue(NAME_FIELD, DUPLICATE_NAME, e.getMessage());
            return REPAIR_UPDATE_VIEW;
        }
        return "redirect:/repairs/" + afterUpdate.getId();
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
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
