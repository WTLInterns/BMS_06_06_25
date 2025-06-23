package com.example.demo.Controller;

import com.example.demo.Model.Vendor;
import com.example.demo.Model.CustomBooking;
import com.example.demo.Service.VendorService;
import com.example.demo.Service.CustomBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.Service.VendorPasswordResetService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    @Autowired
    private VendorPasswordResetService vendorPasswordResetService;

    @Autowired
    private CustomBookingService bookingService;

    // Vendor forgot password: request OTP
    @PostMapping("/forgot-password/request")
    public ResponseEntity<?> requestForgotPassword(@RequestParam String email) {
        try {
            vendorPasswordResetService.sendResetOTP(email);
            return ResponseEntity.ok("OTP sent to email: " + email);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP request failed: " + e.getMessage());
        }
    }

    // Vendor forgot password: verify OTP and reset password
    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<?> verifyOtpAndResetPassword(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {
        boolean valid = vendorPasswordResetService.verifyOTP(email, otp);
        if (valid) {
            try {
                vendorPasswordResetService.resetPassword(email, newPassword);
                return ResponseEntity.ok("Password reset successful.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password reset failed: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
        }
    }

    // Vendor forgot password: reset password
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetForgotPassword(@RequestParam String email, @RequestParam String newPassword) {
        try {
            vendorPasswordResetService.resetPassword(email, newPassword);
            return ResponseEntity.ok("Password reset successful.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password reset failed: " + e.getMessage());
        }
    }

    //Changes From Here
    
    // Get all bookings for vendor
    @GetMapping("/{vendorId}/bookings")
    public ResponseEntity<List<CustomBooking>> getVendorBookings(@PathVariable Long vendorId) {
        return ResponseEntity.ok(bookingService.getBookingsByVendor(vendorId));
    }

    // Get bookings by status for vendor
    @GetMapping("/{vendorId}/bookings/status/{status}")
    public ResponseEntity<List<CustomBooking>> getVendorBookingsByStatus(
            @PathVariable Long vendorId,
            @PathVariable String status) {
        return ResponseEntity.ok(bookingService.getBookingsByVendorAndStatus(vendorId, status));
    }

    // Get booking statistics for vendor
    @GetMapping("/{vendorId}/bookings/stats")
    public ResponseEntity<Map<String, Long>> getVendorBookingStats(@PathVariable Long vendorId) {
        return ResponseEntity.ok(bookingService.getVendorBookingStats(vendorId));
    }





    // Get total revenue for vendor
    @GetMapping("/{vendorId}/bookings/revenue")
    public ResponseEntity<Double> getVendorTotalRevenue(@PathVariable Long vendorId) {
        double totalRevenue = bookingService.calculateTotalRevenue(vendorId);
        return ResponseEntity.ok(totalRevenue);
    }




    
    // Update booking status (only for vendor's own bookings)
    @PostMapping("/{vendorId}/bookings/{bookingId}/status")
    public ResponseEntity<?> updateBookingStatus(
            @PathVariable Long vendorId,
            @PathVariable Integer bookingId,
            @RequestParam String status) {
        try {
            // Validate status
            if (!Arrays.asList("pending", "ongoing", "completed", "cancelled").contains(status.toLowerCase())) {
                return ResponseEntity.badRequest().body("Invalid status value");
            }

            // Get booking and verify it belongs to this vendor
            CustomBooking booking = bookingService.findById(bookingId);
            if (booking == null) {
                return ResponseEntity.notFound().build();
            }
            if (!booking.getVendor().getId().equals(vendorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied - not your booking");
            }

            // Update status
            bookingService.updateBookingStatus(bookingId, status);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating booking status: " + e.getMessage());
        }
    }

    // Get booking details
    @GetMapping("/{vendorId}/bookings/{bookingId}")
    public ResponseEntity<CustomBooking> getBookingDetails(
            @PathVariable Long vendorId,
            @PathVariable Integer bookingId) {
        try {
            CustomBooking booking = bookingService.findById(bookingId);
            if (booking == null) {
                return ResponseEntity.notFound().build();
            }
            if (!booking.getVendor().getId().equals(vendorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(booking);
            }
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching booking details: " + e.getMessage());
        }
    }

    //Till Here

    @Autowired
    private com.example.demo.Service.MasterAdminService masterAdminService;

    /**
     * Handles vendor onboarding via a dedicated onboarding form.
     * The masterAdminId is provided as a path variable, so the vendor does not need to know it.
     *
     * @param masterAdminId the ID of the master admin to associate the vendor with
     * @param vendorForm    the vendor details submitted from the form
     * @return ResponseEntity with the saved Vendor or an error message
     */
    @PostMapping("/onboard/{masterAdminId}")
    public ResponseEntity<?> onboardVendor(@PathVariable Long masterAdminId, @RequestBody com.example.demo.Model.VendorForm vendorForm) {
        com.example.demo.Model.MasterAdmin masterAdmin = masterAdminService.getMasterAdminById(masterAdminId).orElse(null);
        if (masterAdmin == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid master admin ID: " + masterAdminId);
        }
        try {
            Vendor vendor = buildVendorFromForm(vendorForm, masterAdmin);
            Vendor saved = vendorService.createVendor(vendor, null, null, null, null, null, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to onboard vendor: " + e.getMessage());
        }
    }

    /**
     * Utility method to build a Vendor entity from a VendorForm and MasterAdmin.
     */
    private Vendor buildVendorFromForm(com.example.demo.Model.VendorForm vendorForm, com.example.demo.Model.MasterAdmin masterAdmin) {
        Vendor vendor = new Vendor();
        vendor.setVendorFullName(vendorForm.getVendorFullName());
        vendor.setVendorCompanyName(vendorForm.getVendorCompanyName());
        vendor.setContactNo(vendorForm.getContactNo());
        vendor.setAlternateMobileNo(vendorForm.getAlternateMobileNo());
        vendor.setCity(vendorForm.getCity());
        vendor.setVendorEmail(vendorForm.getVendorEmail());
        vendor.setBankName(vendorForm.getBankName());
        vendor.setBankAccountNo(vendorForm.getBankAccountNo());
        vendor.setIfscCode(vendorForm.getIfscCode());
        vendor.setAadharNo(vendorForm.getAadharNo());
        vendor.setPanNo(vendorForm.getPanNo());
        vendor.setGstNo(vendorForm.getGstNo());
        vendor.setUdyogAadharNo(vendorForm.getUdyogAadharNo());
        vendor.setVendorOtherDetails(vendorForm.getVendorOtherDetails());
        vendor.setStatus(vendorForm.getStatus());
        vendor.setPassword(vendorForm.getPassword() != null ? vendorForm.getPassword() : vendorForm.getContactNo());
        vendor.setMasterAdmin(masterAdmin);
        return vendor;
    }

    /**
     * @deprecated Use {@link #onboardVendor(Long, VendorForm)} instead. This endpoint expects masterAdminId in the request body.
     */
    @Deprecated
    @PostMapping("/onboard")
    public ResponseEntity<?> onboardVendorOld(@RequestBody com.example.demo.Model.VendorForm vendorForm) {
        try {
            Long masterAdminId = vendorForm.getMasterAdminId();
            com.example.demo.Model.MasterAdmin masterAdmin = masterAdminService.getMasterAdminById(masterAdminId).orElse(null);
            if (masterAdmin == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid master admin ID");
            }
            Vendor vendor = new Vendor();
            vendor.setVendorFullName(vendorForm.getVendorFullName());
            vendor.setVendorCompanyName(vendorForm.getVendorCompanyName());
            vendor.setContactNo(vendorForm.getContactNo());
            vendor.setAlternateMobileNo(vendorForm.getAlternateMobileNo());
            vendor.setCity(vendorForm.getCity());
            vendor.setVendorEmail(vendorForm.getVendorEmail());
            vendor.setBankName(vendorForm.getBankName());
            vendor.setBankAccountNo(vendorForm.getBankAccountNo());
            vendor.setIfscCode(vendorForm.getIfscCode());
            vendor.setAadharNo(vendorForm.getAadharNo());
            vendor.setPanNo(vendorForm.getPanNo());
            vendor.setGstNo(vendorForm.getGstNo());
            vendor.setUdyogAadharNo(vendorForm.getUdyogAadharNo());
            vendor.setVendorOtherDetails(vendorForm.getVendorOtherDetails());
            vendor.setStatus(vendorForm.getStatus());
            vendor.setPassword(vendorForm.getPassword() != null ? vendorForm.getPassword() : vendorForm.getContactNo());
            vendor.setMasterAdmin(masterAdmin);
            Vendor saved = vendorService.createVendor(vendor, null, null, null, null, null, null);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }




    @Autowired
    private VendorService vendorService;

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVendor(
            @PathVariable Long id,
            @ModelAttribute Vendor vendor,
            @RequestParam(value = "vendorImage", required = false) MultipartFile vendorImage,
            @RequestParam(value = "gstNoImage", required = false) MultipartFile gstNoImage,
            @RequestParam(value = "govtApprovalCertificate", required = false) MultipartFile govtApprovalCertificate,
            @RequestParam(value = "vendorDocs", required = false) MultipartFile vendorDocs,
            @RequestParam(value = "aadharPhoto", required = false) MultipartFile aadharPhoto,
            @RequestParam(value = "panPhoto", required = false) MultipartFile panPhoto
    ) {
        try {
            Vendor updated = vendorService.updateVendor(id, vendor, vendorImage, gstNoImage, govtApprovalCertificate, vendorDocs, aadharPhoto, panPhoto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginVendor(@RequestParam String email, @RequestParam String password) {
        var response = vendorService.loginVendor(email, password);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        return ResponseEntity.ok(response);
    }

    
}

