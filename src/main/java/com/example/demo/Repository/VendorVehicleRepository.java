package com.example.demo.Repository;

import com.example.demo.Model.VendorVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorVehicleRepository extends JpaRepository<VendorVehicle, Integer> {
    List<VendorVehicle> findByVendorId(Long vendorId);
}
