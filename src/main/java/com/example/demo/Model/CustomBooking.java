package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;

@Entity
public class CustomBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int bookingId;
    private String bookingDate;
    private String bookingTime;
    private String bookingStatus;
    private String bookingType;
    private String bookingDetails;
    private String bookingAmount;
    private String customerName;
    private String customerMobileNo;
    private String pickupLocation;
    private String dropLocation;
    private String pickUpDate;
    private String pickUpTime;
    private String carType;
    private String returnDate;
    private String tripType;
    private int collection;
    private String fullName;
    private String customerEmail;
    @Column(nullable = true)
    private String communicationAddress;
    
    @Column(nullable = true)
    private String alternativeMobileNo;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    @JsonBackReference
    private Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "master_admin_id")
    @JsonIgnore
    private MasterAdmin masterAdmin;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private VendorDriver driver;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private VendorVehicle vehicle;

    public CustomBooking() {
        super();
    }

    public CustomBooking(int bookingId, String bookingDate, String bookingTime, String bookingStatus,
            String bookingType,
            String bookingDetails, String bookingAmount, String customerName, String customerMobileNo,
            String pickupLocation, String dropLocation, String pickUpDate, String pickUpTime, String carType,
            String returnDate, String tripType, Vendor vendor, int collection, String fullName, String customerEmail,
            String communicationAddress, String alternativeMobileNo) {
        this.bookingId = bookingId;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.bookingStatus = bookingStatus;
        this.bookingType = bookingType;
        this.bookingDetails = bookingDetails;
        this.bookingAmount = bookingAmount;
        this.customerName = customerName;
        this.customerMobileNo = customerMobileNo;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.pickUpDate = pickUpDate;
        this.pickUpTime = pickUpTime;
        this.carType = carType;
        this.returnDate = returnDate;
        this.tripType = tripType;
        this.vendor = vendor;
        this.collection = collection;
        this.fullName = fullName;
        this.customerEmail = customerEmail;
        this.communicationAddress = communicationAddress;
        this.alternativeMobileNo = alternativeMobileNo;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getBookingDetails() {
        return bookingDetails;
    }

    public void setBookingDetails(String bookingDetails) {
        this.bookingDetails = bookingDetails;
    }

    public String getBookingAmount() {
        return bookingAmount;
    }

    public void setBookingAmount(String bookingAmount) {
        this.bookingAmount = bookingAmount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMobileNo() {
        return customerMobileNo;
    }

    public void setCustomerMobileNo(String customerMobileNo) {
        this.customerMobileNo = customerMobileNo;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public String getPickUpDate() {
        return pickUpDate;
    }

    public void setPickUpDate(String pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public String getPickUpTime() {
        return pickUpTime;
    }

    public void setPickUpTime(String pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public MasterAdmin getMasterAdmin() {
        return masterAdmin;
    }

    public void setMasterAdmin(MasterAdmin masterAdmin) {
        this.masterAdmin = masterAdmin;
    }

    public VendorDriver getDriver() {
        return driver;
    }

    public void setDriver(VendorDriver driver) {
        this.driver = driver;
    }

    public VendorVehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(VendorVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public int getCollection() {
        return collection;
    }

    public void setCollection(int collection) {
        this.collection = collection;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCommunicationAddress() {
        return communicationAddress;
    }

    public void setCommunicationAddress(String communicationAddress) {
        this.communicationAddress = communicationAddress;
    }

    public String getAlternativeMobileNo() {
        return alternativeMobileNo;
    }

    public void setAlternativeMobileNo(String alternativeMobileNo) {
        this.alternativeMobileNo = alternativeMobileNo;
    }
}
