package com.example.demo.Controller;

import com.example.demo.Model.CustomBooking;
import com.example.demo.Model.Vendor;
import com.example.demo.Model.VendorDriver;
import com.example.demo.Model.VendorVehicle;
import com.example.demo.Repository.CustomBookingRepository;
import com.example.demo.Repository.VendorRepository;
import com.example.demo.Repository.VendorDriverRepository;
import com.example.demo.Repository.VendorVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingAssignmentController {

    @Autowired
    private CustomBookingRepository customBookingRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private VendorDriverRepository vendorDriverRepository;

    @Autowired
    private VendorVehicleRepository vendorVehicleRepository;

    @PostMapping("/{bookingId}/assign-vendor/{vendorId}")
    public ResponseEntity<?> assignVendorToBooking(@PathVariable Integer bookingId, @PathVariable Long vendorId) {
        CustomBooking booking = customBookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
        }

        Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
        if (vendor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");
        }

        booking.setVendor(vendor);
        customBookingRepository.save(booking);

        return ResponseEntity.ok("Vendor assigned to booking successfully.");
    }

    @PostMapping("/{bookingId}/assign-driver-vehicle")
    public ResponseEntity<?> assignDriverAndVehicleToBooking(@PathVariable Integer bookingId, @RequestParam Integer driverId, @RequestParam Integer vehicleId) {
        CustomBooking booking = customBookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
        }

        VendorDriver driver = vendorDriverRepository.findById(driverId).orElse(null);
        if (driver == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not found");
        }

        VendorVehicle vehicle = vendorVehicleRepository.findById(vehicleId).orElse(null);
        if (vehicle == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle not found");
        }

        if (booking.getVendor() == null || !booking.getVendor().getId().equals(driver.getVendor().getId()) || !booking.getVendor().getId().equals(vehicle.getVendor().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Driver or vehicle does not belong to the assigned vendor.");
        }

        booking.setDriver(driver);
        booking.setVehicle(vehicle);
        customBookingRepository.save(booking);

        return ResponseEntity.ok("Driver and vehicle assigned to booking successfully.");
    }
}
