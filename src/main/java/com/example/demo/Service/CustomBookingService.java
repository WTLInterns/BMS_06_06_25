package com.example.demo.Service;

import com.example.demo.Model.CustomBooking;
import com.example.demo.Repository.CustomBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomBookingService {

    @Autowired
    private CustomBookingRepository customBookingRepository;

    public List<CustomBooking> getAllBookings() {
        return customBookingRepository.findAll();
    }

    public Optional<CustomBooking> getBookingById(Integer bookingId) {
        return customBookingRepository.findById(bookingId);
    }

    public List<CustomBooking> getBookingsByMasterAdmin(Long masterAdminId) {
        return customBookingRepository.findByMasterAdminId(masterAdminId);
    }

    public List<CustomBooking> getBookingsByVendor(Long vendorId) {
        return customBookingRepository.findByVendorId(vendorId);
    }

    public CustomBooking createBooking(CustomBooking customBooking) {
        return customBookingRepository.save(customBooking);
    }

    public CustomBooking updateBooking(Integer bookingId, CustomBooking bookingDetails) {
        CustomBooking existingBooking = customBookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found"));

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

        return customBookingRepository.save(existingBooking);
    }

    public void deleteBooking(Integer bookingId) {
        customBookingRepository.deleteById(bookingId);
    }
}
