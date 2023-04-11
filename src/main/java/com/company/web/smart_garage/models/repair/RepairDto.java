package com.company.web.smart_garage.models.repair;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepairDto {

    @NotNull
    @Size(min = 2, max = 32)
    private String name;

    @NotNull
    private Double price;
}
