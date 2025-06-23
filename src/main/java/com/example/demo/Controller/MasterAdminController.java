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

    @DeleteMapping("/vendors/{id}")
    public ResponseEntity<?> deleteVendor(@PathVariable Long id) {
        try {
            masterAdminService.deleteVendorById(id);
            return ResponseEntity.ok().body("Vendor deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");
        }
    }

    @PostMapping("/vendors/{masteradminid:\\d+}")
    public ResponseEntity<?> createVendor(
            @PathVariable("masteradminid") Long masterAdminId,
            @ModelAttribute VendorForm vendorForm,
            @RequestParam(value = "vendorImage", required = false) MultipartFile vendorImage,
            @RequestParam(value = "gstNoImage", required = false) MultipartFile gstNoImage,
            @RequestParam(value = "govtApprovalCertificate", required = false) MultipartFile govtApprovalCertificate,
            @RequestParam(value = "vendorDocs", required = false) MultipartFile vendorDocs,
            @RequestParam(value = "aadharPhoto", required = false) MultipartFile aadharPhoto,
            @RequestParam(value = "panPhoto", required = false) MultipartFile panPhoto,
            @RequestParam(value = "vendorCompanyLogo", required = false) MultipartFile vendorCompanyLogo) {
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
            vendor.setVendorCompanyLogo(vendorForm.getVendorCompanyLogo());
            vendor.setStatus(vendorForm.getStatus());
            // Set password: if not provided, use contactNo
            String password = vendorForm.getPassword();
            if (password == null || password.trim().isEmpty()) {
                password = vendorForm.getContactNo();
            }
            vendor.setPassword(password);

            MasterAdmin masterAdmin = masterAdminRepository.findById(masterAdminId).orElse(null);
            if (masterAdmin == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid master admin ID");
            }
            vendor.setMasterAdmin(masterAdmin);
            Vendor saved = vendorService.createVendor(vendor, vendorImage, gstNoImage, govtApprovalCertificate,
                    vendorDocs, aadharPhoto, panPhoto, vendorCompanyLogo);

            // Compose response as { vendor: ..., masterAdmin: ... }
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("vendor", saved);
            response.put("masterAdmin", masterAdmin);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // Public endpoint for vendor onboarding completion
    @PutMapping("/vendors/complete-onboarding")
    public ResponseEntity<?> completeOnboarding(
            @ModelAttribute VendorForm vendorForm,
            @RequestParam(value = "vendorImage", required = false) MultipartFile vendorImage,
            @RequestParam(value = "gstNoImage", required = false) MultipartFile gstNoImage,
            @RequestParam(value = "govtApprovalCertificate", required = false) MultipartFile govtApprovalCertificate,
            @RequestParam(value = "vendorDocs", required = false) MultipartFile vendorDocs,
            @RequestParam(value = "aadharPhoto", required = false) MultipartFile aadharPhoto,
            @RequestParam(value = "panPhoto", required = false) MultipartFile panPhoto,
            @RequestParam(value = "vendorCompanyLogo", required = false) MultipartFile vendorCompanyLogo) {
        try {
            // 1. Find the existing vendor by email from the form
            Vendor vendor = vendorService.findByEmail(vendorForm.getVendorEmail());

            // 2. Check if the vendor exists and is in the "Invited" state
            if (vendor == null || !"Invited".equals(vendor.getStatus())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No pending invitation found for this email.");
            }

            // 3. Update the vendor's details from the form
            vendor.setVendorFullName(vendorForm.getVendorFullName());
            vendor.setVendorCompanyName(vendorForm.getVendorCompanyName());
            vendor.setContactNo(vendorForm.getContactNo());
            vendor.setAlternateMobileNo(vendorForm.getAlternateMobileNo());
            vendor.setCity(vendorForm.getCity());
            vendor.setBankName(vendorForm.getBankName());
            vendor.setBankAccountNo(vendorForm.getBankAccountNo());
            vendor.setIfscCode(vendorForm.getIfscCode());
            vendor.setAadharNo(vendorForm.getAadharNo());
            vendor.setPanNo(vendorForm.getPanNo());
            vendor.setGstNo(vendorForm.getGstNo());
            vendor.setUdyogAadharNo(vendorForm.getUdyogAadharNo());
            vendor.setVendorOtherDetails(vendorForm.getVendorOtherDetails());
            // 4. Change status from "Invited" to "Inactive" (or "Active")
            vendor.setStatus("Inactive"); // The admin can activate them later

            // 5. Call the update service method, passing the files
            vendorService.updateVendor(vendor.getId(), vendor, vendorImage, gstNoImage, govtApprovalCertificate,
                    vendorDocs, aadharPhoto, panPhoto, vendorCompanyLogo);

            return ResponseEntity.ok("Onboarding completed successfully. We will contact you shortly.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during onboarding: " + e.getMessage());
        }
    }

    @GetMapping("/{masterAdminId}/vendors")
    public ResponseEntity<List<Vendor>> getVendorsByMasterAdmin(@PathVariable("masterAdminId") Long masterAdminId) {
        return masterAdminService.getMasterAdminById(masterAdminId)
                .map(masterAdmin -> ResponseEntity.ok(masterAdmin.getVendors()))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(java.util.Collections.emptyList()));
    }

    // PUT mapping to update vendor for a master admin
    @PutMapping("/vendors/{masteradminid:\\d+}/{vendorid:\\d+}")
    public ResponseEntity<?> updateVendor(
            @PathVariable("masteradminid") Long masterAdminId,
            @PathVariable("vendorid") Long vendorId,
            @ModelAttribute VendorForm vendorForm,
            @RequestParam(value = "vendorImage", required = false) MultipartFile vendorImage,
            @RequestParam(value = "gstNoImage", required = false) MultipartFile gstNoImage,
            @RequestParam(value = "govtApprovalCertificate", required = false) MultipartFile govtApprovalCertificate,
            @RequestParam(value = "vendorDocs", required = false) MultipartFile vendorDocs,
            @RequestParam(value = "aadharPhoto", required = false) MultipartFile aadharPhoto,
            @RequestParam(value = "panPhoto", required = false) MultipartFile panPhoto,
            @RequestParam(value = "vendorCompanyLogo", required = false) MultipartFile vendorCompanyLogo) {
        try {
            MasterAdmin masterAdmin = masterAdminRepository.findById(masterAdminId).orElse(null);
            if (masterAdmin == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid master admin ID");
            }
            Vendor vendor = vendorService.getVendorById(vendorId).orElse(null);
            if (vendor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");
            }
            // Update fields (add more as needed)
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
            vendor.setVendorCompanyLogo(vendorForm.getVendorCompanyLogo());
            if (vendorForm.getPassword() != null && !vendorForm.getPassword().isEmpty()) {
                vendor.setPassword(vendorForm.getPassword());
            }
            // else, keep the existing password
            vendor.setMasterAdmin(masterAdmin);
            Vendor updated = vendorService.updateVendor(vendorId, vendor, vendorImage, gstNoImage,
                    govtApprovalCertificate, vendorDocs, aadharPhoto, panPhoto, vendorCompanyLogo);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // POST mapping to send invitation email for vendor onboarding
    @PostMapping("/vendors/onboard")
    public ResponseEntity<?> onboardVendorFromForm(@RequestBody VendorForm vendorForm) {
        try {
            Long masterAdminId = vendorForm.getMasterAdminId();
            MasterAdmin masterAdmin = masterAdminRepository.findById(masterAdminId).orElse(null);
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
            Vendor saved = vendorService.createVendor(vendor, null, null, null, null, null, null, null);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/vendors/send-manually")
    public ResponseEntity<?> sendVendorInvite(@RequestParam String email, @RequestParam Long masterAdminId) {
        try {
            // 1. Find the master admin
            MasterAdmin masterAdmin = masterAdminRepository.findById(masterAdminId).orElse(null);
            if (masterAdmin == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid master admin ID");
            }

            // 2. Check if vendor with this email already exists
            Vendor existingVendor = vendorService.findByEmail(email);
            if (existingVendor != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Vendor with this email already exists");
            }

            // 3. Create a minimal vendor (with default password and role)
            Vendor vendor = new Vendor();
            vendor.setVendorEmail(email);
            vendor.setMasterAdmin(masterAdmin);
            vendor.setStatus("Invited");
            vendor.setPassword(java.util.UUID.randomUUID().toString().substring(0, 8)); // random password
            vendor.setRole("Vendor");
            Vendor savedVendor = vendorService.createVendor(vendor, null, null, null, null, null, null, null);

            // 4. Compose invitation message
            String message = "<html><body>"

                    + "<h2>Welcome to WTL!</h2>"
                    + "<p>If you want to onboard, please fill the following form: <a href='http://localhost:3000/vendor-onboard-form?email="
                    + email + "&masterAdminId=" + masterAdminId + "'>Onboarding Form</a></p>"
                    + "<p>If you need any help, please contact wtlcontact@gmail.com.</p>"
                    + "<p>When you fill the form, your details will be automatically added to our database with your respective master admin. After successful submission, you will receive a confirmation email. Shortly, we will notify you about next steps.</p>"
                    + "</body></html>";
            String subject = "WTL Vendor Onboarding Invitation";
            boolean sent = vendorEmailService.sendCustomHtmlEmail(message, subject, email);

            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("vendor", savedVendor);
            response.put("emailSent", sent);
            if (sent) {
                response.put("message", "Invitation sent successfully to " + email);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Vendor created, but failed to send invitation email");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
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
    public ResponseEntity<?> createMasterAdmin(@ModelAttribute com.example.demo.Model.MasterAdminForm form,
            @RequestParam(value = "profileImg", required = false) MultipartFile profileImg) {
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

    // Send vendor login details via email
    @PostMapping("/vendors/send-login-details")
    public ResponseEntity<?> sendVendorLoginDetails(@RequestParam String email) {
        Vendor vendor = vendorService.findByEmail(email);
        if (vendor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");
        }
        boolean sent = vendorEmailService.sendVendorCredentials(vendor);
        return sent ? ResponseEntity.ok("Login details sent to vendor.")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email.");
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
    public ResponseEntity<?> verifyOtpAndResetPassword(@RequestParam String email, @RequestParam String otp,
            @RequestParam String newPassword) {
        boolean valid = passwordResetService.verifyOTP(email, otp);
        if (valid) {
            try {
                passwordResetService.resetPassword(email, newPassword);
                return ResponseEntity.ok("Password reset successful.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password reset failed: " + e.getMessage());
            }
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
    public ResponseEntity<?> verifyOtpAndResetPassword(@RequestParam String email, @RequestParam String otp,
            @RequestParam String newPassword) {
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
