package com.example.demo.Controller;

import com.example.demo.Model.CustomBooking;
import com.example.demo.Model.MasterAdmin;
import com.example.demo.Repository.MasterAdminRepository;
import com.example.demo.Service.CustomBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/masteradmins/{masterAdminId}/custom-bookings")
public class CustomBookingController {

    @Autowired
    private CustomBookingService customBookingService;

    @Autowired
    private MasterAdminRepository masterAdminRepository;

    @GetMapping
    public ResponseEntity<List<CustomBooking>> getBookingsByMasterAdmin(@PathVariable Long masterAdminId) {
        return ResponseEntity.ok(customBookingService.getBookingsByMasterAdmin(masterAdminId));
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@PathVariable Long masterAdminId, @RequestBody CustomBooking customBooking) {
        MasterAdmin masterAdmin = masterAdminRepository.findById(masterAdminId).orElse(null);
        if (masterAdmin == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid master admin ID");
        }
        customBooking.setMasterAdmin(masterAdmin);
        return ResponseEntity.status(HttpStatus.CREATED).body(customBookingService.createBooking(customBooking));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<CustomBooking> getBookingById(@PathVariable Integer bookingId) {
        return customBookingService.getBookingById(bookingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<CustomBooking> updateBooking(@PathVariable Integer bookingId, @RequestBody CustomBooking bookingDetails) {
        return ResponseEntity.ok(customBookingService.updateBooking(bookingId, bookingDetails));
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Integer bookingId) {
        customBookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}
