package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.example.demo.Model.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "vendor")
@Getter
@Setter
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String vendorFullName;
    private String vendorCompanyName;
    private String contactNo;
    private String alternateMobileNo;
    private String city;
    private String vendorEmail;
    private String bankName;
    private String bankAccountNo;
    private String ifscCode;
    private String aadharNo;
    private String panNo;
    private String gstNo;
    private String gstNoImage; // path
    private String udyogAadharNo;
    private String govtApprovalCertificate; // path
    private String vendorDocs; // path
    private String vendorImage; // path
    private String aadharPhoto; // path
    private String panPhoto; // path
    private String vendorOtherDetails;
    private String vendorCompanyLogo;
    private String govtApprovalCertificateUrl;
    private String vendorDocsUrl;
    private String vendorImageUrl;
    private String aadharPhotoUrl;
    private String panPhotoUrl;
    private String gstNoImageUrl;
    private String status;
    private String role = "Vendor";
    private String password;

    // @Column(name = "master_admin_id")
    // private Long masterAdminId;

    @ManyToOne
    @JoinColumn(name = "master_admin_id")
    @JsonBackReference
    private MasterAdmin masterAdmin;

    public Long getMasterAdminId() {
        return masterAdmin != null ? masterAdmin.getId() : null;
    }

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<VendorDriver> vendorDriver;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<VendorVehicle> vendorVehicles;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CustomBooking> customBookings;

    public Vendor(Long id, String vendorCompanyName, String contactNo, String alternateMobileNo, String city,
            String vendorEmail, String bankName, String bankAccountNo, String ifscCode, String aadharNo, String panNo,
            String udyogAadharNo, String govtApprovalCertificate, String vendorDocs, String vendorImage,
            String aadharPhoto, String panPhoto, String vendorOtherDetails, String vendorCompanyLogo, String password,
            List<VendorDriver> vendorDriver, List<VendorVehicle> vendorVehicles, List<CustomBooking> customBookings) {
        this.id = id;
        this.vendorCompanyName = vendorCompanyName;
        this.contactNo = contactNo;
        this.alternateMobileNo = alternateMobileNo;
        this.city = city;
        this.vendorEmail = vendorEmail;
        this.bankName = bankName;
        this.bankAccountNo = bankAccountNo;
        this.ifscCode = ifscCode;
        this.aadharNo = aadharNo;
        this.panNo = panNo;
        this.udyogAadharNo = udyogAadharNo;
        this.govtApprovalCertificate = govtApprovalCertificate;
        this.vendorDocs = vendorDocs;
        this.vendorImage = vendorImage;
        this.aadharPhoto = aadharPhoto;
        this.panPhoto = panPhoto;
        this.vendorOtherDetails = vendorOtherDetails;
        this.vendorCompanyLogo = vendorCompanyLogo;
        this.password = password;
        this.vendorDriver = vendorDriver;
        this.vendorVehicles = vendorVehicles;
        this.customBookings = customBookings;
        // this.user=user;

    }

    public Vendor() {
    }

}
