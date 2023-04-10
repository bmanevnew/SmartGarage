package com.company.web.smart_garage.models.vehicle;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.company.web.smart_garage.utils.Constants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {
    @NotNull(message = VEHICLE_PLATE_REQUIRED)
    @Pattern(regexp = "^[ABEKMHOPCTYX]{1,2} \\d{4} [ABEKMHOPCTYX]{2}$", message = VEHICLE_PLATE_INVALID_FORMAT)
    private String licensePlate;

    @NotNull(message = VEHICLE_VIN_REQUIRED)
    @Pattern(regexp = "^[A-Z\\d]{17}$", message = VEHICLE_VIN_INVALID_FORMAT)
    private String vin;

    @NotNull(message = VEHICLE_PROD_YEAR_REQUIRED)
    private Integer productionYear;

    @NotNull(message = VEHICLE_MODEL_REQUIRED)
    @Size(min = 2, max = 32, message = VEHICLE_MODEL_INVALID_SIZE)
    private String model;

    @NotNull(message = VEHICLE_BRAND_REQUIRED)
    @Size(min = 2, max = 32, message = VEHICLE_BRAND_INVALID_SIZE)
    private String brand;
}
