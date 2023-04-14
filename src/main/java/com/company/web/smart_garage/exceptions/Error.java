package com.company.web.smart_garage.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Error {
    private LocalDateTime dateTime;
    private String errorMessage;
    private String details;
}
