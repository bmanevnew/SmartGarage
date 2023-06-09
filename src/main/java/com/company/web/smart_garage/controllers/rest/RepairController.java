package com.company.web.smart_garage.controllers.rest;

import com.company.web.smart_garage.data_transfer_objects.RepairDto;
import com.company.web.smart_garage.models.Repair;
import com.company.web.smart_garage.services.RepairService;
import com.company.web.smart_garage.utils.mappers.RepairMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/repairs")
public class RepairController {

    private final RepairService repairService;
    private final RepairMapper repairMapper;

    @GetMapping("/{id}")
    public RepairDto getById(@PathVariable long id) {
        return repairMapper.repairToDto(repairService.getById(id));
    }

    @GetMapping
    public List<RepairDto> getAll(@RequestParam(required = false, name = "name") String name,
                                  @RequestParam(required = false, name = "priceFrom") Double priceFrom,
                                  @RequestParam(required = false, name = "priceTo") Double priceTo,
                                  Pageable pageable) {
        return repairService.getAll(name, priceFrom, priceTo, pageable)
                .map(repairMapper::repairToDto).getContent();
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @PostMapping
    public RepairDto create(@Valid @RequestBody RepairDto dto) {
        Repair repair = repairMapper.dtoToRepair(dto);
        return repairMapper.repairToDto(repairService.create(repair));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @PutMapping("/{id}")
    public RepairDto update(@Valid @RequestBody RepairDto dto, @PathVariable long id) {
        Repair repair = repairMapper.dtoToRepair(dto, id);
        return repairMapper.repairToDto(repairService.update(repair));
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public RepairDto delete(@PathVariable long id) {
        return repairMapper.repairToDto(repairService.delete(id));
    }
}
