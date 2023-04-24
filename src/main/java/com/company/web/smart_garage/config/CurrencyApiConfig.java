package com.company.web.smart_garage.config;

import com.posadskiy.currencyconverter.CurrencyConverter;
import com.posadskiy.currencyconverter.config.Config;
import com.posadskiy.currencyconverter.config.ConfigBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrencyApiConfig {

    public static final String CURRENCY_LAYER = "EydbHQ87oMXgLSLVPFx6CXWqJejTQUUM";

    @Bean
    public CurrencyConverter converter() {
        return new CurrencyConverter(new ConfigBuilder()
                .currencyLayerApiKey(CURRENCY_LAYER)
                .build());

    }


}