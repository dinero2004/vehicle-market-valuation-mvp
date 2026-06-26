import unittest

from autoscout_scraper import MARKET_PRESETS, parse_listings


class ParserTest(unittest.TestCase):
    def test_parse_autoscout_listing_card(self):
        html = """
        <article data-testid="list-item" data-guid="abc-123" data-price="27690"
          data-make="tesla" data-model="model 3" data-mileage="15676"
          data-first-registration="03-2022" data-seller-type="d"
          data-listing-country="d" data-listing-zip-code="35463">
          <h2>
            <span>Tesla Model 3</span>
            <span class="subtitle">Standard Range Plus</span>
          </h2>
          <span data-testid="regular-price">€ 27,690</span>
          <div data-testid="VehicleDetails-calendar">03/2022</div>
          <div data-testid="VehicleDetails-mileage_odometer">15,676 km</div>
          <div data-testid="VehicleDetails-gas_pump">Electric</div>
          <div data-testid="VehicleDetails-speedometer">208 kW (283 hp)</div>
          <span>VIN: WBA8E1C55HK479123</span>
          <span>Typengenehmigung: 1TA123</span>
          <a href="/offers/tesla-model-3-abc-123">Details</a>
          <img data-testid="list-item-image" src="https://example.test/car.webp" />
        </article>
        """

        listings = parse_listings(html, "https://www.autoscout24.com")

        self.assertEqual(len(listings), 1)
        listing = listings[0]
        self.assertEqual(listing.listing_id, "abc-123")
        self.assertEqual(listing.price_amount, 27690)
        self.assertEqual(listing.currency, "€")
        self.assertEqual(listing.make, "tesla")
        self.assertEqual(listing.model, "model 3")
        self.assertEqual(listing.mileage_km, 15676)
        self.assertEqual(listing.fuel_type, "Electric")
        self.assertEqual(listing.vin_number, "WBA8E1C55HK479123")
        self.assertEqual(listing.type_approval, "1TA123")
        self.assertEqual(listing.url, "https://www.autoscout24.com/offers/tesla-model-3-abc-123")

    def test_bmw_m4_ch_market_preset_exists(self):
        self.assertEqual(MARKET_PRESETS["bmw-m4-ch"], "https://www.autoscout24.ch/de/s/mo-m4/mk-bmw")


if __name__ == "__main__":
    unittest.main()
