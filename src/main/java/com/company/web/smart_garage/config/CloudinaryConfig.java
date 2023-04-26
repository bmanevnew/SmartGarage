package com.company.web.smart_garage.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    public static final String CLOUD_NAME_KEY = "cloud_name";
    public static final String CLOUD_API_KEY = "api_key";
    public static final String API_SECRET_KEY = "api_secret";
    @Value("${cloudinary.cloud-name}")
    private String CLOUD_NAME;
    @Value("${cloudinary.api-key}")
    private String API_KEY;
    @Value("${cloudinary.api-secret}")
    private String API_SECRET;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put(CLOUD_NAME_KEY, CLOUD_NAME);
        config.put(CLOUD_API_KEY, API_KEY);
        config.put(API_SECRET_KEY, API_SECRET);
        return new Cloudinary(config);
    }
}
