package com.example.demo.Controller;

import com.example.demo.Model.CustomBooking;
import com.example.demo.Model.Vendor;
import com.example.demo.Model.VendorDriver;
import com.example.demo.Model.VendorVehicle;
import com.example.demo.Repository.VendorRepository;
import com.example.demo.Service.VendorVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/vendors/{vendorId}/vehicles")  
public class VendorVehicleController {

    @Autowired
    private VendorVehicleService vendorVehicleService;

    @Autowired
    private VendorRepository vendorRepository;

    @GetMapping
    public ResponseEntity<List<VendorVehicle>> getVehiclesByVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok(vendorVehicleService.getVehiclesByVendor(vendorId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createVehicle(@PathVariable Long vendorId, @ModelAttribute VendorVehicle vendorVehicle,
            @RequestParam(value = "rCImageFile", required = false) MultipartFile rCImageFile,
            @RequestParam(value = "vehicleNoImageFile", required = false) MultipartFile vehicleNoImageFile,
            @RequestParam(value = "insuranceImageFile", required = false) MultipartFile insuranceImageFile,
            @RequestParam(value = "permitImageFile", required = false) MultipartFile permitImageFile,
            @RequestParam(value = "authorizationImageFile", required = false) MultipartFile authorizationImageFile,
            @RequestParam(value = "cabNoPlateImageFile", required = false) MultipartFile cabNoPlateImageFile,
            @RequestParam(value = "cabImageFile", required = false) MultipartFile cabImageFile,
            @RequestParam(value = "cabFrontImageFile", required = false) MultipartFile cabFrontImageFile,
            @RequestParam(value = "cabBackImageFile", required = false) MultipartFile cabBackImageFile,
            @RequestParam(value = "cabSideImageFile", required = false) MultipartFile cabSideImageFile)
            throws IOException {
        Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
        if (vendor == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid vendor ID");
        }
        vendorVehicle.setVendor(vendor);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vendorVehicleService.createVehicle(vendorVehicle, rCImageFile, vehicleNoImageFile,
                        insuranceImageFile, permitImageFile, authorizationImageFile, cabNoPlateImageFile, cabImageFile,
                        cabFrontImageFile, cabBackImageFile, cabSideImageFile));
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<VendorVehicle> getVehicleById(@PathVariable Integer vehicleId) {
        return vendorVehicleService.getVehicleById(vehicleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{vehicleId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VendorVehicle> updateVehicle(@PathVariable Integer vehicleId,
            @ModelAttribute VendorVehicle vehicleDetails,
            @RequestParam(value = "rCImageFile", required = false) MultipartFile rCImageFile,
            @RequestParam(value = "vehicleNoImageFile", required = false) MultipartFile vehicleNoImageFile,
            @RequestParam(value = "insuranceImageFile", required = false) MultipartFile insuranceImageFile,
            @RequestParam(value = "permitImageFile", required = false) MultipartFile permitImageFile,
            @RequestParam(value = "authorizationImageFile", required = false) MultipartFile authorizationImageFile,
            @RequestParam(value = "cabNoPlateImageFile", required = false) MultipartFile cabNoPlateImageFile,
            @RequestParam(value = "cabImageFile", required = false) MultipartFile cabImageFile,
            @RequestParam(value = "cabFrontImageFile", required = false) MultipartFile cabFrontImageFile,
            @RequestParam(value = "cabBackImageFile", required = false) MultipartFile cabBackImageFile,
            @RequestParam(value = "cabSideImageFile", required = false) MultipartFile cabSideImageFile)
            throws IOException {
        return ResponseEntity.ok(vendorVehicleService.updateVehicle(vehicleId, vehicleDetails, rCImageFile,
                vehicleNoImageFile, insuranceImageFile, permitImageFile, authorizationImageFile, cabNoPlateImageFile,
                cabImageFile, cabFrontImageFile, cabBackImageFile, cabSideImageFile));
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Integer vehicleId) {
        vendorVehicleService.deleteVehicle(vehicleId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<VendorVehicle> changeStatus(@PathVariable int id,
            @RequestBody Map<String, String> requestBody) {

        String status = requestBody.get("status");

        try {
            VendorVehicle updatedOrder = vendorVehicleService.updateStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{bookingId}/assignVendorCab/{vendorCabId}")

    public ResponseEntity<CustomBooking> assignVendorCabToBooking(
            @PathVariable int bookingId,
            @PathVariable int vendorCabId) {   

        // Call the service method to assign vendor
        CustomBooking updatedBooking = vendorVehicleService.assignVendorCabToBooking(bookingId, vendorCabId);

        if (updatedBooking == null) {
            // If the booking or vendor was not found, return a 404 Not Found
            return ResponseEntity.notFound().build();
        }

        // if (updatedBooking.getVendor() == null || updatedBooking.getVendorCab() ==
        // null) {
        // System.out.println("Vendor is not assigned");
        // } else {
        // String subject = "Booking Confirmation - " + updatedBooking.getBookid();
        // String message = "<!DOCTYPE html>"
        // + "<html lang='en'>"
        // + "<head>"
        // + "<meta charset='UTF-8'>"
        // + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
        // + "<title>Booking Confirmation</title>"
        // + "</head>"
        // + "<body style='font-family: Arial, sans-serif; background-color: #f7f7f7;
        // margin: 0; padding: 0;'>"
        // + "<div style='max-width: 600px; margin: 20px auto; background-color:
        // #ffffff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        // overflow: hidden;'>"
        // + "<div style='background-color: #007BFF; color: #ffffff; padding: 20px;
        // text-align: center;'>"
        // + "<h1 style='margin: 0; font-size: 24px; font-weight: bold;'>Booking
        // Confirmation</h1>"
        // + "</div>"
        // + "<div style='padding: 20px;'>"
        // + "<h3 style='color: #007BFF; font-size: 20px; margin-bottom: 20px;'>Hello "
        // + updatedBooking.getName() + ",</h3>"
        // + "<p style='font-size: 16px; line-height: 1.5; color: #333333;
        // margin-bottom: 20px;'>Your booking has been confirmed. Below are the details
        // of your booking:</p>"
        // + "<div style='margin-top: 20px;'>"
        // + "<ul style='list-style-type: none; padding: 0;'>"
        // + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong
        // style='color: #007BFF;'>Booking ID:</strong> "
        // + updatedBooking.getBookid() + "</li>"
        // + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong
        // style='color: #007BFF;'>Pickup Location:</strong> "
        // + updatedBooking.getUserPickup() + "</li>"
        // + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong
        // style='color: #007BFF;'>Drop Location:</strong> "
        // + updatedBooking.getUserDrop() + "</li>"
        // + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong
        // style='color: #007BFF;'>Trip Type:</strong> "
        // + updatedBooking.getTripType() + "</li>"
        // + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong
        // style='color: #007BFF;'>Date:</strong> "
        // + updatedBooking.getDate() + "</li>"
        // + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong
        // style='color: #007BFF;'>Time:</strong> "
        // + updatedBooking.getTime() + "</li>"
        // + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong
        // style='color: #007BFF;'>Amount Paid:</strong> ₹"
        // + updatedBooking.getAmount() + "</li>"
        // + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong
        // style='color: #007BFF;'>Cab Name:</strong> "
        // + updatedBooking.getVendorCab().getCarName() + "</li>"
        // + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong
        // style='color: #007BFF;'>Vehicle No:</strong> "
        // + updatedBooking.getVendorCab().getVehicleNo() + "</li>"
        // + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong
        // style='color: #007BFF;'>Driver Name:</strong> "
        // + updatedBooking.getVendorDriver().getDriverName() + "</li>"
        // + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong
        // style='color: #007BFF;'>Driver Contact:</strong> "
        // + updatedBooking.getVendorDriver().getContactNo() + "</li>"
        // + "</ul>"
        // + "</div>"
        // + "<p style='font-size: 16px; line-height: 1.5; color: #333333; margin-top:
        // 20px;'>Thank you for choosing us! We wish you a safe and pleasant
        // journey.</p>"
        // + "</div>"
        // + "<div style='text-align: center; padding: 20px; background-color: #f1f1f1;
        // color: #777777; font-size: 14px;'>"
        // + "<p style='margin: 0;'>If you have any questions, feel free to contact us
        // at <a href='mailto:support@example.com' style='color: #007BFF;
        // text-decoration: none;'>support@example.com</a>.</p>"
        // + "<img
        // src='https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExcjc1OGk0ZGVqNHFseDRrM3FvOW0xYnVyenJkcmQ2OXNsODE0djUzZyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/3oKIPhUfA1h2U2Koko/giphy.gif'
        // alt='Namaskar' style='width: 100px; height: auto; margin-top: 10px;'>"
        // + "</div>"
        // + "</div>"
        // + "</body>"
        // + "</html>";
        // boolean emailSent = emailService.sendEmail(message, subject,
        // updatedBooking.getEmail());

        // if (emailSent) {
        // System.out.println("Booking confirmation email sent successfully.");
        // } else {
        // System.out.println("Failed to send booking confirmation email.");
        // }

        // }

        return ResponseEntity.ok(updatedBooking);
    }
}
