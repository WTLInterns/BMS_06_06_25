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
        List<CustomBooking> bookings = customBookingService.getBookingsByMasterAdmin(masterAdminId);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping
    public ResponseEntity<CustomBooking> createBooking(@PathVariable Long masterAdminId,
            @RequestBody CustomBooking customBooking) {
        MasterAdmin masterAdmin = masterAdminRepository.findById(masterAdminId).orElse(null);
        if (masterAdmin == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        customBooking.setMasterAdmin(masterAdmin);
        CustomBooking saved = customBookingService.createBooking(customBooking);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<CustomBooking> getBookingById(@PathVariable Integer bookingId) {
        return customBookingService.getBookingById(bookingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<CustomBooking> updateBooking(@PathVariable Integer bookingId,
            @RequestBody CustomBooking bookingDetails) {
        CustomBooking updated = customBookingService.updateBooking(bookingId, bookingDetails);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Integer bookingId) {
        customBookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}
