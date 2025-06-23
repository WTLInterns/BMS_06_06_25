package com.example.demo.Service;

import com.example.demo.Model.CustomBooking;
import com.example.demo.Repository.CustomBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.example.demo.Model.Vendor;

@Service
public class CustomBookingService {

    @Autowired
    private CustomBookingRepository customBookingRepository;

    public List<CustomBooking> getAllBookings() {
        return customBookingRepository.findAll();
    }

    public Optional<CustomBooking> getBookingById(Long vendorId, Integer bookingId) {
        return customBookingRepository.findById(bookingId)
                .filter(booking -> booking.getVendor() != null && vendorId.equals(booking.getVendor().getId()));
    }


    public List<CustomBooking> getBookingsByVendor(Long vendorId) {
        return customBookingRepository.findByVendorId(vendorId);
    }

    //Changes About Status

    public List<CustomBooking> getBookingsByVendorAndStatus(Long vendorId, String status) {
        return getBookingsByVendor(vendorId).stream()
            .filter(b -> status.equalsIgnoreCase(b.getBookingStatus()))
            .collect(Collectors.toList());
    }

    public Map<String, Long> getVendorBookingStats(Long vendorId) {
        Map<String, Long> stats = new HashMap<>();
        List<CustomBooking> bookings = getBookingsByVendor(vendorId);
        
        stats.put("pending", bookings.stream()
            .filter(b -> "pending".equalsIgnoreCase(b.getBookingStatus()))
            .count());
        
        stats.put("ongoing", bookings.stream()
            .filter(b -> "ongoing".equalsIgnoreCase(b.getBookingStatus()))
            .count());
        
        stats.put("completed", bookings.stream()
            .filter(b -> "completed".equalsIgnoreCase(b.getBookingStatus()))
            .count());
        
        stats.put("cancelled", bookings.stream()
            .filter(b -> "cancelled".equalsIgnoreCase(b.getBookingStatus()))
            .count());
        
        return stats;
    }

    public CustomBooking findById(Integer bookingId) {
        return customBookingRepository.findById(bookingId)
            .orElse(null);
    }



    

    // Calculate total revenue for vendor
    public double calculateTotalRevenue(Long vendorId) {
        List<CustomBooking> bookings = getBookingsByVendor(vendorId);
        return bookings.stream()
            .filter(b -> "completed".equalsIgnoreCase(b.getBookingStatus()))
            .mapToDouble(b -> Double.parseDouble(b.getBookingAmount()))
            .sum();
    }

    public CustomBooking updateBookingStatus(Integer bookingId, String status) {
        CustomBooking booking = findById(bookingId);
        if (booking != null) {
            booking.setBookingStatus(status);
            return customBookingRepository.save(booking);
        }
        return null;
    }

    //Till Here

    public CustomBooking createBooking(Long vendorId, CustomBooking customBooking) {
        Vendor vendor = new Vendor();
        vendor.setId(vendorId);
        customBooking.setVendor(vendor);
        customBooking.setBookingStatus("0");
        return customBookingRepository.save(customBooking);
    }

    public CustomBooking updateBooking(Long vendorId, Integer bookingId, CustomBooking bookingDetails) {
        CustomBooking existingBooking = customBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (existingBooking.getVendor() == null || !vendorId.equals(existingBooking.getVendor().getId())) {
            throw new RuntimeException("Booking does not belong to this vendor.");
        }

        existingBooking.setBookingDate(bookingDetails.getBookingDate());
        existingBooking.setBookingTime(bookingDetails.getBookingTime());
        existingBooking.setBookingStatus(bookingDetails.getBookingStatus());
        existingBooking.setBookingType(bookingDetails.getBookingType());
        existingBooking.setBookingDetails(bookingDetails.getBookingDetails());
        existingBooking.setBookingAmount(bookingDetails.getBookingAmount());
        existingBooking.setCustomerName(bookingDetails.getCustomerName());
        existingBooking.setCustomerMobileNo(bookingDetails.getCustomerMobileNo());
        existingBooking.setPickupLocation(bookingDetails.getPickupLocation());
        existingBooking.setDropLocation(bookingDetails.getDropLocation());
        existingBooking.setPickUpDate(bookingDetails.getPickUpDate());
        existingBooking.setPickUpTime(bookingDetails.getPickUpTime());
        existingBooking.setCarType(bookingDetails.getCarType());
        existingBooking.setReturnDate(bookingDetails.getReturnDate());
        existingBooking.setTripType(bookingDetails.getTripType());
        existingBooking.setVendor(bookingDetails.getVendor());
        existingBooking.setMasterAdmin(bookingDetails.getMasterAdmin());
        existingBooking.setCollection(bookingDetails.getCollection());
        existingBooking.setFullName(bookingDetails.getFullName());
        existingBooking.setCustomerEmail(bookingDetails.getCustomerEmail());
        existingBooking.setCommunicationAddress(bookingDetails.getCommunicationAddress());
        existingBooking.setAlternativeMobileNo(bookingDetails.getAlternativeMobileNo());

        return customBookingRepository.save(existingBooking);
    }

    public void deleteBooking(Long vendorId, Integer bookingId) {
        CustomBooking booking = customBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (booking.getVendor() == null || !vendorId.equals(booking.getVendor().getId())) {
            throw new RuntimeException("Booking does not belong to this vendor.");
        }
        customBookingRepository.delete(booking);
    }
}
