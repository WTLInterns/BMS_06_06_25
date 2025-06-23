package com.example.demo.Service;

import com.example.demo.Config.WhatsAppConfig;
import com.example.demo.DTO.WhatsAppMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class WhatsAppService {

    @Autowired
    private WhatsAppConfig whatsAppConfig;

    @Autowired
    private RestTemplate restTemplate;

    public boolean sendTextMessage(String message, String recipientPhoneNumber) {
        try {
            if (whatsAppConfig == null) {
                System.err.println("WhatsAppConfig is null - dependency injection failed!");
                return false;
            }

            String formattedNumber = formatPhoneNumber(recipientPhoneNumber);
            System.out.println("Sending WhatsApp to: " + formattedNumber);

            WhatsAppMessageDTO messageDTO = new WhatsAppMessageDTO(formattedNumber, message);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(whatsAppConfig.getAccessToken());

            HttpEntity<WhatsAppMessageDTO> request = new HttpEntity<>(messageDTO, headers);

            String apiUrl = whatsAppConfig.getApiUrl().trim();
            String phoneNumberId = whatsAppConfig.getPhoneNumberId().trim();
            String url = apiUrl + "/" + phoneNumberId + "/messages";

            System.out.println("Final WhatsApp URL: '" + url + "'");

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                System.err.println("Error: URL doesn't start with http:// or https://");
                return false;
            }

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            return response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED;

        } catch (Exception e) {
            System.err.println("WhatsApp Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String formatPhoneNumber(String phoneNumber) {
        String cleaned = phoneNumber.replaceAll("[^0-9]", "");

        if (cleaned.startsWith("91")) {
            return cleaned;
        }

        if (cleaned.startsWith("0")) {
            return "91" + cleaned.substring(1);
        }

        if (cleaned.length() == 10) {
            return "91" + cleaned;
        }

        return cleaned;
    }
}
