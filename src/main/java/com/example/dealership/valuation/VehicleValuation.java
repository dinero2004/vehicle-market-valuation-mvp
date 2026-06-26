package com.example.dealership.valuation;

import com.example.dealership.vehicle.Vehicle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_valuations")
public class VehicleValuation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "valuation_date")
    private LocalDateTime valuationDate;

    @Column(name = "average_market_price", precision = 12, scale = 2)
    private BigDecimal averageMarketPrice;

    @Column(name = "estimated_market_value", precision = 12, scale = 2)
    private BigDecimal estimatedMarketValue;

    @Column(name = "catalog_price", precision = 12, scale = 2)
    private BigDecimal catalogPrice;

    @Column(name = "catalog_depreciation_value", precision = 12, scale = 2)
    private BigDecimal catalogDepreciationValue;

    @Column(name = "market_catalog_gap", precision = 12, scale = 2)
    private BigDecimal marketCatalogGap;

    @Column(name = "dealer_purchase_price_min", precision = 12, scale = 2)
    private BigDecimal dealerPurchasePriceMin;

    @Column(name = "dealer_purchase_price_max", precision = 12, scale = 2)
    private BigDecimal dealerPurchasePriceMax;

    @Column(name = "trade_in_value", precision = 12, scale = 2)
    private BigDecimal tradeInValue;

    @Column(name = "recommended_selling_price", precision = 12, scale = 2)
    private BigDecimal recommendedSellingPrice;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "similar_listings_count")
    private Integer similarListingsCount;

    @Column(name = "mileage_adjustment", precision = 12, scale = 2)
    private BigDecimal mileageAdjustment;

    @Column(name = "age_adjustment", precision = 12, scale = 2)
    private BigDecimal ageAdjustment;

    @Column(name = "condition_adjustment", precision = 12, scale = 2)
    private BigDecimal conditionAdjustment;

    @Column(name = "accident_adjustment", precision = 12, scale = 2)
    private BigDecimal accidentAdjustment;

    @Column(length = 2000)
    private String explanation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public LocalDateTime getValuationDate() {
        return valuationDate;
    }

    public void setValuationDate(LocalDateTime valuationDate) {
        this.valuationDate = valuationDate;
    }

    public BigDecimal getAverageMarketPrice() {
        return averageMarketPrice;
    }

    public void setAverageMarketPrice(BigDecimal averageMarketPrice) {
        this.averageMarketPrice = averageMarketPrice;
    }

    public BigDecimal getEstimatedMarketValue() {
        return estimatedMarketValue;
    }

    public void setEstimatedMarketValue(BigDecimal estimatedMarketValue) {
        this.estimatedMarketValue = estimatedMarketValue;
    }

    public BigDecimal getCatalogPrice() {
        return catalogPrice;
    }

    public void setCatalogPrice(BigDecimal catalogPrice) {
        this.catalogPrice = catalogPrice;
    }

    public BigDecimal getCatalogDepreciationValue() {
        return catalogDepreciationValue;
    }

    public void setCatalogDepreciationValue(BigDecimal catalogDepreciationValue) {
        this.catalogDepreciationValue = catalogDepreciationValue;
    }

    public BigDecimal getMarketCatalogGap() {
        return marketCatalogGap;
    }

    public void setMarketCatalogGap(BigDecimal marketCatalogGap) {
        this.marketCatalogGap = marketCatalogGap;
    }

    public BigDecimal getDealerPurchasePriceMin() {
        return dealerPurchasePriceMin;
    }

    public void setDealerPurchasePriceMin(BigDecimal dealerPurchasePriceMin) {
        this.dealerPurchasePriceMin = dealerPurchasePriceMin;
    }

    public BigDecimal getDealerPurchasePriceMax() {
        return dealerPurchasePriceMax;
    }

    public void setDealerPurchasePriceMax(BigDecimal dealerPurchasePriceMax) {
        this.dealerPurchasePriceMax = dealerPurchasePriceMax;
    }

    public BigDecimal getTradeInValue() {
        return tradeInValue;
    }

    public void setTradeInValue(BigDecimal tradeInValue) {
        this.tradeInValue = tradeInValue;
    }

    public BigDecimal getRecommendedSellingPrice() {
        return recommendedSellingPrice;
    }

    public void setRecommendedSellingPrice(BigDecimal recommendedSellingPrice) {
        this.recommendedSellingPrice = recommendedSellingPrice;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Integer getSimilarListingsCount() {
        return similarListingsCount;
    }

    public void setSimilarListingsCount(Integer similarListingsCount) {
        this.similarListingsCount = similarListingsCount;
    }

    public BigDecimal getMileageAdjustment() {
        return mileageAdjustment;
    }

    public void setMileageAdjustment(BigDecimal mileageAdjustment) {
        this.mileageAdjustment = mileageAdjustment;
    }

    public BigDecimal getAgeAdjustment() {
        return ageAdjustment;
    }

    public void setAgeAdjustment(BigDecimal ageAdjustment) {
        this.ageAdjustment = ageAdjustment;
    }

    public BigDecimal getConditionAdjustment() {
        return conditionAdjustment;
    }

    public void setConditionAdjustment(BigDecimal conditionAdjustment) {
        this.conditionAdjustment = conditionAdjustment;
    }

    public BigDecimal getAccidentAdjustment() {
        return accidentAdjustment;
    }

    public void setAccidentAdjustment(BigDecimal accidentAdjustment) {
        this.accidentAdjustment = accidentAdjustment;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
