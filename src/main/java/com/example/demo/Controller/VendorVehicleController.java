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

    @PutMapping("/assigncab/{cabId}/{bookingId}")
    public ResponseEntity<CustomBooking> assignCabToBooking(
            @PathVariable Long vendorId,
            @PathVariable int cabId,
            @PathVariable int bookingId) {
        try {
            CustomBooking updatedBooking = vendorVehicleService.assignVendorCabToBooking(vendorId, bookingId, cabId);
            if (updatedBooking == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedBooking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
}
