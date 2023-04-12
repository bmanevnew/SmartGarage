package com.company.web.smart_garage.models.repair;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.company.web.smart_garage.utils.Constants.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepairDto {

    @NotNull(message = NAME_IS_REQUIRED)
    @Size(min = 2, max = 32, message = REPAIR_NAME_INVALID_SIZE)
    private String name;

    @NotNull(message = PRICE_IS_REQUIRED)
    private Double price;
}
