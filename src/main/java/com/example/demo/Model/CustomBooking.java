package com.example.demo.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "custom_bookings")
@Getter
@Setter
public class CustomBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "booking_date")
    private String bookingDate;
    @Column(name = "booking_time")
    private String bookingTime;
    @Column(name = "booking_status")
    private String bookingStatus;
    @Column(name = "booking_type")
    private String bookingType;
    @Column(name = "booking_details")
    private String bookingDetails;
    @Column(name = "booking_amount")
    private String bookingAmount;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "customer_mobile_no")
    private String customerMobileNo;
    @Column(name = "pickup_location")
    private String pickupLocation;
    @Column(name = "drop_location")
    private String dropLocation;
    @Column(name = "pickup_date")
    private String pickUpDate;
    @Column(name = "pickup_time")
    private String pickUpTime; 
    @Column(name = "car_type")
    private String carType;
    @Column(name = "return_date")
    private String returnDate;
    @Column(name = "trip_type")
    private String tripType;



}
