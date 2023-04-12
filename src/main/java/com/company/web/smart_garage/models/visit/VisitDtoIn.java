package com.company.web.smart_garage.models.visit;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisitDtoIn {

    @NotNull
    @Positive
    private Long userId;
    @NotNull
    @Positive
    private Long vehicleId;
    private Set<Long> repairIds;
}
