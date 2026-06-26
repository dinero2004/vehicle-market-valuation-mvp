package com.example.dealership.valuation.dto;

import com.example.dealership.valuation.VehicleValuation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ValuationResponse(
        Long id,
        Long vehicleId,
        LocalDateTime valuationDate,
        BigDecimal averageMarketPrice,
        BigDecimal estimatedMarketValue,
        BigDecimal catalogPrice,
        BigDecimal catalogDepreciationValue,
        BigDecimal marketCatalogGap,
        BigDecimal dealerPurchasePriceMin,
        BigDecimal dealerPurchasePriceMax,
        BigDecimal tradeInValue,
        BigDecimal recommendedSellingPrice,
        Double confidenceScore,
        Integer similarListingsCount,
        BigDecimal mileageAdjustment,
        BigDecimal ageAdjustment,
        BigDecimal conditionAdjustment,
        BigDecimal accidentAdjustment,
        String explanation
) {
    public static ValuationResponse fromEntity(VehicleValuation valuation) {
        return new ValuationResponse(
                valuation.getId(),
                valuation.getVehicle().getId(),
                valuation.getValuationDate(),
                valuation.getAverageMarketPrice(),
                valuation.getEstimatedMarketValue(),
                valuation.getCatalogPrice(),
                valuation.getCatalogDepreciationValue(),
                valuation.getMarketCatalogGap(),
                valuation.getDealerPurchasePriceMin(),
                valuation.getDealerPurchasePriceMax(),
                valuation.getTradeInValue(),
                valuation.getRecommendedSellingPrice(),
                valuation.getConfidenceScore(),
                valuation.getSimilarListingsCount(),
                valuation.getMileageAdjustment(),
                valuation.getAgeAdjustment(),
                valuation.getConditionAdjustment(),
                valuation.getAccidentAdjustment(),
                valuation.getExplanation()
        );
    }
}
