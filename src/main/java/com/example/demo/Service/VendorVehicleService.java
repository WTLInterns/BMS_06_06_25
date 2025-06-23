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
import com.example.demo.Model.VendorVehicle;
import com.example.demo.Repository.CustomBookingRepository;
import com.example.demo.Repository.VendorVehicleRepository;

@Service
public class VendorVehicleService {

    private final String uploadDir = "src/main/resources/static/uploads/images/vehicle/";

    @Autowired
    private VendorVehicleRepository vendorVehicleRepository;

    @Autowired
    private CustomBookingRepository customBookingRepository;

    public List<VendorVehicle> getVehiclesByVendor(Long vendorId) {
        return vendorVehicleRepository.findByVendorId(vendorId);
    }

    public Optional<VendorVehicle> getVehicleById(Integer vehicleId) {
        return vendorVehicleRepository.findById(vehicleId);
    }

    public VendorVehicle createVehicle(VendorVehicle vendorVehicle, MultipartFile rCImageFile,
            MultipartFile vehicleNoImageFile, MultipartFile insuranceImageFile, MultipartFile permitImageFile,
            MultipartFile authorizationImageFile, MultipartFile cabNoPlateImageFile, MultipartFile cabImageFile,
            MultipartFile cabFrontImageFile, MultipartFile cabBackImageFile, MultipartFile cabSideImageFile)
            throws IOException {
        if (rCImageFile != null && !rCImageFile.isEmpty()) {
            String imageUrl = saveImage(rCImageFile, "vehicle");
            vendorVehicle.setrCImage(imageUrl);
        }
        if (vehicleNoImageFile != null && !vehicleNoImageFile.isEmpty()) {
            String imageUrl = saveImage(vehicleNoImageFile, "vehicle");
            vendorVehicle.setVehicleNoImage(imageUrl);
        }
        if (insuranceImageFile != null && !insuranceImageFile.isEmpty()) {
            String imageUrl = saveImage(insuranceImageFile, "vehicle");
            vendorVehicle.setInsuranceImage(imageUrl);
        }
        if (permitImageFile != null && !permitImageFile.isEmpty()) {
            String imageUrl = saveImage(permitImageFile, "vehicle");
            vendorVehicle.setPermitImage(imageUrl);
        }
        if (authorizationImageFile != null && !authorizationImageFile.isEmpty()) {
            String imageUrl = saveImage(authorizationImageFile, "vehicle");
            vendorVehicle.setAuthorizationImage(imageUrl);
        }
        if (cabNoPlateImageFile != null && !cabNoPlateImageFile.isEmpty()) {
            String imageUrl = saveImage(cabNoPlateImageFile, "vehicle");
            vendorVehicle.setCabNoPlateImage(imageUrl);
        }
        if (cabImageFile != null && !cabImageFile.isEmpty()) {
            String imageUrl = saveImage(cabImageFile, "vehicle");
            vendorVehicle.setCabImage(imageUrl);
        }
        if (cabFrontImageFile != null && !cabFrontImageFile.isEmpty()) {
            String imageUrl = saveImage(cabFrontImageFile, "vehicle");
            vendorVehicle.setCabFrontImage(imageUrl);
        }
        if (cabBackImageFile != null && !cabBackImageFile.isEmpty()) {
            String imageUrl = saveImage(cabBackImageFile, "vehicle");
            vendorVehicle.setCabBackImage(imageUrl);
        }
        if (cabSideImageFile != null && !cabSideImageFile.isEmpty()) {
            String imageUrl = saveImage(cabSideImageFile, "vehicle");
            vendorVehicle.setCabSideImage(imageUrl);
        }
        return vendorVehicleRepository.save(vendorVehicle);
    }

