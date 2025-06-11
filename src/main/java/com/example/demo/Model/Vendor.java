package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter; 
import com.example.demo.Model.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

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
}




