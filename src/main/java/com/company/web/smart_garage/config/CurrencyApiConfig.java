package com.company.web.smart_garage.config;

import com.posadskiy.currencyconverter.CurrencyConverter;
import com.posadskiy.currencyconverter.config.ConfigBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrencyApiConfig {

    @Value("${currency.converter.api-key}")
    private String CURRENCY_LAYER_API_KEY;

    @Bean
    public CurrencyConverter converter() {
        return new CurrencyConverter(new ConfigBuilder()
                .currencyLayerApiKey(CURRENCY_LAYER_API_KEY)
                .build());
    }
}