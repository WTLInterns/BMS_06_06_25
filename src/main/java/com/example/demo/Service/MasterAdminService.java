package com.example.demo.Service;

import com.example.demo.Model.MasterAdmin;
import com.example.demo.Repository.MasterAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

@Service
public class MasterAdminService {

    @Autowired
    private MasterAdminRepository masterAdminRepository;

    @Autowired
    private com.example.demo.Repository.VendorRepository vendorRepository;

    // Login
    public MasterAdmin login(String email, String password) {
        MasterAdmin admin = masterAdminRepository.findByEmail(email).orElse(null);
        if (admin != null && admin.getPassword().equals(password)) {
            return admin;
        }
        return null;
    }

    // Create with image upload
    public MasterAdmin createMasterAdmin(MasterAdmin masterAdmin, MultipartFile profileImgFile) throws IOException {
        if (masterAdmin.getPassword() == null || masterAdmin.getPassword().isEmpty()) {
            masterAdmin.setPassword("master@123");
        }
        if (profileImgFile != null && !profileImgFile.isEmpty()) {
            String contentType = profileImgFile.getContentType();
            String originalFilename = profileImgFile.getOriginalFilename();
            // Accept all common image extensions
            String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".svg"};
            boolean valid = false;
            if (contentType != null && contentType.startsWith("image/")) {
                valid = true;
            } else if (originalFilename != null) {
                String lower = originalFilename.toLowerCase();
                for (String ext : allowedExtensions) {
                    if (lower.endsWith(ext)) {
                        valid = true;
                        break;
                    }
                }
            }
            if (!valid) {
                throw new IllegalArgumentException("Only image files are allowed!");
            }
            String uploadDir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "uploads" + File.separator + "images" + File.separator + "profile" + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
            File dest = new File(uploadDir + fileName);
            profileImgFile.transferTo(dest);
            masterAdmin.setProfileImg("/uploads/profile/" + fileName);
        } else {
            masterAdmin.setProfileImg(null);
        }
        return masterAdminRepository.save(masterAdmin);
    }

    // Read all
    public List<MasterAdmin> getAllMasterAdmins() {
        return masterAdminRepository.findAll();
    }

    // Read by id
    public Optional<MasterAdmin> getMasterAdminById(Long id) {
        return masterAdminRepository.findByIdWithVendors(id);
    }

    // Update
    public MasterAdmin updateMasterAdmin(Long id, com.example.demo.Model.MasterAdminForm form, MultipartFile profileImgFile) throws IOException {
        MasterAdmin masterAdmin = masterAdminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MasterAdmin not found with id: " + id));
        masterAdmin.setFullName(form.getFullName());
        masterAdmin.setEmail(form.getEmail());
        if (form.getPassword() != null && !form.getPassword().isEmpty()) {
            masterAdmin.setPassword(form.getPassword());
        }
    // else, keep the old password
        if (profileImgFile != null && !profileImgFile.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "uploads" + File.separator + "images" + File.separator + "profile" + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = UUID.randomUUID().toString() + "_" + profileImgFile.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            profileImgFile.transferTo(dest);
            masterAdmin.setProfileImg("/uploads/images/profile/" + fileName);
        }
        return masterAdminRepository.save(masterAdmin);
    }

    // Login
    public MasterAdmin findByEmail(String email) {
        return masterAdminRepository.findByEmail(email).orElse(null);
    }

    public void updatePassword(Long id, String newPassword) {
        MasterAdmin admin = masterAdminRepository.findById(id).orElse(null);
        if (admin != null) {
            admin.setPassword(newPassword);
            masterAdminRepository.save(admin);
        }
    }

    // OTP storage for forgot password
    private final Map<String, String> otpStorage = new HashMap<>();

    public void sendResetOTP(String email, EmailService emailService) {
        MasterAdmin admin = masterAdminRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Email not found"));
                .orElseThrow(() -> new RuntimeException("Email not found"));
        String otp = UUID.randomUUID().toString().substring(0, 6);
        otpStorage.put(email, otp);
        boolean sent = emailService.sendHtmlEmail(otp, "Password Reset OTP", email);
        if (!sent) throw new RuntimeException("Failed to send OTP email");
    }

    public boolean verifyOTP(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStorage.remove(email);
            return true;
        }
        return false;
    }

    public void resetPassword(String email, String newPassword) {
        MasterAdmin admin = masterAdminRepository.findAll().stream()
                .filter(a -> a.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Email not found"));
        admin.setPassword(newPassword);
        masterAdminRepository.save(admin);
    }

    // Delete
    public void deleteMasterAdmin(Long id) {
        masterAdminRepository.deleteById(id);
    }

    // Delete Vendor
    public void deleteVendorById(Long id) {
        if (!vendorRepository.existsById(id)) {
            throw new RuntimeException("Vendor not found");
        }
        vendorRepository.deleteById(id);
    }
}
