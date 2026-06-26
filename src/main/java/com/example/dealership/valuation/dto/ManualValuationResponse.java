package com.example.dealership.valuation.dto;

import com.example.dealership.valuation.ValuationCalculationResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ManualValuationResponse(
        boolean evaluationPossible,
        String message,
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
        String explanation,
        List<MarketListingResponse> similarListings
) {
    public static ManualValuationResponse success(
            LocalDateTime valuationDate,
            ValuationCalculationResult result,
            List<MarketListingResponse> similarListings
    ) {
        return new ManualValuationResponse(
                true,
                "Valuation calculated successfully.",
                valuationDate,
                result.averageMarketPrice(),
                result.estimatedMarketValue(),
                result.catalogPrice(),
                result.catalogDepreciationValue(),
                result.marketCatalogGap(),
                result.dealerPurchasePriceMin(),
                result.dealerPurchasePriceMax(),
                result.tradeInValue(),
                result.recommendedSellingPrice(),
                result.confidenceScore(),
                result.similarListingsCount(),
                result.mileageAdjustment(),
                result.ageAdjustment(),
                result.conditionAdjustment(),
                result.accidentAdjustment(),
                result.explanation(),
                similarListings
        );
    }

    public static ManualValuationResponse notEvaluable(
            String message,
            LocalDateTime valuationDate,
            int similarListingsCount,
            List<MarketListingResponse> similarListings
    ) {
        return new ManualValuationResponse(
                false,
                message,
                valuationDate,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                similarListingsCount,
                null,
                null,
                null,
                null,
                null,
                similarListings
        );
    }
}
