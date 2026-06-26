package com.example.dealership.valuation;

import com.example.dealership.market.VehicleMarketListing;
import com.example.dealership.catalog.CatalogPrice;
import com.example.dealership.catalog.DepreciationProfile;
import com.example.dealership.vehicle.Vehicle;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

@Component
public class ValuationCalculator {

    private static final BigDecimal MILEAGE_VALUE_PER_KM = new BigDecimal("0.08");
    private static final BigDecimal YEAR_VALUE = new BigDecimal("1200");
    private static final BigDecimal TRADE_IN_FACTOR = new BigDecimal("0.88");
    private static final BigDecimal DEALER_MARGIN_MIN = new BigDecimal("3000");
    private static final BigDecimal DEALER_MARGIN_MAX = new BigDecimal("4000");
    private static final BigDecimal MINIMUM_CATALOG_RETENTION = new BigDecimal("0.20");

    public ValuationCalculationResult calculate(
            Vehicle vehicle,
            List<VehicleMarketListing> similarListings,
            CatalogPrice catalogPrice,
            DepreciationProfile depreciationProfile,
            int currentYear
    ) {
        if (similarListings.isEmpty()) {
            throw new IllegalArgumentException("At least one similar market listing is required.");
        }

        BigDecimal averagePrice = averagePrice(similarListings);
        int averageMileage = averageMileage(similarListings);
        int averageYear = averageYear(similarListings);

        // Mileage adjustment: lower mileage than the market average increases the value.
        BigDecimal mileageAdjustment = BigDecimal.valueOf(averageMileage - vehicle.getMileage())
                .multiply(MILEAGE_VALUE_PER_KM);

        // Age adjustment: newer vehicles receive a simple fixed amount per year.
        BigDecimal ageAdjustment = BigDecimal.valueOf(vehicle.getManufactureYear() - averageYear)
                .multiply(YEAR_VALUE);

        BigDecimal valueBeforeCondition = averagePrice.add(mileageAdjustment).add(ageAdjustment);

        // Condition and accident history are percentage adjustments on the market-adjusted value.
        BigDecimal conditionAdjustment = valueBeforeCondition.multiply(conditionFactor(vehicle.getCondition()));
        BigDecimal accidentAdjustment = valueBeforeCondition.multiply(accidentFactor(vehicle.getAccidentHistory()));

        BigDecimal estimatedMarketValue = valueBeforeCondition
                .add(conditionAdjustment)
                .add(accidentAdjustment);

        if (estimatedMarketValue.compareTo(BigDecimal.ZERO) < 0) {
            estimatedMarketValue = BigDecimal.ZERO;
        }

        BigDecimal roundedMarketValue = roundToNearestHundred(estimatedMarketValue);
        BigDecimal tradeInValue = roundToNearestHundred(roundedMarketValue.multiply(TRADE_IN_FACTOR));
        BigDecimal recommendedSellingPrice = roundedMarketValue;
        BigDecimal catalogDepreciationValue = calculateCatalogDepreciationValue(
                vehicle,
                catalogPrice,
                depreciationProfile,
                currentYear
        );
        BigDecimal roundedCatalogValue = roundToNearestHundred(catalogDepreciationValue);
        BigDecimal marketCatalogGap = roundToNearestHundred(roundedMarketValue.subtract(roundedCatalogValue));
        BigDecimal dealerPurchasePriceMin = roundToNearestHundred(roundedMarketValue.subtract(DEALER_MARGIN_MAX));
        BigDecimal dealerPurchasePriceMax = roundToNearestHundred(roundedMarketValue.subtract(DEALER_MARGIN_MIN));

        double confidenceScore = confidenceScore(similarListings, averagePrice);
        String explanation = String.format(
                Locale.US,
                "Based on %d similar listings. Average market price %.0f, mileage adjustment %.0f, age adjustment %.0f, condition adjustment %.0f, accident adjustment %.0f. Catalog value %.0f uses original catalog price %.0f and depreciation profile %s.",
                similarListings.size(),
                averagePrice,
                mileageAdjustment,
                ageAdjustment,
                conditionAdjustment,
                accidentAdjustment,
                roundedCatalogValue,
                catalogPrice.getCatalogPrice(),
                depreciationProfile.getSegment()
        );

        return new ValuationCalculationResult(
                roundToNearestHundred(averagePrice),
                roundedMarketValue,
                catalogPrice.getCatalogPrice(),
                roundedCatalogValue,
                marketCatalogGap,
                dealerPurchasePriceMin,
                dealerPurchasePriceMax,
                tradeInValue,
                recommendedSellingPrice,
                confidenceScore,
                similarListings.size(),
                roundToNearestHundred(mileageAdjustment),
                roundToNearestHundred(ageAdjustment),
                roundToNearestHundred(conditionAdjustment),
                roundToNearestHundred(accidentAdjustment),
                explanation
        );
    }

