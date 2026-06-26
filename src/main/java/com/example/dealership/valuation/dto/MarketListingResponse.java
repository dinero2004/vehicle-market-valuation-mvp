package com.example.dealership.valuation.dto;

import com.example.dealership.market.VehicleMarketListing;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MarketListingResponse(
        Long id,
        String source,
        String brand,
        String model,
        String version,
        Integer year,
        Integer mileage,
        BigDecimal price,
        String location,
        LocalDate listingDate,
        String url
) {
    public static MarketListingResponse fromEntity(VehicleMarketListing listing) {
        return new MarketListingResponse(
                listing.getId(),
                listing.getSource(),
                listing.getBrand(),
                listing.getModel(),
                listing.getVersion(),
                listing.getManufactureYear(),
                listing.getMileage(),
                listing.getPrice(),
                listing.getLocation(),
                listing.getListingDate(),
                listing.getUrl()
        );
    }
}
