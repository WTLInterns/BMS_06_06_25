package com.example.demo.Controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Model.CustomBooking;
import com.example.demo.Model.Vendor;
import com.example.demo.Model.VendorDriver;
import com.example.demo.Repository.VendorRepository;
import com.example.demo.Service.VendorDriverService;

@RestController
@RequestMapping("/api/vendors/{vendorId}/drivers")
public class VendorDriverController {

    @Autowired
    private VendorDriverService vendorDriverService;

    @Autowired
    private VendorRepository vendorRepository;

    @GetMapping
    public ResponseEntity<List<VendorDriver>> getDriversByVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok(vendorDriverService.getDriversByVendor(vendorId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createDriver(@PathVariable Long vendorId, @ModelAttribute VendorDriver vendorDriver,
            @RequestParam(value = "driverImageFile", required = false) MultipartFile driverImageFile,
            @RequestParam(value = "driverSelfieFile", required = false) MultipartFile driverSelfieFile,
            @RequestParam(value = "dLnoImageFile", required = false) MultipartFile dLnoImageFile,
            @RequestParam(value = "pvcImageFile", required = false) MultipartFile pvcImageFile,
            @RequestParam(value = "driverDoc1ImageFile", required = false) MultipartFile driverDoc1ImageFile,
            @RequestParam(value = "driverDoc2ImageFile", required = false) MultipartFile driverDoc2ImageFile,
            @RequestParam(value = "driverDoc3ImageFile", required = false) MultipartFile driverDoc3ImageFile)
            throws IOException {
        Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
        if (vendor == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid vendor ID");
        }
        vendorDriver.setVendor(vendor);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vendorDriverService.createDriver(vendorDriver, driverImageFile, driverSelfieFile, dLnoImageFile,
                        pvcImageFile, driverDoc1ImageFile, driverDoc2ImageFile, driverDoc3ImageFile));
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<VendorDriver> getDriverById(@PathVariable Integer driverId) {
        return vendorDriverService.getDriverById(driverId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{driverId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VendorDriver> updateDriver(@PathVariable Integer driverId,
            @ModelAttribute VendorDriver driverDetails,
            @RequestParam(value = "driverImageFile", required = false) MultipartFile driverImageFile,
            @RequestParam(value = "driverSelfieFile", required = false) MultipartFile driverSelfieFile,
            @RequestParam(value = "dLnoImageFile", required = false) MultipartFile dLnoImageFile,
            @RequestParam(value = "pvcImageFile", required = false) MultipartFile pvcImageFile,
            @RequestParam(value = "driverDoc1ImageFile", required = false) MultipartFile driverDoc1ImageFile,
            @RequestParam(value = "driverDoc2ImageFile", required = false) MultipartFile driverDoc2ImageFile,
            @RequestParam(value = "driverDoc3ImageFile", required = false) MultipartFile driverDoc3ImageFile)
            throws IOException {
        return ResponseEntity
                .ok(vendorDriverService.updateDriver(driverId, driverDetails, driverImageFile, driverSelfieFile,
                        dLnoImageFile, pvcImageFile, driverDoc1ImageFile, driverDoc2ImageFile, driverDoc3ImageFile));
    }

    @DeleteMapping("/{driverId}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Integer driverId) {
        vendorDriverService.deleteDriver(driverId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<VendorDriver> changeStatus(@PathVariable int id,
            @RequestBody Map<String, String> requestBody) {

        String status = requestBody.get("status");

        try {
            VendorDriver updatedOrder = vendorDriverService.updateStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/assigndriver/{driverId}/{bookingId}")
    public ResponseEntity<CustomBooking> assignDriverToBooking(
            @PathVariable Long vendorId,
            @PathVariable int driverId,
            @PathVariable int bookingId) {
        try {
            CustomBooking updatedBooking = vendorDriverService.assignVendorDriverToBooking(vendorId, bookingId, driverId);
            if (updatedBooking == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedBooking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }


}
