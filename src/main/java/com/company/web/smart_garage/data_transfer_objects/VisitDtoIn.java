package com.company.web.smart_garage.data_transfer_objects;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import static com.company.web.smart_garage.utils.Constants.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisitDtoIn {

    @NotNull(message = USER_ID_IS_REQUIRED)
    @Positive(message = USER_ID_MUST_BE_POSITIVE)
    private Long userId;
    @NotNull(message = VEHICLE_ID_IS_REQUIRED)
    @Positive(message = VEHICLE_ID_MUST_BE_POSITIVE)
    private Long vehicleId;
    private Set<Long> repairIds;
}
