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

    @Autowired
    private EmailService emailService;

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
 
    double bookingAmount = Double.parseDouble(customBooking.getBookingAmount());
    double gst = bookingAmount * 0.05;
    double serviceCharge = bookingAmount * 0.10;
    double totalAmount = bookingAmount + gst + serviceCharge;
 
    customBooking.setGst(String.format("%.2f", gst));
    customBooking.setServiceCharge(String.format("%.2f", serviceCharge));
    customBooking.setTotalAmount(String.format("%.2f", totalAmount));
    customBooking.setBookingAmount(String.format("%.2f", bookingAmount));
    customBooking.setVendor(vendor);
 
    CustomBooking savedBooking = customBookingRepository.save(customBooking);
 
    // Prepare styled invoice email
    String subject = "Invoice: Your Booking Details";
    String message = String.format("""
        <div style="font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: auto;">
            <h2 style="color: #2E86C1; text-align: center;">Booking Invoice</h2>
            <table style="width: 100%%; border-collapse: collapse; margin-top: 20px;">
                <tr><td><strong>Booking ID:</strong></td><td>%d</td></tr>
                <tr><td><strong>Date:</strong></td><td>%s</td></tr>
                <tr><td><strong>Time:</strong></td><td>%s</td></tr>
                <tr><td><strong>Customer Name:</strong></td><td>%s</td></tr>
                <tr><td><strong>Mobile:</strong></td><td>%s</td></tr>
                <tr><td><strong>Email:</strong></td><td>%s</td></tr>
                <tr><td><strong>Pickup Location:</strong></td><td>%s</td></tr>
                <tr><td><strong>Drop Location:</strong></td><td>%s</td></tr>
                <tr><td><strong>Pickup Date & Time:</strong></td><td>%s at %s</td></tr>
                <tr><td><strong>Return Date:</strong></td><td>%s</td></tr>
                <tr><td><strong>Car Type:</strong></td><td>%s</td></tr>
            </table>
 
            <h3 style="color: #117A65; margin-top: 30px;">Payment Summary</h3>
            <table style="width: 100%%; border: 1px solid #ccc; border-collapse: collapse;">
                <tr style="background-color: #f2f2f2;">
                    <th style="padding: 8px; border: 1px solid #ccc; text-align: left;">Description</th>
                    <th style="padding: 8px; border: 1px solid #ccc; text-align: right;">Amount (INR)</th>
                </tr>
                <tr>
                    <td style="padding: 8px; border: 1px solid #ccc;">Base Fare</td>
                    <td style="padding: 8px; border: 1px solid #ccc; text-align: right;">%s</td>
                </tr>
                <tr>
                    <td style="padding: 8px; border: 1px solid #ccc;">GST (5%%)</td>
                    <td style="padding: 8px; border: 1px solid #ccc; text-align: right;">%s</td>
                </tr>
                <tr>
                    <td style="padding: 8px; border: 1px solid #ccc;">Service Charge (10%%)</td>
                    <td style="padding: 8px; border: 1px solid #ccc; text-align: right;">%s</td>
                </tr>
                <tr style="font-weight: bold; background-color: #f9f9f9;">
                    <td style="padding: 8px; border: 1px solid #ccc;">Total Amount</td>
                    <td style="padding: 8px; border: 1px solid #ccc; text-align: right;">%s</td>
                </tr>
            </table>
 
            <p style="margin-top: 30px; text-align: center;">Thank you for booking with us!</p>
        </div>
        """,
        savedBooking.getBookingId(),
        savedBooking.getBookingDate(),
        savedBooking.getBookingTime(),
        savedBooking.getCustomerName(),
        savedBooking.getCustomerMobileNo(),
        savedBooking.getCustomerEmail(),
        savedBooking.getPickupLocation(),
        savedBooking.getDropLocation(),
        savedBooking.getPickUpDate(),
        savedBooking.getPickUpTime(),
        savedBooking.getReturnDate(),
        savedBooking.getCarType(),
        savedBooking.getBookingAmount(),
        savedBooking.getGst(),
        savedBooking.getServiceCharge(),
        savedBooking.getTotalAmount()
    );
 
    emailService.sendHtmlEmail(message, subject, savedBooking.getCustomerEmail());
 
    return savedBooking;
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
        existingBooking.setGst(bookingDetails.getGst());
        existingBooking.setServiceCharge(bookingDetails.getServiceCharge());
        existingBooking.setTotalAmount(bookingDetails.getTotalAmount());
        

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
