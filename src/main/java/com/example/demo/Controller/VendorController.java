package com.example.demo.Controller;

import com.example.demo.Model.Vendor;
import com.example.demo.Service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.Service.VendorPasswordResetService;


@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    @Autowired
    private VendorPasswordResetService passwordResetService;


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

