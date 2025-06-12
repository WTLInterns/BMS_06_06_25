package com.example.demo.Repository;

import com.example.demo.Model.CustomBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomBookingRepository extends JpaRepository<CustomBooking, Integer> {
    List<CustomBooking> findByMasterAdminId(Long masterAdminId);
    List<CustomBooking> findByVendorId(Long vendorId);
}
