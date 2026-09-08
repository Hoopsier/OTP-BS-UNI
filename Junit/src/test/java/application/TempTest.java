package application;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TempTest {

  TemperatureConverter converter = new TemperatureConverter();

  @Nested
  class CTF {
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
  }

  @Nested
  class FTC {
    @Test
    public void shouldConvertFreezingPoint() {
      assertEquals(0, converter.fahrToCel(32), 0.001); // I'm just gonna assume that delta is leeway for lack of float
                                                       // precision
    }

    @Test
    public void shouldConvertBoilingPoint() {
      assertEquals(100, converter.fahrToCel(212), 0.001);
    }

    @Test
    public void shouldConvertNegative() {
      assertEquals(-40, converter.fahrToCel(-40), 0.001);
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
    public void ShouldNotBeHot() {
      assertFalse(converter.isExtremeTemperature(50));
    }

    @Test
    public void ShouldBeHot() {
      assertTrue(converter.isExtremeTemperature(50.001f));
    }
  }
}
