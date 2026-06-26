package com.example.dealership.valuation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ManualValuationRequest {

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    private String version;

    @NotNull
    @Min(1900)
    private Integer year;

    @NotNull
    @Min(0)
    private Integer mileage;

    private String fuelType;
    private String transmission;

    @Min(0)
    private Integer powerKw;

    private String condition = "Good";
    private String accidentHistory = "None";
    private String equipment;
    private String location;
    private BigDecimal purchasePrice;
    private BigDecimal listedPrice;

    @Min(0)
    private Integer yearRange = 2;

    @Min(0)
    private Integer mileageRange = 30_000;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public Integer getPowerKw() {
        return powerKw;
    }

    public void setPowerKw(Integer powerKw) {
        this.powerKw = powerKw;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getAccidentHistory() {
        return accidentHistory;
    }

    public void setAccidentHistory(String accidentHistory) {
        this.accidentHistory = accidentHistory;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getListedPrice() {
        return listedPrice;
    }

    public void setListedPrice(BigDecimal listedPrice) {
        this.listedPrice = listedPrice;
    }

    public Integer getYearRange() {
        return yearRange;
    }

    public void setYearRange(Integer yearRange) {
        this.yearRange = yearRange;
    }

    public Integer getMileageRange() {
        return mileageRange;
    }

    public void setMileageRange(Integer mileageRange) {
        this.mileageRange = mileageRange;
    }
}
