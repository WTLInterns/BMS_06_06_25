package com.example.demo.Repository;

import com.example.demo.Model.MasterAdmin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterAdminRepository extends JpaRepository<MasterAdmin, Long> {
    Optional<MasterAdmin> findByEmail(String email);
    // Optionally, add custom query methods here
}
