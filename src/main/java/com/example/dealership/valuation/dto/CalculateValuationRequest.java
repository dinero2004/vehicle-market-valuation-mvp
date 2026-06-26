package com.example.dealership.valuation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CalculateValuationRequest {

    @NotNull
    private Long vehicleId;

    // Wider ranges return more comparable listings but can reduce precision.
    @Min(0)
    private Integer yearRange = 2;

    @Min(0)
    private Integer mileageRange = 30_000;

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
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
