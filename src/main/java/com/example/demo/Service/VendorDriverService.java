package com.example.demo.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Model.CustomBooking;
import com.example.demo.Model.VendorDriver;
import com.example.demo.Repository.CustomBookingRepository;
import com.example.demo.Repository.VendorDriverRepository;

@Service
public class VendorDriverService {

    private final String uploadDir = "src/main/resources/static/uploads/images/driver/";

    @Autowired
    private VendorDriverRepository vendorDriverRepository;

    @Autowired
    private CustomBookingRepository customBookingRepository;

    public List<VendorDriver> getDriversByVendor(Long vendorId) {
        return vendorDriverRepository.findByVendorId(vendorId);
    }

    public Optional<VendorDriver> getDriverById(Integer driverId) {
        return vendorDriverRepository.findById(driverId);
    }

    public VendorDriver createDriver(VendorDriver vendorDriver, MultipartFile driverImageFile,
            MultipartFile driverSelfieFile, MultipartFile dLnoImageFile, MultipartFile pvcImageFile,
            MultipartFile driverDoc1ImageFile, MultipartFile driverDoc2ImageFile, MultipartFile driverDoc3ImageFile)
            throws IOException {
        if (driverImageFile != null && !driverImageFile.isEmpty()) {
            String imageUrl = saveImage(driverImageFile, "driver");
            vendorDriver.setDriverImage(imageUrl);
        }
        if (driverSelfieFile != null && !driverSelfieFile.isEmpty()) {
            String imageUrl = saveImage(driverSelfieFile, "driver");
            vendorDriver.setDriverSelfie(imageUrl);
        }
        if (dLnoImageFile != null && !dLnoImageFile.isEmpty()) {
            String imageUrl = saveImage(dLnoImageFile, "driver");
            vendorDriver.setdLnoImage(imageUrl);
        }
        if (pvcImageFile != null && !pvcImageFile.isEmpty()) {
            String imageUrl = saveImage(pvcImageFile, "driver");
            vendorDriver.setPvcImage(imageUrl);
        }
        if (driverDoc1ImageFile != null && !driverDoc1ImageFile.isEmpty()) {
            String imageUrl = saveImage(driverDoc1ImageFile, "driver");
            vendorDriver.setDriverDoc1Image(imageUrl);
        }
        if (driverDoc2ImageFile != null && !driverDoc2ImageFile.isEmpty()) {
            String imageUrl = saveImage(driverDoc2ImageFile, "driver");
            vendorDriver.setDriverDoc2Image(imageUrl);
        }
        if (driverDoc3ImageFile != null && !driverDoc3ImageFile.isEmpty()) {
            String imageUrl = saveImage(driverDoc3ImageFile, "driver");
            vendorDriver.setDriverDoc3Image(imageUrl);
        }
        return vendorDriverRepository.save(vendorDriver);
    }

    public VendorDriver updateDriver(Integer driverId, VendorDriver driverDetails, MultipartFile driverImageFile,
            MultipartFile driverSelfieFile, MultipartFile dLnoImageFile, MultipartFile pvcImageFile,
            MultipartFile driverDoc1ImageFile, MultipartFile driverDoc2ImageFile, MultipartFile driverDoc3ImageFile)
            throws IOException {
        VendorDriver existingDriver = vendorDriverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        existingDriver.setDriverName(driverDetails.getDriverName());
        existingDriver.setContactNo(driverDetails.getContactNo());
        existingDriver.setAltContactNo(driverDetails.getAltContactNo());
        existingDriver.setAddress(driverDetails.getAddress());
        existingDriver.setdLNo(driverDetails.getdLNo());
        existingDriver.setPvcNo(driverDetails.getPvcNo());
        existingDriver.setEmailId(driverDetails.getEmailId());
        existingDriver.setDriverOtherDetails(driverDetails.getDriverOtherDetails());
        existingDriver.setPassword(driverDetails.getPassword());

        if (driverImageFile != null && !driverImageFile.isEmpty()) {
            String imageUrl = saveImage(driverImageFile, "driver");
            existingDriver.setDriverImage(imageUrl);
        }
        if (driverSelfieFile != null && !driverSelfieFile.isEmpty()) {
            String imageUrl = saveImage(driverSelfieFile, "driver");
            existingDriver.setDriverSelfie(imageUrl);
        }
        if (dLnoImageFile != null && !dLnoImageFile.isEmpty()) {
            String imageUrl = saveImage(dLnoImageFile, "driver");
            existingDriver.setdLnoImage(imageUrl);
        }
        if (pvcImageFile != null && !pvcImageFile.isEmpty()) {
            String imageUrl = saveImage(pvcImageFile, "driver");
            existingDriver.setPvcImage(imageUrl);
        }
        if (driverDoc1ImageFile != null && !driverDoc1ImageFile.isEmpty()) {
            String imageUrl = saveImage(driverDoc1ImageFile, "driver");
            existingDriver.setDriverDoc1Image(imageUrl);
        }
        if (driverDoc2ImageFile != null && !driverDoc2ImageFile.isEmpty()) {
            String imageUrl = saveImage(driverDoc2ImageFile, "driver");
            existingDriver.setDriverDoc2Image(imageUrl);
        }
        if (driverDoc3ImageFile != null && !driverDoc3ImageFile.isEmpty()) {
            String imageUrl = saveImage(driverDoc3ImageFile, "driver");
            existingDriver.setDriverDoc3Image(imageUrl);
        }

        return vendorDriverRepository.save(existingDriver);
    }

    public void deleteDriver(Integer driverId) {
        vendorDriverRepository.deleteById(driverId);
    }

    private String saveImage(MultipartFile imageFile, String entityType) throws IOException {
        String originalFilename = imageFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + extension;

        Path uploadPath = Paths.get("src/main/resources/static/uploads/images/" + entityType);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(newFileName);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/images/" + entityType + "/" + newFileName;
    }

    public VendorDriver updateStatus(int id, String status) {
        VendorDriver driver = this.vendorDriverRepository.findById(id).get();
        driver.setStatus(status);
        return vendorDriverRepository.save(driver);
    }

    public CustomBooking assignVendorDriverToBooking(Long vendorId, int bookingId, int vendorDriverId) {
        Optional<CustomBooking> bookingOptional = customBookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            throw new RuntimeException("Booking not found");
        }
        CustomBooking booking = bookingOptional.get();
        if (booking.getVendor() == null || !booking.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("Booking does not belong to the specified vendor");
        }
        Optional<VendorDriver> driverOptional = vendorDriverRepository.findById(vendorDriverId);
        if (driverOptional.isEmpty()) {
            throw new RuntimeException("Driver not found");
        }
        VendorDriver driver = driverOptional.get();
        if (!driver.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("Driver does not belong to the specified vendor");
        }
        booking.setDriver(driver);
        return customBookingRepository.save(booking);
    }
}
