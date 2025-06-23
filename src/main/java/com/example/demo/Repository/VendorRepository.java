package com.example.demo.Repository;

import com.example.demo.Model.Vendor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByVendorEmail(String vendorEmail);
    // Add custom queries if needed
}
