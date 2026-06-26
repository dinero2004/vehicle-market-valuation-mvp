package com.example.dealership.valuation;

import com.example.dealership.catalog.CatalogPrice;
import com.example.dealership.catalog.CatalogPriceRepository;
import com.example.dealership.catalog.DepreciationProfile;
import com.example.dealership.catalog.DepreciationProfileRepository;
import com.example.dealership.market.VehicleMarketListing;
import com.example.dealership.market.VehicleMarketListingRepository;
import com.example.dealership.valuation.dto.CalculateValuationRequest;
import com.example.dealership.valuation.dto.ManualValuationRequest;
import com.example.dealership.valuation.dto.ManualValuationResponse;
import com.example.dealership.valuation.dto.MarketListingResponse;
import com.example.dealership.valuation.dto.ValuationResponse;
import com.example.dealership.vehicle.Vehicle;
import com.example.dealership.vehicle.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ValuationService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMarketListingRepository marketListingRepository;
    private final VehicleValuationRepository valuationRepository;
    private final ValuationCalculator valuationCalculator;
    private final CatalogPriceRepository catalogPriceRepository;
    private final DepreciationProfileRepository depreciationProfileRepository;

    public ValuationService(
            VehicleRepository vehicleRepository,
            VehicleMarketListingRepository marketListingRepository,
            VehicleValuationRepository valuationRepository,
            ValuationCalculator valuationCalculator,
            CatalogPriceRepository catalogPriceRepository,
            DepreciationProfileRepository depreciationProfileRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.marketListingRepository = marketListingRepository;
        this.valuationRepository = valuationRepository;
        this.valuationCalculator = valuationCalculator;
        this.catalogPriceRepository = catalogPriceRepository;
        this.depreciationProfileRepository = depreciationProfileRepository;
    }

    @Transactional
    public ValuationResponse calculateValuation(CalculateValuationRequest request) {
        Vehicle vehicle = getVehicleOrThrow(request.getVehicleId());
        int yearRange = request.getYearRange() == null ? 2 : request.getYearRange();
        int mileageRange = request.getMileageRange() == null ? 30_000 : request.getMileageRange();
        List<VehicleMarketListing> similarListings = findSimilarListings(vehicle, yearRange, mileageRange);

        if (similarListings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No similar market listings found for this vehicle.");
        }

        CatalogPrice catalogPrice = findCatalogPrice(vehicle);
        DepreciationProfile depreciationProfile = findDepreciationProfile(vehicle);
        ValuationCalculationResult result = valuationCalculator.calculate(
                vehicle,
                similarListings,
                catalogPrice,
                depreciationProfile,
                Year.now().getValue()
        );
        VehicleValuation valuation = new VehicleValuation();
        valuation.setVehicle(vehicle);
        valuation.setValuationDate(LocalDateTime.now());
        valuation.setAverageMarketPrice(result.averageMarketPrice());
        valuation.setEstimatedMarketValue(result.estimatedMarketValue());
        valuation.setCatalogPrice(result.catalogPrice());
        valuation.setCatalogDepreciationValue(result.catalogDepreciationValue());
        valuation.setMarketCatalogGap(result.marketCatalogGap());
        valuation.setDealerPurchasePriceMin(result.dealerPurchasePriceMin());
        valuation.setDealerPurchasePriceMax(result.dealerPurchasePriceMax());
        valuation.setTradeInValue(result.tradeInValue());
        valuation.setRecommendedSellingPrice(result.recommendedSellingPrice());
        valuation.setConfidenceScore(result.confidenceScore());
        valuation.setSimilarListingsCount(result.similarListingsCount());
        valuation.setMileageAdjustment(result.mileageAdjustment());
        valuation.setAgeAdjustment(result.ageAdjustment());
        valuation.setConditionAdjustment(result.conditionAdjustment());
        valuation.setAccidentAdjustment(result.accidentAdjustment());
        valuation.setExplanation(result.explanation());

        return ValuationResponse.fromEntity(valuationRepository.save(valuation));
    }

    @Transactional(readOnly = true)
    public ManualValuationResponse calculateManualValuation(ManualValuationRequest request) {
        Vehicle vehicle = buildVehicleFromManualRequest(request);
        LocalDateTime valuationDate = LocalDateTime.now();
        int yearRange = request.getYearRange() == null ? 2 : request.getYearRange();
        int mileageRange = request.getMileageRange() == null ? 30_000 : request.getMileageRange();
        List<VehicleMarketListing> similarListings = findSimilarListings(vehicle, yearRange, mileageRange);
        List<MarketListingResponse> listingResponses = similarListings.stream()
                .map(MarketListingResponse::fromEntity)
                .toList();

        if (similarListings.isEmpty()) {
            return ManualValuationResponse.notEvaluable(
                    "No similar market listings were found for this vehicle profile.",
                    valuationDate,
                    0,
                    listingResponses
            );
        }

        Optional<CatalogPrice> catalogPrice = lookupCatalogPrice(vehicle);
        if (catalogPrice.isEmpty()) {
            return ManualValuationResponse.notEvaluable(
                    "No catalog price was found for this brand, model, and year combination.",
                    valuationDate,
                    similarListings.size(),
                    listingResponses
            );
        }

        Optional<DepreciationProfile> depreciationProfile = lookupDepreciationProfile(vehicle);
        if (depreciationProfile.isEmpty()) {
            return ManualValuationResponse.notEvaluable(
                    "No depreciation profile was found for this vehicle segment.",
                    valuationDate,
                    similarListings.size(),
                    listingResponses
            );
        }

        ValuationCalculationResult result = valuationCalculator.calculate(
                vehicle,
                similarListings,
                catalogPrice.get(),
                depreciationProfile.get(),
                Year.now().getValue()
        );

        return ManualValuationResponse.success(valuationDate, result, listingResponses);
    }

    @Transactional(readOnly = true)
    public ValuationResponse getLatestValuation(Long vehicleId) {
        return valuationRepository.findFirstByVehicleIdOrderByValuationDateDesc(vehicleId)
                .map(ValuationResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No valuation found for vehicle " + vehicleId));
    }

    @Transactional(readOnly = true)
    public List<MarketListingResponse> getSimilarListings(Long vehicleId) {
        Vehicle vehicle = getVehicleOrThrow(vehicleId);
        return findSimilarListings(vehicle, 2, 30_000).stream()
                .map(MarketListingResponse::fromEntity)
                .toList();
    }

    private Vehicle getVehicleOrThrow(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found: " + vehicleId));
    }

    private List<VehicleMarketListing> findSimilarListings(Vehicle vehicle, int yearRange, int mileageRange) {
        int minYear = vehicle.getManufactureYear() - yearRange;
        int maxYear = vehicle.getManufactureYear() + yearRange;
        int minMileage = Math.max(0, vehicle.getMileage() - mileageRange);
        int maxMileage = vehicle.getMileage() + mileageRange;

        return marketListingRepository.findSimilarListings(
                vehicle.getBrand(),
                vehicle.getModel(),
                minYear,
                maxYear,
                minMileage,
                maxMileage
        );
    }

    private CatalogPrice findCatalogPrice(Vehicle vehicle) {
        return lookupCatalogPrice(vehicle)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No catalog price found for " + vehicle.getBrand() + " " + vehicle.getModel()
                ));
    }

    private DepreciationProfile findDepreciationProfile(Vehicle vehicle) {
        return lookupDepreciationProfile(vehicle)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No depreciation profile found."));
    }

    private Optional<CatalogPrice> lookupCatalogPrice(Vehicle vehicle) {
        return catalogPriceRepository.findBestMatches(
                        vehicle.getBrand(),
                        vehicle.getModel(),
                        vehicle.getVersion() == null ? "" : vehicle.getVersion(),
                        vehicle.getManufactureYear()
                ).stream()
                .findFirst();
    }

    private Optional<DepreciationProfile> lookupDepreciationProfile(Vehicle vehicle) {
        return depreciationProfileRepository.findFirstByBrandIgnoreCaseAndModelIgnoreCase(vehicle.getBrand(), vehicle.getModel())
                .or(() -> depreciationProfileRepository.findFirstByBrandIgnoreCaseAndModelIsNull(vehicle.getBrand()))
                .or(() -> depreciationProfileRepository.findFirstByBrandIsNullAndModelIsNull());
    }

    private Vehicle buildVehicleFromManualRequest(ManualValuationRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(request.getBrand().trim());
        vehicle.setModel(request.getModel().trim());
        vehicle.setVersion(request.getVersion() == null ? null : request.getVersion().trim());
        vehicle.setManufactureYear(request.getYear());
        vehicle.setMileage(request.getMileage());
        vehicle.setFuelType(request.getFuelType());
        vehicle.setTransmission(request.getTransmission());
        vehicle.setPowerKw(request.getPowerKw());
        vehicle.setCondition(request.getCondition());
        vehicle.setAccidentHistory(request.getAccidentHistory());
        vehicle.setEquipment(request.getEquipment());
        vehicle.setLocation(request.getLocation());
        vehicle.setPurchasePrice(request.getPurchasePrice());
        vehicle.setListedPrice(request.getListedPrice());
        return vehicle;
    }
}