    public VendorVehicle updateVehicle(Integer vehicleId, VendorVehicle vehicleDetails, MultipartFile rCImageFile,
            MultipartFile vehicleNoImageFile, MultipartFile insuranceImageFile, MultipartFile permitImageFile,
            MultipartFile authorizationImageFile, MultipartFile cabNoPlateImageFile, MultipartFile cabImageFile,
            MultipartFile cabFrontImageFile, MultipartFile cabBackImageFile, MultipartFile cabSideImageFile)
            throws IOException {
        VendorVehicle existingVehicle = vendorVehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        existingVehicle.setCarName(vehicleDetails.getCarName());
        existingVehicle.setrCNo(vehicleDetails.getrCNo());
        existingVehicle.setVehicleNo(vehicleDetails.getVehicleNo());
        existingVehicle.setCabOtherDetails(vehicleDetails.getCabOtherDetails());

        if (rCImageFile != null && !rCImageFile.isEmpty()) {
            String imageUrl = saveImage(rCImageFile, "vehicle");
            existingVehicle.setrCImage(imageUrl);
        }
        if (vehicleNoImageFile != null && !vehicleNoImageFile.isEmpty()) {
            String imageUrl = saveImage(vehicleNoImageFile, "vehicle");
            existingVehicle.setVehicleNoImage(imageUrl);
        }
        if (insuranceImageFile != null && !insuranceImageFile.isEmpty()) {
            String imageUrl = saveImage(insuranceImageFile, "vehicle");
            existingVehicle.setInsuranceImage(imageUrl);
        }
        if (permitImageFile != null && !permitImageFile.isEmpty()) {
            String imageUrl = saveImage(permitImageFile, "vehicle");
            existingVehicle.setPermitImage(imageUrl);
        }
        if (authorizationImageFile != null && !authorizationImageFile.isEmpty()) {
            String imageUrl = saveImage(authorizationImageFile, "vehicle");
            existingVehicle.setAuthorizationImage(imageUrl);
        }
        if (cabNoPlateImageFile != null && !cabNoPlateImageFile.isEmpty()) {
            String imageUrl = saveImage(cabNoPlateImageFile, "vehicle");
            existingVehicle.setCabNoPlateImage(imageUrl);
        }
        if (cabImageFile != null && !cabImageFile.isEmpty()) {
            String imageUrl = saveImage(cabImageFile, "vehicle");
            existingVehicle.setCabImage(imageUrl);
        }
        if (cabFrontImageFile != null && !cabFrontImageFile.isEmpty()) {
            String imageUrl = saveImage(cabFrontImageFile, "vehicle");
            existingVehicle.setCabFrontImage(imageUrl);
        }
        if (cabBackImageFile != null && !cabBackImageFile.isEmpty()) {
            String imageUrl = saveImage(cabBackImageFile, "vehicle");
            existingVehicle.setCabBackImage(imageUrl);
        }
        if (cabSideImageFile != null && !cabSideImageFile.isEmpty()) {
            String imageUrl = saveImage(cabSideImageFile, "vehicle");
            existingVehicle.setCabSideImage(imageUrl);
        }

        return vendorVehicleRepository.save(existingVehicle);
    }

    public void deleteVehicle(Integer vehicleId) {
        vendorVehicleRepository.deleteById(vehicleId);
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

    public VendorVehicle updateStatus(int id, String status) {
        VendorVehicle vehicle = this.vendorVehicleRepository.findById(id).get();
        vehicle.setStatus(status);
        return vendorVehicleRepository.save(vehicle);
    }

    public CustomBooking assignVendorCabToBooking(Long vendorId, int bookingId, int vendorCabId) {
        Optional<CustomBooking> bookingOptional = customBookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            throw new RuntimeException("Booking not found");
        }
        CustomBooking booking = bookingOptional.get();
        if (booking.getVendor() == null || !booking.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("Booking does not belong to the specified vendor");
        }
        Optional<VendorVehicle> vehicleOptional = vendorVehicleRepository.findById(vendorCabId);
        if (vehicleOptional.isEmpty()) {
            throw new RuntimeException("Vehicle not found");
        }
        VendorVehicle vehicle = vehicleOptional.get();
        if (!vehicle.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("Vehicle does not belong to the specified vendor");
        }
        booking.setVehicle(vehicle);
        return customBookingRepository.save(booking);
    }
}
