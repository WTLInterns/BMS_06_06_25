package com.example.demo.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "whatsapp")
public class WhatsAppConfig {

    @Value("${whatsapp.api.url:}")
    private String apiUrl;

    @Value("${whatsapp.access.token:}")
    private String accessToken;

    @Value("${whatsapp.phone.number.id:}")
    private String phoneNumberId;


    public String getApiUrl() {
        return apiUrl;
    }


    public String getAccessToken() {
        return accessToken;
    }


    public String getPhoneNumberId() {
        return phoneNumberId;
    }

}