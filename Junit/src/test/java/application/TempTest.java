package application;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TempTest {

  private final TemperatureConverter converter = new TemperatureConverter();

  @Nested
  class CelsiusToFahrenheit {

    @Test
    public void shouldConvertFreezingPoint() {
      assertEquals(32, converter.celToFahr(0), 0.001);
    }

    @Test
    public void shouldConvertBoilingPoint() {
      assertEquals(212.0, converter.celToFahr(100), 0.001);
    }

    @Test
    public void shouldConvertNegative() {
      assertEquals(-40, converter.celToFahr(-40), 0.001);
    }

    @ParameterizedTest(name = "{0} C = {1} F")
    @CsvSource({
        "-40, -40",
        "-10, 14",
        "0, 32",
        "30, 86",
        "37, 98.6",
        "100, 212"
    })
    public void shouldConvertKnownValues(float celsius, float fahrenheit) {
      assertEquals(fahrenheit, converter.celToFahr(celsius), 0.001);
    }
  }

  @Nested
  class FahrenheitToCelsius {

    @Test
    public void shouldConvertFreezingPoint() {
      // I'm just gonna assume that delta is leeway for lack of float precision
      assertEquals(0, converter.fahrToCel(32), 0.001);
    }

    @Test
    public void shouldConvertBoilingPoint() {
      assertEquals(100, converter.fahrToCel(212), 0.001);
    }

    @Test
    public void shouldConvertNegative() {
      assertEquals(-40, converter.fahrToCel(-40), 0.001);
    }

    @ParameterizedTest(name = "{0} F = {1} C")
    @CsvSource({
        "-40, -40",
        "0, -17.7778",
        "32, 0",
        "50, 10",
        "98.6, 37",
        "212, 100"
    })
    public void shouldConvertKnownValues(float fahrenheit, float celsius) {
      assertEquals(celsius, converter.fahrToCel(fahrenheit), 0.001);
    }
  }

  @Nested
  class RoundTrip {

    @ParameterizedTest(name = "{0} C survives a round trip")
    @ValueSource(floats = {-100f, -40f, 0f, 21.5f, 100f})
    public void shouldSurviveCelsiusRoundTrip(float celsius) {
      float fahrenheit = converter.celToFahr(celsius);

      assertEquals(celsius, converter.fahrToCel(fahrenheit), 0.001);
    }

    @ParameterizedTest(name = "{0} F survives a round trip")
    @ValueSource(floats = {-40f, 0f, 32f, 77f, 212f})
    public void shouldSurviveFahrenheitRoundTrip(float fahrenheit) {
      float celsius = converter.fahrToCel(fahrenheit);

      assertEquals(fahrenheit, converter.celToFahr(celsius), 0.001);
    }
  }

  @Nested
  class Extremes {

    @Test
    public void shouldBeExtreme() {
      assertTrue(converter.isExtremeTemperature(-40.00001f)); // Any more is off presicion and points to equal or more
                                                              // positive than -40
    }

    @Test
    public void shouldNotBeExtreme() {
      assertFalse(converter.isExtremeTemperature(-40));
    }

    @Test
    public void shouldNotBeHotAtUpperBoundary() {
      assertFalse(converter.isExtremeTemperature(50));
    }

    @Test
    public void shouldBeHot() {
      assertTrue(converter.isExtremeTemperature(50.001f));
    }

    @Test
    public void shouldNotBeExtremeAtComfortableTemperature() {
      assertFalse(converter.isExtremeTemperature(21.5f));
    }

    @ParameterizedTest(name = "{0} C is extreme")
    @ValueSource(floats = {-100f, -40.5f, 50.5f, 100f})
    public void shouldTreatValuesOutsideLimitsAsExtreme(float celsius) {
      assertTrue(converter.isExtremeTemperature(celsius));
    }

    @ParameterizedTest(name = "{0} C is not extreme")
    @ValueSource(floats = {-40f, -39.9f, 0f, 49.9f, 50f})
    public void shouldTreatValuesInsideLimitsAsNotExtreme(float celsius) {
      assertFalse(converter.isExtremeTemperature(celsius));
    }
  }
}
