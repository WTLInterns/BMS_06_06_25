package com.example.demo.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.CustomBooking;
import com.example.demo.Model.Vendor;
import com.example.demo.Model.VendorDriver;
import com.example.demo.Model.VendorVehicle;
import com.example.demo.Repository.CustomBookingRepository;
import com.example.demo.Repository.VendorDriverRepository;
import com.example.demo.Repository.VendorRepository;
import com.example.demo.Repository.VendorVehicleRepository;

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

    @PostMapping("/custom-bookings/{bookingId}/assign-vendor/{vendorId}")
    public ResponseEntity<CustomBooking> assignVendorToBooking(
            @PathVariable Integer bookingId,
            @PathVariable Long vendorId) {

        CustomBooking booking = customBookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
        if (vendor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        booking.setVendor(vendor);
        CustomBooking updated = customBookingRepository.save(booking);

        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{bookingId}/assign-driver-vehicle")
    public ResponseEntity<CustomBooking> assignDriverAndVehicleToBooking(@PathVariable Integer bookingId,
            @RequestParam Integer driverId, @RequestParam Integer vehicleId) {
        CustomBooking booking = customBookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        VendorDriver driver = vendorDriverRepository.findById(driverId).orElse(null);
        if (driver == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        VendorVehicle vehicle = vendorVehicleRepository.findById(vehicleId).orElse(null);
        if (vehicle == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (booking.getVendor() == null || !booking.getVendor().getId().equals(driver.getVendor().getId())
                || !booking.getVendor().getId().equals(vehicle.getVendor().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        booking.setDriver(driver);
        booking.setVehicle(vehicle);
        CustomBooking updated = customBookingRepository.save(booking);

        return ResponseEntity.ok(updated);
    }

    // Get All Customer Booking Of Particular Vendor
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<CustomBooking>> getBookingsByVendor(@PathVariable Long vendorId) {
        List<CustomBooking> bookings = customBookingRepository.findByVendorId(vendorId);
        return ResponseEntity.ok(bookings);
    }

    // DTO for booking with vendor info
    class CustomBookingWithVendorDTO {
        private Integer bookingId;
        private Long assignedVendorId;
        private String assignedVendorName;

        public CustomBookingWithVendorDTO(CustomBooking booking) {
            this.bookingId = booking.getBookingId();
            this.assignedVendorId = booking.getVendor() != null ? booking.getVendor().getId() : null;
            this.assignedVendorName = booking.getVendor() != null
                    ? (booking.getVendor().getVendorCompanyName() != null ? booking.getVendor().getVendorCompanyName()
                            : booking.getVendor().getVendorFullName())
                    : null;
        }

        public Integer getBookingId() {
            return bookingId;
        }

        public Long getAssignedVendorId() {
            return assignedVendorId;
        }

        public String getAssignedVendorName() {
            return assignedVendorName;
        }
    }

    @GetMapping("/custom-bookings-with-vendor")
    public ResponseEntity<List<CustomBookingWithVendorDTO>> getAllBookingsWithVendor() {
        List<CustomBooking> bookings = customBookingRepository.findAll();
        List<CustomBookingWithVendorDTO> dtos = bookings.stream().map(CustomBookingWithVendorDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{bookingId}/details")
    public ResponseEntity<BookingDetailsDTO> getBookingDetails(@PathVariable Integer bookingId) {
        CustomBooking booking = customBookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(new BookingDetailsDTO(booking));
    }

    // DTO for booking details
    public static class BookingDetailsDTO {
        private Integer bookingId;
        private Long vendorId;
        private String vendorName;
        private Integer driverId;
        private String driverName;
        private Integer vehicleId;
        private String vehicleName;
        public BookingDetailsDTO(CustomBooking booking) {
            this.bookingId = booking.getBookingId();
            this.vendorId = booking.getVendor() != null ? booking.getVendor().getId() : null;
            this.vendorName = booking.getVendor() != null ? booking.getVendor().getVendorCompanyName() : null;
            this.driverId = booking.getDriver() != null ? booking.getDriver().getDriverId() : null;
            this.driverName = booking.getDriver() != null ? booking.getDriver().getDriverName() : null;
            this.vehicleId = booking.getVehicle() != null ? booking.getVehicle().getVehicleId() : null;
            this.vehicleName = booking.getVehicle() != null ? booking.getVehicle().getCarName() : null;
        }
        // getters
        public Integer getBookingId() { return bookingId; }
        public Long getVendorId() { return vendorId; }
        public String getVendorName() { return vendorName; }
        public Integer getDriverId() { return driverId; }
        public String getDriverName() { return driverName; }
        public Integer getVehicleId() { return vehicleId; }
        public String getVehicleName() { return vehicleName; }
    }
}
