package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class VendorVehicle {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int vendorCabId;

	private String carName;

	private String rCNo;

	private String rCImage;

	private String vehicleNo;

	private String vehicleNoImage;

	private String insuranceImage;

	private String permitImage;

	private String authorizationImage;

	private String cabNoPlateImage;

	private String cabImage;

	private String cabFrontImage;

	private String cabBackImage;

	private String cabOtherDetails;

	private String cabSideImage;

	private String status;

	@ManyToOne
	@JoinColumn(name = "vendor_id")
	@JsonBackReference
	private Vendor vendor;

	public VendorVehicle() {
		super();
	}

	public VendorVehicle(int vendorCabId, String carName, String rCNo, String rCImage, String vehicleNo,
			String vehicleNoImage, String insuranceImage, String permitImage, String authorizationImage,
			String cabNoPlateImage, String cabImage, String cabFrontImage, String cabBackImage, String cabOtherDetails,String status,
			String cabSideImage, Vendor vendor) {
		this.vendorCabId = vendorCabId;
		this.carName = carName;
		this.rCNo = rCNo;
		this.status=status;
		this.rCImage = rCImage;
		this.vehicleNo = vehicleNo;
		this.vehicleNoImage = vehicleNoImage;
		this.insuranceImage = insuranceImage;
		this.permitImage = permitImage;
		this.authorizationImage = authorizationImage;
		this.cabNoPlateImage = cabNoPlateImage;
		this.cabImage = cabImage;
		this.cabFrontImage = cabFrontImage;
		this.cabBackImage = cabBackImage;
		this.cabOtherDetails = cabOtherDetails;
		this.cabSideImage = cabSideImage;
		this.vendor = vendor;
	}

	public int getVendorCabId() {
		return vendorCabId;
	}

	public void setVendorCabId(int vendorCabId) {
		this.vendorCabId = vendorCabId;
	}

	public String getCarName() {
		return carName;
	}

	public void setCarName(String carName) {
		this.carName = carName;
	}

	public String getrCNo() {
		return rCNo;
	}

	public void setrCNo(String rCNo) {
		this.rCNo = rCNo;
	}

	public String getrCImage() {
		return rCImage;
	}

	public void setrCImage(String rCImage) {
		this.rCImage = rCImage;
	}

	

	public String getVehicleNo() {
		return vehicleNo;
	}

	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}

	public String getVehicleNoImage() {
		return vehicleNoImage;
	}

	public void setVehicleNoImage(String vehicleNoImage) {
		this.vehicleNoImage = vehicleNoImage;
	}

	public String getInsuranceImage() {
		return insuranceImage;
	}

	public void setInsuranceImage(String insuranceImage) {
		this.insuranceImage = insuranceImage;
	}

	public String getPermitImage() {
		return permitImage;
	}

	public void setPermitImage(String permitImage) {
		this.permitImage = permitImage;
	}

	public String getAuthorizationImage() {
		return authorizationImage;
	}

	public void setAuthorizationImage(String authorizationImage) {
		this.authorizationImage = authorizationImage;
	}

	public String getCabNoPlateImage() {
		return cabNoPlateImage;
	}

	public void setCabNoPlateImage(String cabNoPlateImage) {
		this.cabNoPlateImage = cabNoPlateImage;
	}

	public String getCabImage() {
		return cabImage;
	}

	public void setCabImage(String cabImage) {
		this.cabImage = cabImage;
	}

	public String getCabFrontImage() {
		return cabFrontImage;
	}

	public void setCabFrontImage(String cabFrontImage) {
		this.cabFrontImage = cabFrontImage;
	}

	public String getCabBackImage() {
		return cabBackImage;
	}

	public void setCabBackImage(String cabBackImage) {
		this.cabBackImage = cabBackImage;
	}

	public String getCabOtherDetails() {
		return cabOtherDetails;
	}

	public void setCabOtherDetails(String cabOtherDetails) {
		this.cabOtherDetails = cabOtherDetails;
	}

	public String getCabSideImage() {
		return cabSideImage;
	}

	public void setCabSideImage(String cabSideImage) {
		this.cabSideImage = cabSideImage;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
