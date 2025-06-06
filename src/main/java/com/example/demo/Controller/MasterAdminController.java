package com.example.demo.Controller;

import com.example.demo.Model.MasterAdmin;
import com.example.demo.Service.MasterAdminService;
import com.example.demo.Model.Vendor;
import com.example.demo.Model.VendorForm;
import com.example.demo.Service.VendorService;
import com.example.demo.Repository.MasterAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.Service.MasterAdminPasswordResetService;
import com.example.demo.Service.VendorEmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/masteradmins")
public class MasterAdminController {

    @Autowired
    private MasterAdminPasswordResetService passwordResetService;


    @Autowired
    private MasterAdminService masterAdminService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private MasterAdminRepository masterAdminRepository;

    @Autowired
    private VendorEmailService vendorEmailService;

    // --- Vendor CRUD (except update) ---
    @PostMapping("/vendors")
    public ResponseEntity<?> createVendor(
        @ModelAttribute VendorForm vendorForm,
        @RequestParam(value = "vendorImage", required = false) MultipartFile vendorImage,
        @RequestParam(value = "gstNoImage", required = false) MultipartFile gstNoImage,
        @RequestParam(value = "govtApprovalCertificate", required = false) MultipartFile govtApprovalCertificate,
        @RequestParam(value = "vendorDocs", required = false) MultipartFile vendorDocs,
        @RequestParam(value = "aadharPhoto", required = false) MultipartFile aadharPhoto,
        @RequestParam(value = "panPhoto", required = false) MultipartFile panPhoto
    ) {
        try {
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
            // Set password: if not provided, use contactNo
            String password = vendorForm.getPassword();
            if (password == null || password.trim().isEmpty()) {
                password = vendorForm.getContactNo();
            }
            vendor.setPassword(password);

            MasterAdmin masterAdmin = null;
            if (vendorForm.getMasterAdminId() != null) {
                masterAdmin = masterAdminRepository.findById(vendorForm.getMasterAdminId()).orElse(null);
                vendor.setMasterAdmin(masterAdmin);
            }
            Vendor saved = vendorService.createVendor(vendor, vendorImage, gstNoImage, govtApprovalCertificate, vendorDocs, aadharPhoto, panPhoto);

            // Compose response as { vendor: ..., masterAdmin: ... }
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("vendor", saved);
            response.put("masterAdmin", masterAdmin);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/vendors")
    public ResponseEntity<List<Vendor>> getAllVendors() {
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    @GetMapping("/vendors/{id}")
    public ResponseEntity<?> getVendorById(@PathVariable Long id) {
        return vendorService.getVendorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createMasterAdmin(@ModelAttribute com.example.demo.Model.MasterAdminForm form, @RequestParam(value = "profileImg", required = false) MultipartFile profileImg) {
        try {
            // Map form to entity
            MasterAdmin masterAdmin = new MasterAdmin();
            masterAdmin.setFullName(form.getFullName());
            masterAdmin.setEmail(form.getEmail());
            masterAdmin.setPassword(form.getPassword());
            // Set other fields if needed

            MasterAdmin saved = masterAdminService.createMasterAdmin(masterAdmin, profileImg);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<MasterAdmin>> getAllMasterAdmins() {
        return ResponseEntity.ok(masterAdminService.getAllMasterAdmins());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MasterAdmin> getMasterAdminById(@PathVariable Long id) {
        return masterAdminService.getMasterAdminById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMasterAdmin(
        @PathVariable Long id,
        @ModelAttribute com.example.demo.Model.MasterAdminForm form, // Use the DTO, not the entity
        @RequestParam(value = "profileImg", required = false) MultipartFile profileImg // File field only
    ) {
        try {
            MasterAdmin updated = masterAdminService.updateMasterAdmin(id, form, profileImg);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")

    // Send vendor login details via email
    @PostMapping("/vendors/send-login-details/{vendorEmail}")
    public ResponseEntity<?> sendVendorLoginDetails(@PathVariable String vendorEmail) {
        Vendor vendor = vendorService.findByEmail(vendorEmail);
        if (vendor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");
        }
        boolean sent = vendorEmailService.sendVendorCredentials(vendor);
        return sent ? ResponseEntity.ok("Login details sent to vendor.") : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email.");
    }

    // --- Password Reset (OTP) Endpoints ---
    @PostMapping("/password-reset/request-otp")
    public ResponseEntity<?> requestPasswordResetOtp(@RequestParam String email) {
        try {
            passwordResetService.sendResetOTP(email);
            return ResponseEntity.ok("OTP sent to email.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/password-reset/verify-otp")
    public ResponseEntity<?> verifyPasswordResetOtp(@RequestParam String email, @RequestParam String otp) {
        boolean valid = passwordResetService.verifyOTP(email, otp);
        if (valid) {
            return ResponseEntity.ok("OTP verified.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
        }
    }

    @PostMapping("/password-reset/reset")
    public ResponseEntity<?> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        try {
            passwordResetService.resetPassword(email, newPassword);
            return ResponseEntity.ok("Password reset successful.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    public ResponseEntity<Void> deleteMasterAdmin(@PathVariable Long id) {
        masterAdminService.deleteMasterAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        MasterAdmin admin = masterAdminService.login(email, password);
        return admin != null ? ResponseEntity.ok(admin)
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestParam Long id, @RequestParam String newPassword) {
        try {
            masterAdminService.updatePassword(id, newPassword);
            return ResponseEntity.ok("Password updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password update failed: " + e.getMessage());
        }
    }

    @Autowired
    private com.example.demo.Service.EmailService emailService;

    @PostMapping("/forgot-password/request")
    public ResponseEntity<?> requestForgotPassword(@RequestParam String email) {
        try {
            masterAdminService.sendResetOTP(email, emailService);
            return ResponseEntity.ok("OTP sent to email: " + email);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP request failed: " + e.getMessage());
        }
    }

    @PostMapping("/forgot-password/verify")
    public ResponseEntity<?> verifyOtpAndResetPassword(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {
        if (!masterAdminService.verifyOTP(email, otp)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");
        }
        try {
            masterAdminService.resetPassword(email, newPassword);
            return ResponseEntity.ok("Password reset successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password reset failed: " + e.getMessage());
        }
    }
}
