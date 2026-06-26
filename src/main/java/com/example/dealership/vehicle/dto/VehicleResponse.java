package com.example.dealership.vehicle.dto;

import com.example.dealership.vehicle.Vehicle;

import java.math.BigDecimal;

public record VehicleResponse(
        Long id,
        String brand,
        String model,
        String version,
        Integer year,
        Integer mileage,
        String fuelType,
        String transmission,
        Integer powerKw,
        String condition,
        String accidentHistory,
        String equipment,
        String location,
        BigDecimal purchasePrice,
        BigDecimal listedPrice
) {
    public static VehicleResponse fromEntity(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getVersion(),
                vehicle.getManufactureYear(),
                vehicle.getMileage(),
                vehicle.getFuelType(),
                vehicle.getTransmission(),
                vehicle.getPowerKw(),
                vehicle.getCondition(),
                vehicle.getAccidentHistory(),
                vehicle.getEquipment(),
                vehicle.getLocation(),
                vehicle.getPurchasePrice(),
                vehicle.getListedPrice()
        );
    }
}
