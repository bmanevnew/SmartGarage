package com.company.web.smart_garage.controllers.mvc;

import com.company.web.smart_garage.data_transfer_objects.filters.RepairFilterOptionsDto;
import com.company.web.smart_garage.exceptions.EntityNotFoundException;
import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.exceptions.UnauthorizedOperationException;
import com.company.web.smart_garage.models.Repair;
import com.company.web.smart_garage.services.RepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/repairs")
public class RepairsMvcController {

    public static final int DEFAULT_PAGE_SIZE = 5;
    private final RepairService repairService;

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
}
