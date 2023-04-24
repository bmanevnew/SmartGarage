package com.company.web.smart_garage.data_transfer_objects;

import com.posadskiy.currencyconverter.enums.Currency;
import jakarta.validation.constraints.NotNull;


public class CurrencyExportDTO {

    @NotNull
    private Currency currency;

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

}