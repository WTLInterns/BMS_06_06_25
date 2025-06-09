package com.example.demo.Repository;

import com.example.demo.Model.MasterAdmin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface MasterAdminRepository extends JpaRepository<MasterAdmin, Long> {
    @Query("SELECT m FROM MasterAdmin m LEFT JOIN FETCH m.vendors WHERE m.id = :id")
    Optional<MasterAdmin> findByIdWithVendors(@Param("id") Long id);
    Optional<MasterAdmin> findByEmail(String email);
    // Optionally, add custom query methods here
}
