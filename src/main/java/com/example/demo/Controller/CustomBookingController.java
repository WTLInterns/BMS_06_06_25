package com.example.demo.Controller;

import com.example.demo.Model.CustomBooking;
import com.example.demo.Service.CustomBookingService;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.WhatsAppService;
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

    @Autowired
    private WhatsAppService whatsAppService;

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
        String emailSubject = "🎉 Booking Confirmed - Your Ride is Ready!";
        String emailMessage = createCustomerEmailMessage(booking);

        String whatsappMessage = createCustomerWhatsAppMessage(booking);

        boolean emailSent = emailService.sendHtmlEmail(emailMessage, emailSubject, customerEmail);

        boolean whatsappSent = false;
        if (booking.getCustomerMobileNo() != null && !booking.getCustomerMobileNo().isEmpty()) {
            whatsappSent = whatsAppService.sendTextMessage(whatsappMessage, booking.getCustomerMobileNo());
        }

        String responseMessage = "";
        if (emailSent && whatsappSent) {
            responseMessage = "✅ Messages sent successfully to both Email and WhatsApp";
        } else if (emailSent) {
            responseMessage = "✅ Email sent successfully. ⚠️ WhatsApp failed";
        } else if (whatsappSent) {
            responseMessage = "✅ WhatsApp sent successfully. ⚠️ Email failed";
        } else {
            responseMessage = "❌ Failed to send both Email and WhatsApp";
        }

        return ResponseEntity.ok(responseMessage);
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
        String emailSubject = "🚨 New Booking Assignment - Action Required";
        String emailMessage = createDriverEmailMessage(booking);

        String whatsappMessage = createDriverWhatsAppMessage(booking);

        boolean emailSent = emailService.sendHtmlEmail(emailMessage, emailSubject, driverEmail);

        boolean whatsappSent = false;
        if (booking.getDriver() != null && booking.getDriver().getContactNo() != null
                && !booking.getDriver().getContactNo().isEmpty()) {
            whatsappSent = whatsAppService.sendTextMessage(whatsappMessage, booking.getDriver().getContactNo());
        }

        String responseMessage = "";
        if (emailSent && whatsappSent) {
            responseMessage = "✅ Messages sent successfully to both Email and WhatsApp";
        } else if (emailSent) {
            responseMessage = "✅ Email sent successfully. ⚠️ WhatsApp failed";
        } else if (whatsappSent) {
            responseMessage = "✅ WhatsApp sent successfully. ⚠️ Email failed";
        } else {
            responseMessage = "❌ Failed to send both Email and WhatsApp";
        }

        return ResponseEntity.ok(responseMessage);
    }


    private String createCustomerEmailMessage(CustomBooking booking) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: white; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); overflow: hidden; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; }
                    .header h1 { margin: 0; font-size: 24px; }
                    .content { padding: 30px; }
                    .section { margin-bottom: 25px; }
                    .section-title { color: #333; font-size: 18px; font-weight: bold; margin-bottom: 15px; border-bottom: 2px solid #667eea; padding-bottom: 5px; }
                    .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
                    .info-item { background-color: #f8f9fa; padding: 15px; border-radius: 8px; border-left: 4px solid #667eea; }
                    .info-label { font-weight: bold; color: #555; font-size: 12px; text-transform: uppercase; }
                    .info-value { color: #333; font-size: 16px; margin-top: 5px; }
                    .trip-section { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 20px; border-radius: 8px; margin: 20px 0; }
                    .footer { background-color: #333; color: white; padding: 20px; text-align: center; }
                    @media (max-width: 600px) { .info-grid { grid-template-columns: 1fr; } }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Booking Confirmed!</h1>
                        <p>Your ride is all set. Safe travels!</p>
                    </div>
                    
                    <div class="content">
                        <div class="section">
                            <div class="section-title">📋 Booking Information</div>
                            <div class="info-grid">
                                <div class="info-item">
                                    <div class="info-label">Booking ID</div>
                                    <div class="info-value">#%d</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Booking Date</div>
                                    <div class="info-value">%s</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Booking Time</div>
                                    <div class="info-value">%s</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Total Amount</div>
                                    <div class="info-value">₹%s</div>
                                </div>
                            </div>
                        </div>

                        <div class="trip-section">
                            <h3 style="margin: 0 0 15px 0;">🚗 Trip Details</h3>
                            <div class="info-grid">
                                <div style="background: rgba(255,255,255,0.1); padding: 15px; border-radius: 8px;">
                                    <div style="font-size: 12px; opacity: 0.8;">PICKUP</div>
                                    <div style="font-size: 16px; margin-top: 5px;">📍 %s</div>
                                    <div style="font-size: 14px; margin-top: 5px;">📅 %s at ⏰ %s</div>
                                </div>
                                <div style="background: rgba(255,255,255,0.1); padding: 15px; border-radius: 8px;">
                                    <div style="font-size: 12px; opacity: 0.8;">DROP</div>
                                    <div style="font-size: 16px; margin-top: 5px;">🏁 %s</div>
                                    <div style="font-size: 14px; margin-top: 5px;">🔄 Return: %s</div>
                                </div>
                            </div>
                        </div>

                        <div class="section">
                            <div class="section-title">🚙 Vehicle & Driver Details</div>
                            <div class="info-grid">
                                <div class="info-item">
                                    <div class="info-label">Driver Name</div>
                                    <div class="info-value">👨‍✈️ %s</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Contact Number</div>
                                    <div class="info-value">📞 %s</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Vehicle</div>
                                    <div class="info-value">🚗 %s</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Vehicle Number</div>
                                    <div class="info-value">🔢 %s</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>✅ Have a wonderful journey!</p>
                        <p>For any queries, contact support</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                booking.getBookingId(),
                booking.getBookingDate(),
                booking.getBookingTime(),
                booking.getBookingAmount(),
                booking.getPickupLocation(),
                booking.getPickUpDate(),
                booking.getPickUpTime(),
                booking.getDropLocation(),
                booking.getReturnDate(),
                booking.getDriver() != null ? booking.getDriver().getDriverName() : "TBD",
                booking.getDriver() != null ? booking.getDriver().getContactNo() : "TBD",
                booking.getVehicle() != null ? booking.getVehicle().getCarName() : "TBD",
                booking.getVehicle() != null ? booking.getVehicle().getVehicleNo() : "TBD"
        );
    }


    private String createCustomerWhatsAppMessage(CustomBooking booking) {
        return String.format("""
            🎉 *BOOKING CONFIRMED* 🎉
            
            📋 *Booking Details*
            🆔 ID: %d
            📅 Date: %s
            ⏰ Time: %s
            💰 Amount: ₹%s
            
            👤 *Customer Info*
            Name: %s
            📱 Mobile: %s
            
            🚗 *Trip Details*
            📍 Pickup: %s
            🏁 Drop: %s
            📅 Pickup Date: %s
            ⏰ Pickup Time: %s
            🔄 Return Date: %s
            
            🚙 *Vehicle & Driver*
            👨‍✈️ Driver: %s
            📞 Contact: %s
            🚗 Car: %s
            🔢 Vehicle No: %s
            
            ✅ Have a safe journey!
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
                booking.getDriver() != null ? booking.getDriver().getDriverName() : "TBD",
                booking.getDriver() != null ? booking.getDriver().getContactNo() : "TBD",
                booking.getVehicle() != null ? booking.getVehicle().getCarName() : "TBD",
                booking.getVehicle() != null ? booking.getVehicle().getVehicleNo() : "TBD"
        );
    }

    // DRIVER EMAIL MESSAGE
    private String createDriverEmailMessage(CustomBooking booking) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: white; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); overflow: hidden; }
                    .header { background: linear-gradient(135deg, #e74c3c 0%%, #c0392b 100%%); color: white; padding: 30px; text-align: center; }
                    .header h1 { margin: 0; font-size: 24px; }
                    .content { padding: 30px; }
                    .alert { background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 8px; margin-bottom: 20px; }
                    .section { margin-bottom: 25px; }
                    .section-title { color: #333; font-size: 18px; font-weight: bold; margin-bottom: 15px; border-bottom: 2px solid #e74c3c; padding-bottom: 5px; }
                    .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
                    .info-item { background-color: #f8f9fa; padding: 15px; border-radius: 8px; border-left: 4px solid #e74c3c; }
                    .info-label { font-weight: bold; color: #555; font-size: 12px; text-transform: uppercase; }
                    .info-value { color: #333; font-size: 16px; margin-top: 5px; }
                    .customer-section { background: linear-gradient(135deg, #3498db 0%%, #2980b9 100%%); color: white; padding: 20px; border-radius: 8px; margin: 20px 0; }
                    .footer { background-color: #333; color: white; padding: 20px; text-align: center; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🚨 New Booking Assignment</h1>
                        <p>Please review the details and prepare for pickup</p>
                    </div>
                    
                    <div class="content">
                        <div class="alert">
                            <strong>⚠️ Action Required:</strong> Please contact the customer before pickup time!
                        </div>

                        <div class="section">
                            <div class="section-title">📋 Booking Information</div>
                            <div class="info-grid">
                                <div class="info-item">
                                    <div class="info-label">Booking ID</div>
                                    <div class="info-value">#%d</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Booking Date</div>
                                    <div class="info-value">%s</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Booking Time</div>
                                    <div class="info-value">%s</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Trip Amount</div>
                                    <div class="info-value">₹%s</div>
                                </div>
                            </div>
                        </div>

                        <div class="customer-section">
                            <h3 style="margin: 0 0 15px 0;">👤 Customer Details</h3>
                            <div class="info-grid">
                                <div style="background: rgba(255,255,255,0.1); padding: 15px; border-radius: 8px;">
                                    <div style="font-size: 12px; opacity: 0.8;">CUSTOMER NAME</div>
                                    <div style="font-size: 16px; margin-top: 5px;">%s</div>
                                </div>
                                <div style="background: rgba(255,255,255,0.1); padding: 15px; border-radius: 8px;">
                                    <div style="font-size: 12px; opacity: 0.8;">CONTACT NUMBER</div>
                                    <div style="font-size: 16px; margin-top: 5px;">📞 %s</div>
                                </div>
                            </div>
                        </div>

                        <div class="section">
                            <div class="section-title">🗺️ Trip Route</div>
                            <div class="info-grid">
                                <div class="info-item">
                                    <div class="info-label">Pickup Location</div>
                                    <div class="info-value">📍 %s</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Drop Location</div>
                                    <div class="info-value">🏁 %s</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Pickup Date</div>
                                    <div class="info-value">📅 %s</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Pickup Time</div>
                                    <div class="info-value">⏰ %s</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Return Date</div>
                                    <div class="info-value">🔄 %s</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>🚗 Drive safely and provide excellent service!</p>
                        <p>Contact support for any issues</p>
                    </div>
                </div>
            </body>
            </html>
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
                booking.getReturnDate()
        );
    }

    // DRIVER WHATSAPP MESSAGE
    private String createDriverWhatsAppMessage(CustomBooking booking) {
        return String.format("""
            🚨 *NEW BOOKING ASSIGNMENT* 🚨
            
            📋 *Trip Details*
            🆔 Booking ID: %d
            📅 Date: %s
            ⏰ Time: %s
            💰 Amount: ₹%s
            
            👤 *Customer Details*
            Name: %s
            📱 Mobile: %s
            
            🗺️ *Route Info*
            📍 Pickup: %s
            🏁 Drop: %s
            📅 Pickup Date: %s
            ⏰ Pickup Time: %s
            🔄 Return Date: %s
            
            ⚠️ *Please contact customer before pickup!*
            🚗 Drive safely!
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
                booking.getReturnDate()
        );
    }
}
