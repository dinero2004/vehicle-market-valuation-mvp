import {
  AlertTriangle,
  Calculator,
  Car,
  CheckCircle2,
  Database,
  ExternalLink,
  Gauge,
  MapPin,
  RefreshCw,
  Search,
  TrendingDown,
  TrendingUp
} from "lucide-react";
import type { ReactNode } from "react";
import { useEffect, useMemo, useState } from "react";

type Vehicle = {
  id: number;
  brand: string;
  model: string;
  version: string;
  year: number;
  mileage: number;
  fuelType: string;
  transmission: string;
  powerKw: number;
  condition: string;
  accidentHistory: string;
  equipment: string;
  location: string;
  purchasePrice: number;
  listedPrice: number;
};

type MarketListing = {
  id: number;
  source: string;
  brand: string;
  model: string;
  version: string;
  year: number;
  mileage: number;
  price: number;
  location: string;
  listingDate: string;
  url: string;
};

type StoredValuation = {
  id: number;
  vehicleId: number;
  valuationDate: string;
  averageMarketPrice: number | null;
  estimatedMarketValue: number | null;
  catalogPrice: number | null;
  catalogDepreciationValue: number | null;
  marketCatalogGap: number | null;
  dealerPurchasePriceMin: number | null;
  dealerPurchasePriceMax: number | null;
  tradeInValue: number | null;
  recommendedSellingPrice: number | null;
  confidenceScore: number;
  similarListingsCount: number;
  mileageAdjustment: number | null;
  ageAdjustment: number | null;
  conditionAdjustment: number | null;
  accidentAdjustment: number | null;
  explanation: string;
};

type ManualValuation = {
  evaluationPossible: boolean;
  message: string;
  valuationDate: string | null;
  averageMarketPrice: number | null;
  estimatedMarketValue: number | null;
  catalogPrice: number | null;
  catalogDepreciationValue: number | null;
  marketCatalogGap: number | null;
  dealerPurchasePriceMin: number | null;
  dealerPurchasePriceMax: number | null;
  tradeInValue: number | null;
  recommendedSellingPrice: number | null;
  confidenceScore: number | null;
  similarListingsCount: number | null;
  mileageAdjustment: number | null;
  ageAdjustment: number | null;
  conditionAdjustment: number | null;
  accidentAdjustment: number | null;
  explanation: string | null;
  similarListings: MarketListing[];
};

type ValuationView = {
  evaluationPossible: boolean;
  message: string;
  valuationDate: string | null;
  averageMarketPrice: number | null;
  estimatedMarketValue: number | null;
  catalogPrice: number | null;
  catalogDepreciationValue: number | null;
  marketCatalogGap: number | null;
  dealerPurchasePriceMin: number | null;
  dealerPurchasePriceMax: number | null;
  tradeInValue: number | null;
  recommendedSellingPrice: number | null;
  confidenceScore: number | null;
  similarListingsCount: number;
  mileageAdjustment: number | null;
  ageAdjustment: number | null;
  conditionAdjustment: number | null;
  accidentAdjustment: number | null;
  explanation: string | null;
};

type ManualVehicleForm = {
  brand: string;
  model: string;
  version: string;
  year: string;
  mileage: string;
  fuelType: string;
  transmission: string;
  powerKw: string;
  condition: string;
  accidentHistory: string;
  equipment: string;
  location: string;
  purchasePrice: string;
  listedPrice: string;
};

type AppMode = "inventory" | "manual";
type LoadState = "idle" | "loading" | "ready" | "error";

const configuredApiBaseUrl = import.meta.env.VITE_API_BASE_URL;
const fallbackApiBaseUrl = typeof window !== "undefined"
  && window.location.hostname !== "localhost"
  && window.location.hostname !== "127.0.0.1"
  ? "https://vehicle-market-valuation-backend.onrender.com"
  : "";
const API_BASE_URL = (configuredApiBaseUrl ?? fallbackApiBaseUrl).replace(/\/$/, "");

const currencyFormatter = new Intl.NumberFormat("de-CH", {
  style: "currency",
  currency: "CHF",
  maximumFractionDigits: 0
});

const numberFormatter = new Intl.NumberFormat("de-CH");

