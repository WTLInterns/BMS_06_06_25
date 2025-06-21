package com.example.demo.Controller;

import com.example.demo.Model.CustomBooking;
import com.example.demo.Service.CustomBookingService;
import com.example.demo.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/custom-bookings")
public class CustomBookingController {

    @Autowired
    private CustomBookingService customBookingService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<CustomBooking>> getBookingsByVendor(
            @PathVariable Long vendorId,
            @RequestParam(required = false) String status) {
        if (status != null) {
            List<CustomBooking> bookings = customBookingService.getBookingsByVendorAndStatus(vendorId, status);
            return ResponseEntity.ok(bookings);
        }
        List<CustomBooking> bookings = customBookingService.getBookingsByVendor(vendorId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/vendor/{vendorId}/stats")
    public ResponseEntity<Map<String, Long>> getVendorBookingStats(@PathVariable Long vendorId) {
        Map<String, Long> stats = customBookingService.getVendorBookingStats(vendorId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/vendor/{vendorId}/revenue")
    public ResponseEntity<Double> getVendorTotalRevenue(@PathVariable Long vendorId) {
        double revenue = customBookingService.calculateTotalRevenue(vendorId);
        return ResponseEntity.ok(revenue);
    }

    @PutMapping("/{bookingId}/status")
    public ResponseEntity<CustomBooking> updateBookingStatus(
            @PathVariable Integer bookingId,
            @RequestParam String status) {
        CustomBooking updated = customBookingService.updateBookingStatus(bookingId, status);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<CustomBooking>> getAllBookings() {
        List<CustomBooking> bookings = customBookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    // @PostMapping("/bookings")
    // public ResponseEntity<CustomBooking> postBooking(@RequestBody CustomBooking
    // customBooking) {
    // return ResponseEntity.status(HttpStatus.CREATED).build();
    // }

    @PostMapping("/vendor/{vendorId}/bookings")
    public ResponseEntity<CustomBooking> createBooking(@PathVariable Long vendorId,
            @RequestBody CustomBooking customBooking) {
        CustomBooking saved = customBookingService.createBooking(vendorId, customBooking);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/vendor/{vendorId}/{bookingId}")
    public ResponseEntity<CustomBooking> getBookingById(@PathVariable Long vendorId, @PathVariable Integer bookingId) {
        return customBookingService.getBookingById(vendorId, bookingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/vendor/{vendorId}/{bookingId}")
    public ResponseEntity<CustomBooking> updateBooking(@PathVariable Long vendorId, @PathVariable Integer bookingId,
            @RequestBody CustomBooking bookingDetails) {
        CustomBooking updated = customBookingService.updateBooking(vendorId, bookingId, bookingDetails);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/vendor/{vendorId}/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long vendorId, @PathVariable Integer bookingId) {
        customBookingService.deleteBooking(vendorId, bookingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/vendors/{vendorId}/send-details-customeremail/{customerEmail}/{bookingId}")
    public ResponseEntity<String> sendBookingDetailsToCustomer(
            @PathVariable Long vendorId,
            @PathVariable String customerEmail,
            @PathVariable Integer bookingId) {

        Optional<CustomBooking> bookingOpt = customBookingService.getBookingById(vendorId, bookingId);
        if (!bookingOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        CustomBooking booking = bookingOpt.get();
        String subject = "Your Booking Details";
        String message = String.format("""
                <h2>Booking Details</h2>
                <p>Booking ID: %d</p>
                <p>Booking Date: %s</p>
                <p>Booking Time: %s</p>
                <p>Booking Amount: %s</p>
                <p>Customer Name: %s</p>
                <p>Customer Mobile: %s</p>
                <p>Pickup Location: %s</p>
                <p>Drop Location: %s</p>
                <p>Pickup Date: %s</p>
                <p>Pickup Time: %s</p>
                <p>Return Date: %s</p>
                <h3>Vehicle Details</h3>
                <p>Driver Name: %s</p>
                <p>Contact No: %s</p>
                <p>Car Name: %s</p>
                <p>Vehicle No: %s</p>
                """,
                booking.getBookingId(),
                booking.getBookingDate(),
                booking.getBookingTime(),
                booking.getBookingAmount(),
                booking.getCustomerName(),
                booking.getCustomerMobileNo(),
                booking.getPickupLocation(),
                booking.getDropLocation(),
                booking.getPickUpDate(),
                booking.getPickUpTime(),
                booking.getReturnDate(),
                booking.getDriver() != null ? booking.getDriver().getDriverName() : "",
                booking.getDriver() != null ? booking.getDriver().getContactNo() : "",
                booking.getVehicle() != null ? booking.getVehicle().getCarName() : "",
                booking.getVehicle() != null ? booking.getVehicle().getVehicleNo() : "");

        boolean sent = emailService.sendHtmlEmail(message, subject, customerEmail);
        if (sent) {
            return ResponseEntity.ok("Email sent successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email");
        }
    }

    @PostMapping("/vendors/{vendorId}/send-details-driver/{driverEmail}/{bookingId}")
    public ResponseEntity<String> sendBookingDetailsToDriver(
            @PathVariable Long vendorId,
            @PathVariable String driverEmail,
            @PathVariable Integer bookingId) {

        Optional<CustomBooking> bookingOpt = customBookingService.getBookingById(vendorId, bookingId);
        if (!bookingOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        CustomBooking booking = bookingOpt.get();
        String subject = "New Booking Assignment";
        String message = String.format("""
                <h2>Customer Booking Details</h2>
                <p>Booking ID: %d</p>
                <p>Booking Date: %s</p>
                <p>Booking Time: %s</p>
                <p>Booking Amount: %s</p>
                <p>Customer Name: %s</p>
                <p>Customer Mobile: %s</p>
                <p>Pickup Location: %s</p>
                <p>Drop Location: %s</p>
                <p>Pickup Date: %s</p>
                <p>Pickup Time: %s</p>
                <p>Return Date: %s</p>
                """,
                booking.getBookingId(),
                booking.getBookingDate(),
                booking.getBookingTime(),
                booking.getBookingAmount(),
                booking.getCustomerName(),
                booking.getCustomerMobileNo(),
                booking.getPickupLocation(),
                booking.getDropLocation(),
                booking.getPickUpDate(),
                booking.getPickUpTime(),
                booking.getReturnDate());

        boolean sent = emailService.sendHtmlEmail(message, subject, driverEmail);
        if (sent) {
            return ResponseEntity.ok("Email sent successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email");
        }
    }
}
