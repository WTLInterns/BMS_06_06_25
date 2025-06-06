package com.example.demo.DTO;

import com.example.demo.Model.MasterAdmin;
import com.example.demo.Model.Vendor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VendorLoginResponse {
    private Vendor vendor;
    private MasterAdmin masterAdmin;
}
