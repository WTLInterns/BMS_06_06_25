package com.example.demo.Repository;

import com.example.demo.Model.VendorDriver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorDriverRepository extends JpaRepository<VendorDriver, Integer> {
    List<VendorDriver> findByVendorId(Long vendorId);
}