    private BigDecimal calculateCatalogDepreciationValue(
            Vehicle vehicle,
            CatalogPrice catalogPrice,
            DepreciationProfile profile,
            int currentYear
    ) {
        int age = Math.max(0, currentYear - vehicle.getManufactureYear());
        BigDecimal ageRetention = ageRetentionFactor(age, profile);
        BigDecimal mileageRetention = BigDecimal.ONE.subtract(
                BigDecimal.valueOf(vehicle.getMileage()).multiply(profile.getMileageRatePerKm())
        ).max(MINIMUM_CATALOG_RETENTION);
        BigDecimal conditionRetention = BigDecimal.ONE.add(conditionFactor(vehicle.getCondition()));
        BigDecimal accidentRetention = BigDecimal.ONE.add(accidentFactor(vehicle.getAccidentHistory()));

        BigDecimal catalogValue = catalogPrice.getCatalogPrice()
                .multiply(ageRetention)
                .multiply(mileageRetention)
                .multiply(profile.getManufacturerRetentionFactor())
                .multiply(conditionRetention)
                .multiply(accidentRetention);

        return catalogValue.max(BigDecimal.ZERO);
    }

    private BigDecimal ageRetentionFactor(int age, DepreciationProfile profile) {
        BigDecimal retention = BigDecimal.ONE;
        for (int year = 1; year <= age; year++) {
            BigDecimal rate = switch (year) {
                case 1 -> profile.getYearOneRate();
                case 2 -> profile.getYearTwoRate();
                case 3 -> profile.getYearThreeRate();
                default -> profile.getAnnualRateAfterYearThree();
            };
            retention = retention.multiply(BigDecimal.ONE.subtract(rate));
        }
        return retention.max(MINIMUM_CATALOG_RETENTION);
    }

    private BigDecimal averagePrice(List<VehicleMarketListing> listings) {
        BigDecimal total = listings.stream()
                .map(VehicleMarketListing::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(listings.size()), MathContext.DECIMAL64);
    }

    private int averageMileage(List<VehicleMarketListing> listings) {
        return (int) Math.round(listings.stream()
                .mapToInt(VehicleMarketListing::getMileage)
                .average()
                .orElse(0));
    }

    private int averageYear(List<VehicleMarketListing> listings) {
        return (int) Math.round(listings.stream()
                .mapToInt(VehicleMarketListing::getManufactureYear)
                .average()
                .orElse(0));
    }

    private BigDecimal conditionFactor(String condition) {
        String normalized = normalize(condition);
        return switch (normalized) {
            case "excellent" -> new BigDecimal("0.05");
            case "fair" -> new BigDecimal("-0.07");
            case "poor" -> new BigDecimal("-0.15");
            default -> BigDecimal.ZERO;
        };
    }

    private BigDecimal accidentFactor(String accidentHistory) {
        String normalized = normalize(accidentHistory);
        if (normalized.contains("major") || normalized.equals("yes")) {
            return new BigDecimal("-0.12");
        }
        if (normalized.contains("minor")) {
            return new BigDecimal("-0.05");
        }
        if (normalized.contains("unknown")) {
            return new BigDecimal("-0.03");
        }
        return BigDecimal.ZERO;
    }

    private double confidenceScore(List<VehicleMarketListing> listings, BigDecimal averagePrice) {
        double base = switch (Math.min(listings.size(), 25)) {
            case 25 -> 0.90;
            default -> {
                if (listings.size() >= 15) {
                    yield 0.80;
                }
                if (listings.size() >= 8) {
                    yield 0.70;
                }
                if (listings.size() >= 4) {
                    yield 0.55;
                }
                yield 0.35;
            }
        };

        double average = averagePrice.doubleValue();
        double variance = listings.stream()
                .map(VehicleMarketListing::getPrice)
                .mapToDouble(BigDecimal::doubleValue)
                .map(price -> Math.pow(price - average, 2))
                .average()
                .orElse(0);
        double coefficientOfVariation = Math.sqrt(variance) / average;

        if (coefficientOfVariation > 0.30) {
            base -= 0.15;
        } else if (coefficientOfVariation > 0.20) {
            base -= 0.08;
        }

        return BigDecimal.valueOf(Math.max(0.25, Math.min(0.95, base)))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private BigDecimal roundToNearestHundred(BigDecimal value) {
        return value.divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