const defaultManualForm: ManualVehicleForm = {
  brand: "",
  model: "",
  version: "",
  year: "",
  mileage: "",
  fuelType: "Petrol",
  transmission: "Automatic",
  powerKw: "",
  condition: "Good",
  accidentHistory: "None",
  equipment: "",
  location: "Zurich",
  purchasePrice: "",
  listedPrice: ""
};

async function apiRequest<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Request failed with status ${response.status}`);
  }

  return response.json() as Promise<T>;
}

function formatMoney(value?: number | null) {
  return value == null ? "-" : currencyFormatter.format(value);
}

function formatMileage(value?: number) {
  return `${numberFormatter.format(value ?? 0)} km`;
}

function formatRange(min?: number | null, max?: number | null) {
  if (min == null || max == null) {
    return "-";
  }
  return `${formatMoney(min)} - ${formatMoney(max)}`;
}

function formatDate(value?: string | null) {
  if (!value) {
    return "-";
  }
  return new Intl.DateTimeFormat("de-CH", {
    year: "numeric",
    month: "short",
    day: "2-digit"
  }).format(new Date(value));
}

function toInventoryValuation(value: StoredValuation): ValuationView {
  return {
    evaluationPossible: true,
    message: "",
    valuationDate: value.valuationDate,
    averageMarketPrice: value.averageMarketPrice,
    estimatedMarketValue: value.estimatedMarketValue,
    catalogPrice: value.catalogPrice,
    catalogDepreciationValue: value.catalogDepreciationValue,
    marketCatalogGap: value.marketCatalogGap,
    dealerPurchasePriceMin: value.dealerPurchasePriceMin,
    dealerPurchasePriceMax: value.dealerPurchasePriceMax,
    tradeInValue: value.tradeInValue,
    recommendedSellingPrice: value.recommendedSellingPrice,
    confidenceScore: value.confidenceScore,
    similarListingsCount: value.similarListingsCount,
    mileageAdjustment: value.mileageAdjustment,
    ageAdjustment: value.ageAdjustment,
    conditionAdjustment: value.conditionAdjustment,
    accidentAdjustment: value.accidentAdjustment,
    explanation: value.explanation
  };
}

function toManualValuation(value: ManualValuation): ValuationView {
  return {
    evaluationPossible: value.evaluationPossible,
    message: value.message,
    valuationDate: value.valuationDate,
    averageMarketPrice: value.averageMarketPrice,
    estimatedMarketValue: value.estimatedMarketValue,
    catalogPrice: value.catalogPrice,
    catalogDepreciationValue: value.catalogDepreciationValue,
    marketCatalogGap: value.marketCatalogGap,
    dealerPurchasePriceMin: value.dealerPurchasePriceMin,
    dealerPurchasePriceMax: value.dealerPurchasePriceMax,
    tradeInValue: value.tradeInValue,
    recommendedSellingPrice: value.recommendedSellingPrice,
    confidenceScore: value.confidenceScore,
    similarListingsCount: value.similarListingsCount ?? value.similarListings.length,
    mileageAdjustment: value.mileageAdjustment,
    ageAdjustment: value.ageAdjustment,
    conditionAdjustment: value.conditionAdjustment,
    accidentAdjustment: value.accidentAdjustment,
    explanation: value.explanation
  };
}

function parseOptionalNumber(value: string) {
  return value.trim() === "" ? undefined : Number(value);
}

export function App() {
  const [mode, setMode] = useState<AppMode>("inventory");
  const [vehicles, setVehicles] = useState<Vehicle[]>([]);
  const [selectedVehicleId, setSelectedVehicleId] = useState<number | null>(null);
  const [inventoryValuation, setInventoryValuation] = useState<ValuationView | null>(null);
  const [inventoryListings, setInventoryListings] = useState<MarketListing[]>([]);
  const [manualValuation, setManualValuation] = useState<ValuationView | null>(null);
  const [manualListings, setManualListings] = useState<MarketListing[]>([]);
  const [manualForm, setManualForm] = useState<ManualVehicleForm>(defaultManualForm);
  const [yearRange, setYearRange] = useState(2);
  const [mileageRange, setMileageRange] = useState(30_000);
  const [status, setStatus] = useState<LoadState>("idle");
  const [message, setMessage] = useState("");
  const [calculating, setCalculating] = useState(false);

  const selectedVehicle = useMemo(
    () => vehicles.find((vehicle) => vehicle.id === selectedVehicleId) ?? null,
    [vehicles, selectedVehicleId]
  );

  const activeValuation = mode === "inventory" ? inventoryValuation : manualValuation;
  const activeListings = mode === "inventory" ? inventoryListings : manualListings;
  const averageComparablePrice = useMemo(() => {
    if (activeListings.length === 0) {
      return 0;
    }
    const total = activeListings.reduce((sum, listing) => sum + listing.price, 0);
    return Math.round(total / activeListings.length);
  }, [activeListings]);

  useEffect(() => {
    loadVehicles();
  }, []);

  useEffect(() => {
    if (selectedVehicleId) {
      void loadVehicleContext(selectedVehicleId);
    }
  }, [selectedVehicleId]);

  async function loadVehicles() {
    setStatus("loading");
    setMessage("");

    try {
      const vehicleData = await apiRequest<Vehicle[]>("/api/vehicles");
      setVehicles(vehicleData);
      setSelectedVehicleId((current) => current ?? vehicleData[0]?.id ?? null);
      setStatus("ready");
    } catch (error) {
      setStatus("error");
      setMessage(error instanceof Error ? error.message : "Backend unavailable");
    }
  }

  async function loadVehicleContext(vehicleId: number) {
    setMessage("");

    try {
      const listings = await apiRequest<MarketListing[]>(`/api/market-listings/similar/${vehicleId}`);
      setInventoryListings(listings);
    } catch (error) {
      setInventoryListings([]);
      setMessage(error instanceof Error ? error.message : "Could not load similar listings");
    }

    try {
      const latestValuation = await apiRequest<StoredValuation>(`/api/valuations/${vehicleId}`);
      setInventoryValuation(toInventoryValuation(latestValuation));
    } catch {
      setInventoryValuation(null);
    }
  }

  async function calculateInventoryValuation() {
    if (!selectedVehicleId) {
      return;
    }

    setCalculating(true);
    setMessage("");

    try {
      const result = await apiRequest<StoredValuation>("/api/valuations/calculate", {
        method: "POST",
        body: JSON.stringify({
          vehicleId: selectedVehicleId,
          yearRange,
          mileageRange
        })
      });
      setInventoryValuation(toInventoryValuation(result));
      await loadVehicleContext(selectedVehicleId);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Could not calculate valuation");
    } finally {
      setCalculating(false);
    }
  }

  async function calculateManualValuation() {
    setCalculating(true);
    setMessage("");

    try {
      const result = await apiRequest<ManualValuation>("/api/valuations/manual", {
        method: "POST",
        body: JSON.stringify({
          brand: manualForm.brand,
          model: manualForm.model,
          version: manualForm.version,
          year: Number(manualForm.year),
          mileage: Number(manualForm.mileage),
          fuelType: manualForm.fuelType,
          transmission: manualForm.transmission,
          powerKw: parseOptionalNumber(manualForm.powerKw),
          condition: manualForm.condition,
          accidentHistory: manualForm.accidentHistory,
          equipment: manualForm.equipment,
          location: manualForm.location,
          purchasePrice: parseOptionalNumber(manualForm.purchasePrice),
          listedPrice: parseOptionalNumber(manualForm.listedPrice),
          yearRange,
          mileageRange
        })
      });

      setManualValuation(toManualValuation(result));
      setManualListings(result.similarListings);
    } catch (error) {
      setManualValuation(null);
      setManualListings([]);
      setMessage(error instanceof Error ? error.message : "Could not evaluate vehicle");
    } finally {
      setCalculating(false);
    }
  }

  function updateManualField<K extends keyof ManualVehicleForm>(field: K, value: ManualVehicleForm[K]) {
    setManualForm((current) => ({
      ...current,
      [field]: value
    }));
  }

  return (
    <main className="app-shell">
      <header className="topbar">
        <div>
          <div className="eyebrow">
            <Database size={15} />
            Market Value Estimation
          </div>
          <h1>Vehicle Valuation</h1>
        </div>
        <button className="secondary-button" type="button" onClick={loadVehicles}>
          <RefreshCw size={17} />
          Refresh
        </button>
      </header>

      {status === "error" && (
        <section className="notice error">
          <AlertTriangle size={20} />
          <div>
            <strong>Backend connection failed</strong>
            <span>{message}</span>
          </div>
        </section>
      )}

      {message && status !== "error" && (
        <section className="notice">
          <AlertTriangle size={20} />
          <span>{message}</span>
        </section>
      )}

      {activeValuation && !activeValuation.evaluationPossible && (
        <section className="notice">
          <AlertTriangle size={20} />
          <div>
            <strong>Vehicle could not be evaluated</strong>
            <span>{activeValuation.message}</span>
          </div>
        </section>
      )}

      <section className="workspace-grid">
        <aside className="panel vehicle-panel">
          <div className="panel-heading">
            <h2>{mode === "inventory" ? "Inventory Vehicle" : "Manual Vehicle"}</h2>
            <span>{mode === "inventory" ? `${vehicles.length} cars` : "Ad hoc input"}</span>
          </div>

          <div className="mode-switch" role="tablist" aria-label="Valuation mode">
            <button
              className={`mode-button ${mode === "inventory" ? "active" : ""}`}
              type="button"
              onClick={() => setMode("inventory")}
            >
              Inventory
            </button>
            <button
              className={`mode-button ${mode === "manual" ? "active" : ""}`}
              type="button"
              onClick={() => setMode("manual")}
            >
              Manual
            </button>
          </div>

          {mode === "inventory" ? (
            <>
              <label className="field-label" htmlFor="vehicle-select">
                Vehicle
              </label>
              <select
                id="vehicle-select"
                value={selectedVehicleId ?? ""}
                onChange={(event) => setSelectedVehicleId(Number(event.target.value))}
                disabled={vehicles.length === 0}
              >
                {vehicles.map((vehicle) => (
                  <option key={vehicle.id} value={vehicle.id}>
                    #{vehicle.id} {vehicle.brand} {vehicle.model} {vehicle.version}
                  </option>
                ))}
              </select>

              {selectedVehicle && <VehicleSummary vehicle={selectedVehicle} />}
            </>
          ) : (
            <div className="manual-form">
              <div className="form-grid">
                <label>
                  <span>Brand</span>
                  <input
                    required
                    value={manualForm.brand}
                    onChange={(event) => updateManualField("brand", event.target.value)}
                    placeholder="BMW"
                  />
                </label>
                <label>
                  <span>Model</span>
                  <input
                    required
                    value={manualForm.model}
                    onChange={(event) => updateManualField("model", event.target.value)}
                    placeholder="M4"
                  />
                </label>
                <label>
                  <span>Version</span>
                  <input
                    value={manualForm.version}
                    onChange={(event) => updateManualField("version", event.target.value)}
                    placeholder="Competition Coupe"
                  />
                </label>
                <label>
                  <span>Year</span>
                  <input
                    type="number"
                    min={1900}
                    required
                    value={manualForm.year}
                    onChange={(event) => updateManualField("year", event.target.value)}
                    placeholder="2021"
                  />
                </label>
                <label>
                  <span>Mileage</span>
                  <input
                    type="number"
                    min={0}
                    required
                    value={manualForm.mileage}
                    onChange={(event) => updateManualField("mileage", event.target.value)}
                    placeholder="42000"
                  />
                </label>
                <label>
                  <span>Power kW</span>
                  <input
                    type="number"
                    min={0}
                    value={manualForm.powerKw}
                    onChange={(event) => updateManualField("powerKw", event.target.value)}
                    placeholder="375"
                  />
                </label>
                <label>
                  <span>Fuel type</span>
                  <input
                    value={manualForm.fuelType}
                    onChange={(event) => updateManualField("fuelType", event.target.value)}
                  />
                </label>
                <label>
                  <span>Transmission</span>
                  <input
                    value={manualForm.transmission}
                    onChange={(event) => updateManualField("transmission", event.target.value)}
                  />
                </label>
                <label>
                  <span>Condition</span>
                  <select
                    value={manualForm.condition}
                    onChange={(event) => updateManualField("condition", event.target.value)}
                  >
                    <option>Excellent</option>
                    <option>Good</option>
                    <option>Fair</option>
                    <option>Poor</option>
                  </select>
                </label>
                <label>
                  <span>Accident history</span>
                  <select
                    value={manualForm.accidentHistory}
                    onChange={(event) => updateManualField("accidentHistory", event.target.value)}
                  >
                    <option>None</option>
                    <option>Minor repaired</option>
                    <option>Major repaired</option>
                    <option>Unknown</option>
                  </select>
                </label>
                <label>
                  <span>Location</span>
                  <input
                    value={manualForm.location}
                    onChange={(event) => updateManualField("location", event.target.value)}
                  />
                </label>
                <label>
                  <span>Purchase price</span>
                  <input
                    type="number"
                    min={0}
                    step="100"
                    value={manualForm.purchasePrice}
                    onChange={(event) => updateManualField("purchasePrice", event.target.value)}
                    placeholder="62500"
                  />
                </label>
                <label className="form-grid-wide">
                  <span>Equipment</span>
                  <input
                    value={manualForm.equipment}
                    onChange={(event) => updateManualField("equipment", event.target.value)}
                    placeholder="Carbon roof, adaptive suspension"
                  />
                </label>
              </div>
            </div>
          )}

          <div className="range-grid">
            <label>
              <span>Year range</span>
              <input
                type="number"
                min={0}
                max={8}
                value={yearRange}
                onChange={(event) => setYearRange(Number(event.target.value))}
              />
            </label>
            <label>
              <span>Mileage range</span>
              <input
                type="number"
                min={0}
                step={5000}
                value={mileageRange}
                onChange={(event) => setMileageRange(Number(event.target.value))}
              />
            </label>
          </div>

          <button
            className="primary-button"
            type="button"
            onClick={mode === "inventory" ? calculateInventoryValuation : calculateManualValuation}
            disabled={
              calculating ||
              (mode === "inventory" && !selectedVehicleId) ||
              (mode === "manual" && (!manualForm.brand || !manualForm.model || !manualForm.year || !manualForm.mileage))
            }
          >
            <Calculator size={18} />
            {calculating ? "Calculating" : mode === "inventory" ? "Calculate stored vehicle" : "Evaluate vehicle"}
          </button>
        </aside>

        <section className="main-column">
          <section className="result-grid">
            <MetricCard
              icon={<TrendingUp size={19} />}
              label="Live market value"
              value={activeValuation ? formatMoney(activeValuation.estimatedMarketValue) : "-"}
              accent="green"
            />
            <MetricCard
              icon={<Database size={19} />}
              label="Catalog value"
              value={activeValuation ? formatMoney(activeValuation.catalogDepreciationValue) : "-"}
              accent="amber"
            />
            <MetricCard
              icon={<TrendingUp size={19} />}
              label="Market gap"
              value={activeValuation ? formatMoney(activeValuation.marketCatalogGap) : "-"}
              accent="blue"
            />
            <MetricCard
              icon={<TrendingDown size={19} />}
              label="Dealer buy range"
              value={activeValuation ? formatRange(activeValuation.dealerPurchasePriceMin, activeValuation.dealerPurchasePriceMax) : "-"}
              accent="slate"
            />
          </section>

          <section className="result-grid compact">
            <MetricCard
              icon={<Car size={19} />}
              label="Recommended selling"
              value={activeValuation ? formatMoney(activeValuation.recommendedSellingPrice) : "-"}
              accent="green"
            />
            <MetricCard
              icon={<TrendingDown size={19} />}
              label="Legacy trade-in"
              value={activeValuation ? formatMoney(activeValuation.tradeInValue) : "-"}
              accent="amber"
            />
            <MetricCard
              icon={<Database size={19} />}
              label="Catalog price"
              value={activeValuation ? formatMoney(activeValuation.catalogPrice) : "-"}
              accent="blue"
            />
            <MetricCard
              icon={<CheckCircle2 size={19} />}
              label="Confidence"
              value={activeValuation?.confidenceScore != null ? `${Math.round(activeValuation.confidenceScore * 100)}%` : "-"}
              accent="slate"
            />
          </section>

          <section className="panel valuation-panel">
            <div className="panel-heading">
              <h2>Valuation Breakdown</h2>
              <span>{activeValuation ? formatDate(activeValuation.valuationDate) : "No valuation"}</span>
            </div>

            {activeValuation ? (
              activeValuation.evaluationPossible ? (
                <>
                  <div className="breakdown-grid">
                    <Adjustment label="Average market" value={activeValuation.averageMarketPrice} />
                    <Adjustment label="Catalog depreciation" value={activeValuation.catalogDepreciationValue} />
                    <Adjustment label="Market gap" value={activeValuation.marketCatalogGap} />
                    <Adjustment label="Mileage" value={activeValuation.mileageAdjustment} />
                    <Adjustment label="Age" value={activeValuation.ageAdjustment} />
                    <Adjustment label="Condition" value={activeValuation.conditionAdjustment} />
                    <Adjustment label="Accident" value={activeValuation.accidentAdjustment} />
                  </div>
                  <p className="explanation">{activeValuation.explanation}</p>
                </>
              ) : (
                <div className="empty-state">
                  <AlertTriangle size={28} />
                  <span>{activeValuation.message}</span>
                </div>
              )
            ) : (
              <div className="empty-state">
                <Calculator size={28} />
                <span>{mode === "inventory" ? "No stored valuation for this vehicle." : "Enter a vehicle profile and run an evaluation."}</span>
              </div>
            )}
          </section>

          <section className="panel">
            <div className="panel-heading">
              <h2>Comparable Listings</h2>
              <span>
                {activeListings.length} matches · avg {formatMoney(averageComparablePrice)}
              </span>
            </div>
            <ComparableTable listings={activeListings} />
          </section>
        </section>
      </section>
    </main>
  );
}

function VehicleSummary({ vehicle }: { vehicle: Vehicle }) {
  return (
    <div className="vehicle-summary">
      <div className="vehicle-title">
        <Car size={22} />
        <div>
          <strong>
            {vehicle.brand} {vehicle.model}
          </strong>
          <span>{vehicle.version}</span>
        </div>
      </div>

      <div className="detail-grid">
        <Detail icon={<Gauge size={16} />} label="Mileage" value={formatMileage(vehicle.mileage)} />
        <Detail icon={<Search size={16} />} label="Year" value={String(vehicle.year)} />
        <Detail icon={<MapPin size={16} />} label="Location" value={vehicle.location} />
        <Detail icon={<TrendingUp size={16} />} label="Listed" value={formatMoney(vehicle.listedPrice)} />
      </div>

      <div className="spec-list">
        <span>{vehicle.fuelType}</span>
        <span>{vehicle.transmission}</span>
        <span>{vehicle.powerKw} kW</span>
        <span>{vehicle.condition}</span>
        <span>{vehicle.accidentHistory}</span>
      </div>
    </div>
  );
}

function Detail({ icon, label, value }: { icon: ReactNode; label: string; value: string }) {
  return (
    <div className="detail">
      {icon}
      <div>
        <span>{label}</span>
        <strong>{value}</strong>
      </div>
    </div>
  );
}

function MetricCard({
  icon,
  label,
  value,
  accent
}: {
  icon: ReactNode;
  label: string;
  value: string;
  accent: "green" | "amber" | "blue" | "slate";
}) {
  return (
    <article className={`metric metric-${accent}`}>
      <div className="metric-icon">{icon}</div>
      <span>{label}</span>
      <strong>{value}</strong>
    </article>
  );
}

function Adjustment({ label, value }: { label: string; value: number | null }) {
  const numericValue = value ?? 0;
  const direction = numericValue > 0 ? "positive" : numericValue < 0 ? "negative" : "neutral";
  return (
    <div className={`adjustment ${direction}`}>
      <span>{label}</span>
      <strong>{formatMoney(value)}</strong>
    </div>
  );
}

function ComparableTable({ listings }: { listings: MarketListing[] }) {
  if (listings.length === 0) {
    return (
      <div className="empty-state">
        <Search size={28} />
        <span>No comparable listings found.</span>
      </div>
    );
  }

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Vehicle</th>
            <th>Year</th>
            <th>Mileage</th>
            <th>Price</th>
            <th>Location</th>
            <th>Date</th>
            <th aria-label="Listing link" />
          </tr>
        </thead>
        <tbody>
          {listings.map((listing) => (
            <tr key={listing.id}>
              <td>
                <strong>
                  {listing.brand} {listing.model}
                </strong>
                <span>{listing.version}</span>
              </td>
              <td>{listing.year}</td>
              <td>{formatMileage(listing.mileage)}</td>
              <td>{formatMoney(listing.price)}</td>
              <td>{listing.location}</td>
              <td>{formatDate(listing.listingDate)}</td>
              <td>
                <a href={listing.url} target="_blank" rel="noreferrer" title="Open listing">
                  <ExternalLink size={16} />
                </a>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
