package application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemperatureTypeTest {

  @Test
  public void shouldStoreAllValuesFromFullConstructor() {
    TemperatureType type = new TemperatureType(1, "Celsius", "C");

    assertEquals(1, type.getId());
    assertEquals("Celsius", type.getName());
    assertEquals("C", type.getSymbol());
  }

  @Test
  public void shouldDefaultIdToZeroWhenConstructedWithoutId() {
    TemperatureType type = new TemperatureType("Fahrenheit", "F");

    assertEquals(0, type.getId());
    assertEquals("Fahrenheit", type.getName());
    assertEquals("F", type.getSymbol());
  }

  @Test
  public void shouldFormatToStringWithNameAndSymbol() {
    TemperatureType type = new TemperatureType(2, "Fahrenheit", "F");

    assertEquals("Fahrenheit (F)", type.toString());
  }

  @Test
  public void shouldSupportLocalizedNamesAndSymbols() {
    TemperatureType type = new TemperatureType(3, "摂氏", "C");

    assertEquals("摂氏 (C)", type.toString());
  }
}
