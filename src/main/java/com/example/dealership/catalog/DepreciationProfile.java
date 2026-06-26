package com.example.dealership.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "depreciation_profiles")
public class DepreciationProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String model;
    private String segment;

    @Column(name = "year_one_rate", precision = 6, scale = 4)
    private BigDecimal yearOneRate;

    @Column(name = "year_two_rate", precision = 6, scale = 4)
    private BigDecimal yearTwoRate;

    @Column(name = "year_three_rate", precision = 6, scale = 4)
    private BigDecimal yearThreeRate;

    @Column(name = "annual_rate_after_year_three", precision = 6, scale = 4)
    private BigDecimal annualRateAfterYearThree;

    @Column(name = "mileage_rate_per_km", precision = 10, scale = 8)
    private BigDecimal mileageRatePerKm;

    @Column(name = "manufacturer_retention_factor", precision = 6, scale = 4)
    private BigDecimal manufacturerRetentionFactor;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public BigDecimal getYearOneRate() {
        return yearOneRate;
    }

    public void setYearOneRate(BigDecimal yearOneRate) {
        this.yearOneRate = yearOneRate;
    }

    public BigDecimal getYearTwoRate() {
        return yearTwoRate;
    }

    public void setYearTwoRate(BigDecimal yearTwoRate) {
        this.yearTwoRate = yearTwoRate;
    }

    public BigDecimal getYearThreeRate() {
        return yearThreeRate;
    }

    public void setYearThreeRate(BigDecimal yearThreeRate) {
        this.yearThreeRate = yearThreeRate;
    }

    public BigDecimal getAnnualRateAfterYearThree() {
        return annualRateAfterYearThree;
    }

    public void setAnnualRateAfterYearThree(BigDecimal annualRateAfterYearThree) {
        this.annualRateAfterYearThree = annualRateAfterYearThree;
    }

    public BigDecimal getMileageRatePerKm() {
        return mileageRatePerKm;
    }

    public void setMileageRatePerKm(BigDecimal mileageRatePerKm) {
        this.mileageRatePerKm = mileageRatePerKm;
    }

    public BigDecimal getManufacturerRetentionFactor() {
        return manufacturerRetentionFactor;
    }

    public void setManufacturerRetentionFactor(BigDecimal manufacturerRetentionFactor) {
        this.manufacturerRetentionFactor = manufacturerRetentionFactor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
