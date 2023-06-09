package com.company.web.smart_garage.data_transfer_objects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class VehicleDtoCreate {
    @NotNull(message = VEHICLE_PLATE_REQUIRED)
    @Pattern(regexp = VEHICLE_LICENSE_PLATE_REGEX, message = VEHICLE_PLATE_INVALID_FORMAT)
    private String licensePlate;

    @NotNull(message = VEHICLE_VIN_REQUIRED)
    @Pattern(regexp = VEHICLE_VIN_REGEX, message = VEHICLE_VIN_INVALID_FORMAT)
    private String vin;

    @NotNull(message = VEHICLE_PROD_YEAR_REQUIRED)
    private String productionYear;

    @NotNull(message = VEHICLE_MODEL_REQUIRED)
    @Size(min = 2, max = 32, message = VEHICLE_MODEL_INVALID_SIZE)
    private String model;

    @NotNull(message = VEHICLE_BRAND_REQUIRED)
    @Size(min = 2, max = 32, message = VEHICLE_BRAND_INVALID_SIZE)
    private String brand;

    @NotNull(message = EMAIL_IS_REQUIRED)
    @Email(message = USER_EMAIL_INVALID)
    private String ownerUsernameOrEmail;
}
