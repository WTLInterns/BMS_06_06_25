package com.example.demo.Service;

import com.example.demo.Model.Vendor;
import com.example.demo.Model.MasterAdmin;
import com.example.demo.Repository.VendorRepository;
import com.example.demo.Repository.MasterAdminRepository;
import com.example.demo.DTO.VendorLoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;             

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VendorService {
    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private MasterAdminRepository masterAdminRepository;

    public VendorLoginResponse loginVendor(String email, String password) {
        Vendor vendor = findByEmail(email);
        if (vendor == null || !password.equals(vendor.getPassword())) return null;
        MasterAdmin masterAdmin = null;
        if (vendor.getMasterAdminId() != null) {
            masterAdmin = masterAdminRepository.findById(vendor.getMasterAdminId()).orElse(null);
        }
        return new VendorLoginResponse(vendor, masterAdmin);
    }

    public Vendor findByEmail(String email) {
        return vendorRepository.findByVendorEmail(email).orElse(null);
    }

    public void updatePassword(Long id, String newPassword) {
        Vendor vendor = vendorRepository.findById(id).orElse(null);
        if (vendor != null) {
            vendor.setPassword(newPassword);
            vendorRepository.save(vendor);
        }
    }

    public Vendor createVendor(Vendor vendor, MultipartFile vendorImage, MultipartFile gstNoImage, MultipartFile govtApprovalCertificate, MultipartFile vendorDocs, MultipartFile aadharPhoto, MultipartFile panPhoto, MultipartFile vendorCompanyLogo) throws IOException {
        String uploadDir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "uploads" + File.separator + "images" + File.separator + "vendor" + File.separator;
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
        if (vendorImage != null && !vendorImage.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + vendorImage.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            vendorImage.transferTo(dest);
            vendor.setVendorImage("/uploads/images/vendor/" + fileName);
            vendor.setVendorImageUrl("/uploads/images/vendor/" + fileName);
        }
        if (gstNoImage != null && !gstNoImage.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + gstNoImage.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            gstNoImage.transferTo(dest);
            vendor.setGstNoImage("/uploads/images/vendor/" + fileName);
            vendor.setGstNoImageUrl("/uploads/images/vendor/" + fileName);
        }
        if (govtApprovalCertificate != null && !govtApprovalCertificate.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + govtApprovalCertificate.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            govtApprovalCertificate.transferTo(dest);
            vendor.setGovtApprovalCertificate("/uploads/images/vendor/" + fileName);
            vendor.setGovtApprovalCertificateUrl("/uploads/images/vendor/" + fileName);
        }
        if (vendorDocs != null && !vendorDocs.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + vendorDocs.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            vendorDocs.transferTo(dest);
            vendor.setVendorDocs("/uploads/images/vendor/" + fileName);
            vendor.setVendorDocsUrl("/uploads/images/vendor/" + fileName);
        }
        if (aadharPhoto != null && !aadharPhoto.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + aadharPhoto.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            aadharPhoto.transferTo(dest);
            vendor.setAadharPhoto("/uploads/images/vendor/" + fileName);
            vendor.setAadharPhotoUrl("/uploads/images/vendor/" + fileName);
        }
        if (panPhoto != null && !panPhoto.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + panPhoto.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            panPhoto.transferTo(dest);
            vendor.setPanPhoto("/uploads/images/vendor/" + fileName);
            vendor.setPanPhotoUrl("/uploads/images/vendor/" + fileName);
        }
        if (vendorCompanyLogo != null && !vendorCompanyLogo.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + vendorCompanyLogo.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            vendorCompanyLogo.transferTo(dest);
            vendor.setVendorCompanyLogo("/uploads/images/vendor/" + fileName);
        }
        return vendorRepository.save(vendor);
    }

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    public Optional<Vendor> getVendorById(Long id) {
        return vendorRepository.findById(id);
    }

    public Vendor updateVendor(Long id, Vendor vendorDetails, MultipartFile vendorImage, MultipartFile gstNoImage, MultipartFile govtApprovalCertificate, MultipartFile vendorDocs, MultipartFile aadharPhoto, MultipartFile panPhoto, MultipartFile vendorCompanyLogo) throws IOException {
        Vendor vendor = vendorRepository.findById(id).orElseThrow(() -> new RuntimeException("Vendor not found"));
        vendor.setVendorFullName(vendorDetails.getVendorFullName());
        vendor.setVendorCompanyName(vendorDetails.getVendorCompanyName());
        vendor.setContactNo(vendorDetails.getContactNo());
        vendor.setAlternateMobileNo(vendorDetails.getAlternateMobileNo());
        vendor.setCity(vendorDetails.getCity());
        vendor.setVendorEmail(vendorDetails.getVendorEmail());
        vendor.setBankName(vendorDetails.getBankName());
        vendor.setBankAccountNo(vendorDetails.getBankAccountNo());
        vendor.setIfscCode(vendorDetails.getIfscCode());
        vendor.setAadharNo(vendorDetails.getAadharNo());
        vendor.setPanNo(vendorDetails.getPanNo());
        vendor.setGstNo(vendorDetails.getGstNo());
        vendor.setGstNoImage(vendorDetails.getGstNoImage());
        vendor.setUdyogAadharNo(vendorDetails.getUdyogAadharNo());
        vendor.setGovtApprovalCertificate(vendorDetails.getGovtApprovalCertificate());
        vendor.setVendorDocs(vendorDetails.getVendorDocs());
        vendor.setAadharPhoto(vendorDetails.getAadharPhoto());
        vendor.setPanPhoto(vendorDetails.getPanPhoto());
        vendor.setVendorOtherDetails(vendorDetails.getVendorOtherDetails());
        vendor.setGovtApprovalCertificateUrl(vendorDetails.getGovtApprovalCertificateUrl());
        vendor.setVendorDocsUrl(vendorDetails.getVendorDocsUrl());
        vendor.setVendorImageUrl(vendorDetails.getVendorImageUrl());
        vendor.setAadharPhotoUrl(vendorDetails.getAadharPhotoUrl());
        vendor.setPanPhotoUrl(vendorDetails.getPanPhotoUrl());
        vendor.setGstNoImageUrl(vendorDetails.getGstNoImageUrl());
        vendor.setStatus(vendorDetails.getStatus());
        vendor.setVendorCompanyLogo(vendorDetails.getVendorCompanyLogo());
        // Image/file updates
        if (vendorImage != null && !vendorImage.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + vendorImage.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "uploads" + File.separator + "images" + File.separator + "vendor" + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            File dest = new File(uploadDir + fileName);
            vendorImage.transferTo(dest);
            vendor.setVendorImage("/uploads/images/vendor/" + fileName);
            vendor.setVendorImageUrl("/uploads/images/vendor/" + fileName);
        }
        if (gstNoImage != null && !gstNoImage.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + gstNoImage.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "uploads" + File.separator + "images" + File.separator + "vendor" + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            File dest = new File(uploadDir + fileName);
            gstNoImage.transferTo(dest);
            vendor.setGstNoImage("/uploads/images/vendor/" + fileName);
            vendor.setGstNoImageUrl("/uploads/images/vendor/" + fileName);
        }
        if (govtApprovalCertificate != null && !govtApprovalCertificate.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + govtApprovalCertificate.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "uploads" + File.separator + "images" + File.separator + "vendor" + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            File dest = new File(uploadDir + fileName);
            govtApprovalCertificate.transferTo(dest);
            vendor.setGovtApprovalCertificate("/uploads/images/vendor/" + fileName);
            vendor.setGovtApprovalCertificateUrl("/uploads/images/vendor/" + fileName);
        }
        if (vendorDocs != null && !vendorDocs.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + vendorDocs.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "uploads" + File.separator + "images" + File.separator + "vendor" + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            File dest = new File(uploadDir + fileName);
            vendorDocs.transferTo(dest);
            vendor.setVendorDocs("/uploads/images/vendor/" + fileName);
            vendor.setVendorDocsUrl("/uploads/images/vendor/" + fileName);
        }
        if (aadharPhoto != null && !aadharPhoto.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + aadharPhoto.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "uploads" + File.separator + "images" + File.separator + "vendor" + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            File dest = new File(uploadDir + fileName);
            aadharPhoto.transferTo(dest);
            vendor.setAadharPhoto("/uploads/images/vendor/" + fileName);
            vendor.setAadharPhotoUrl("/uploads/images/vendor/" + fileName);
        }
        if (panPhoto != null && !panPhoto.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + panPhoto.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "uploads" + File.separator + "images" + File.separator + "vendor" + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            File dest = new File(uploadDir + fileName);
            panPhoto.transferTo(dest);
            vendor.setPanPhoto("/uploads/images/vendor/" + fileName);
            vendor.setPanPhotoUrl("/uploads/images/vendor/" + fileName);
        }
        if (vendorCompanyLogo != null && !vendorCompanyLogo.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + vendorCompanyLogo.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "uploads" + File.separator + "images" + File.separator + "vendor" + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            File dest = new File(uploadDir + fileName);
            vendorCompanyLogo.transferTo(dest);
            vendor.setVendorCompanyLogo("/uploads/images/vendor/" + fileName);
        }
        return vendorRepository.save(vendor);
    }
}

