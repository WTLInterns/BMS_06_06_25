package com.example.demo.Service;

import com.example.demo.Model.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VendorEmailService {

    @Autowired
    private EmailService emailService;

    // Send a custom HTML email (for onboarding invitations, etc.)
    public boolean sendCustomHtmlEmail(String message, String subject, String to) {
        return emailService.sendHtmlEmail(message, subject, to);
    }

    // Sends the vendor's credentials via HTML email
    public boolean sendVendorCredentials(Vendor vendor) {
        String fullName = capitalize(vendor.getVendorFullName());
        String message = "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f0f2f5; padding: 30px; color: #333;'>" +
                "<div style='max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 12px; box-shadow: 0 0 10px rgba(0,0,0,0.1);'>" +
                "<h2 style='color: #2c3e50; margin-bottom: 10px;'>Hello " + fullName + ",</h2>" +
                "<p style='font-size: 16px; margin: 15px 0;'><strong>Welcome to WTL Tourism Pvt Ltd!</strong></p>" +
                "<p style='font-size: 15px; margin: 15px 0;'>We're excited to have you on board as a <strong>Vendor</strong>. Your account has been created successfully. Below are your login credentials:</p>" +
                "<p style='font-size: 15px; margin: 15px 0;'><strong>Email:</strong> " + vendor.getVendorEmail() + "<br>" +
                "<strong>Password:</strong> " + vendor.getPassword() + "</p>" +
                "<p style='font-size: 15px; margin: 15px 0;'>Login URL: <a href='https://yourplatform.com' style='color: #007bff; text-decoration: none;'>https://yourplatform.com</a></p>" +
                "<p style='font-size: 15px; margin: 15px 0;'><strong>Please change your password after your first login</strong> to ensure account security.</p>" +
                "<p style='font-size: 15px; margin: 15px 0;'>If you face any issues, feel free to contact our support team at any time.</p>" +
                "<p style='font-size: 15px; margin-top: 30px;'>Thanks & Regards,<br><strong>WTL Tourism Pvt Ltd</strong></p>" +
                "</div>" +
                "</body>" +
                "</html>";
        String to = vendor.getVendorEmail();
        String subject = "Your Vendor Account Details";
        return emailService.sendHtmlEmail(message, subject, to);
    }

    // Utility method to capitalize a string properly.
    private String capitalize(String str) {
        if (str == null || str.isEmpty())
            return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
