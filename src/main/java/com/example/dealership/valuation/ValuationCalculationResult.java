package com.example.dealership.valuation;

import java.math.BigDecimal;

public record ValuationCalculationResult(
        BigDecimal averageMarketPrice,
        BigDecimal estimatedMarketValue,
        BigDecimal catalogPrice,
        BigDecimal catalogDepreciationValue,
        BigDecimal marketCatalogGap,
        BigDecimal dealerPurchasePriceMin,
        BigDecimal dealerPurchasePriceMax,
        BigDecimal tradeInValue,
        BigDecimal recommendedSellingPrice,
        double confidenceScore,
        int similarListingsCount,
        BigDecimal mileageAdjustment,
        BigDecimal ageAdjustment,
        BigDecimal conditionAdjustment,
        BigDecimal accidentAdjustment,
        String explanation
) {
}
