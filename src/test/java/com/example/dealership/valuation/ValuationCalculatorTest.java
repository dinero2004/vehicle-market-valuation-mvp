package com.example.dealership.valuation;

import com.example.dealership.catalog.CatalogPrice;
import com.example.dealership.catalog.DepreciationProfile;
import com.example.dealership.market.VehicleMarketListing;
import com.example.dealership.vehicle.Vehicle;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValuationCalculatorTest {

    private final ValuationCalculator calculator = new ValuationCalculator();

    @Test
    void calculatesExplainableMarketValueForGoodVehicle() {
        Vehicle vehicle = vehicle(2021, 42_000, "Good", "None");
        List<VehicleMarketListing> listings = List.of(
                listing(2021, 40_000, "72000"),
                listing(2021, 44_000, "70000"),
                listing(2022, 36_000, "76000"),
                listing(2020, 50_000, "66000")
        );

        ValuationCalculationResult result = calculator.calculate(vehicle, listings, catalogPrice(), depreciationProfile(), 2026);

        assertThat(result.averageMarketPrice()).isEqualByComparingTo("71000");
        assertThat(result.estimatedMarketValue()).isEqualByComparingTo("71000");
        assertThat(result.catalogPrice()).isEqualByComparingTo("119900");
        assertThat(result.catalogDepreciationValue()).isEqualByComparingTo("65700");
        assertThat(result.marketCatalogGap()).isEqualByComparingTo("5300");
        assertThat(result.dealerPurchasePriceMin()).isEqualByComparingTo("67000");
        assertThat(result.dealerPurchasePriceMax()).isEqualByComparingTo("68000");
        assertThat(result.tradeInValue()).isEqualByComparingTo("62500");
        assertThat(result.recommendedSellingPrice()).isEqualByComparingTo("71000");
        assertThat(result.confidenceScore()).isEqualTo(0.55);
        assertThat(result.similarListingsCount()).isEqualTo(4);
        assertThat(result.explanation()).contains("Based on 4 similar listings");
        assertThat(result.explanation()).contains("Catalog value");
    }

    @Test
    void adjustsForConditionAndAccidentHistory() {
        Vehicle vehicle = vehicle(2021, 30_000, "Excellent", "Minor repaired");
        List<VehicleMarketListing> listings = List.of(
                listing(2021, 45_000, "70000"),
                listing(2021, 48_000, "69000"),
                listing(2021, 42_000, "72000"),
                listing(2021, 47_000, "71000")
        );

        ValuationCalculationResult result = calculator.calculate(vehicle, listings, catalogPrice(), depreciationProfile(), 2026);

        assertThat(result.mileageAdjustment()).isGreaterThan(BigDecimal.ZERO);
        assertThat(result.conditionAdjustment()).isGreaterThan(BigDecimal.ZERO);
        assertThat(result.accidentAdjustment()).isLessThan(BigDecimal.ZERO);
        assertThat(result.estimatedMarketValue()).isGreaterThan(result.averageMarketPrice());
        assertThat(result.catalogDepreciationValue()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void rejectsCalculationWithoutComparableListings() {
        Vehicle vehicle = vehicle(2021, 42_000, "Good", "None");

        assertThatThrownBy(() -> calculator.calculate(vehicle, List.of(), catalogPrice(), depreciationProfile(), 2026))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("similar market listing");
    }

    private Vehicle vehicle(int year, int mileage, String condition, String accidentHistory) {
        Vehicle vehicle = new Vehicle();
        vehicle.setBrand("BMW");
        vehicle.setModel("M4");
        vehicle.setVersion("Competition Coupe");
        vehicle.setManufactureYear(year);
        vehicle.setMileage(mileage);
        vehicle.setFuelType("Petrol");
        vehicle.setTransmission("Automatic");
        vehicle.setPowerKw(375);
        vehicle.setCondition(condition);
        vehicle.setAccidentHistory(accidentHistory);
        return vehicle;
    }

    private VehicleMarketListing listing(int year, int mileage, String price) {
        VehicleMarketListing listing = new VehicleMarketListing();
        listing.setSource("AutoScout24 CH");
        listing.setBrand("BMW");
        listing.setModel("M4");
        listing.setVersion("Competition Coupe");
        listing.setManufactureYear(year);
        listing.setMileage(mileage);
        listing.setPrice(new BigDecimal(price));
        return listing;
    }

    private CatalogPrice catalogPrice() {
        CatalogPrice catalogPrice = new CatalogPrice();
        catalogPrice.setBrand("BMW");
        catalogPrice.setModel("M4");
        catalogPrice.setVersion("Competition Coupe");
        catalogPrice.setManufactureYear(2021);
        catalogPrice.setCatalogPrice(new BigDecimal("119900"));
        catalogPrice.setCurrency("CHF");
        catalogPrice.setSource("Test");
        return catalogPrice;
    }

    private DepreciationProfile depreciationProfile() {
        DepreciationProfile profile = new DepreciationProfile();
        profile.setBrand("BMW");
        profile.setModel("M4");
        profile.setSegment("Performance coupe");
        profile.setYearOneRate(new BigDecimal("0.1500"));
        profile.setYearTwoRate(new BigDecimal("0.1100"));
        profile.setYearThreeRate(new BigDecimal("0.0800"));
        profile.setAnnualRateAfterYearThree(new BigDecimal("0.0550"));
        profile.setMileageRatePerKm(new BigDecimal("0.000004"));
        profile.setManufacturerRetentionFactor(new BigDecimal("1.0600"));
        return profile;
    }
}
