package application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class TemperatureTypeTest {

  @Test
  public void shouldStoreNameAndSymbolFromSimpleConstructor() {
    TemperatureType type = new TemperatureType("Fahrenheit", 'F');

    assertEquals(0, type.getId());
    assertEquals("Fahrenheit", type.getName());
    assertEquals('F', type.getSymbol());
  }

  @Test
  public void shouldLoadLocalizedNameAndSymbolForLocale() {
    try (MockedStatic<TemperatureLocaleDataAccessObject> locale =
        mockStatic(TemperatureLocaleDataAccessObject.class)) {
      locale.when(() -> TemperatureLocaleDataAccessObject.getUnit(1, "ja")).thenReturn("セルシウス");
      locale.when(() -> TemperatureLocaleDataAccessObject.getSymbol(1, "ja")).thenReturn('ど');

      TemperatureType type = new TemperatureType(1, "ja");

      assertEquals(1, type.getId());
      assertEquals("セルシウス", type.getName());
      assertEquals('ど', type.getSymbol());
    }
  }

  @Test
  public void shouldReloadNameAndSymbolWhenLocaleChanges() {
    try (MockedStatic<TemperatureLocaleDataAccessObject> locale =
        mockStatic(TemperatureLocaleDataAccessObject.class)) {
      locale.when(() -> TemperatureLocaleDataAccessObject.getUnit(1, "en")).thenReturn("Celsius");
      locale.when(() -> TemperatureLocaleDataAccessObject.getSymbol(1, "en")).thenReturn('C');

      TemperatureType type = new TemperatureType(1, "en");

      locale.when(() -> TemperatureLocaleDataAccessObject.getUnit(1, "ja")).thenReturn("摂氏");
      locale.when(() -> TemperatureLocaleDataAccessObject.getSymbol(1, "ja")).thenReturn('C');

      type.setLocale("ja");

      assertEquals("摂氏", type.getName());
      assertEquals('C', type.getSymbol());
    }
  }

  @Test
  public void shouldFormatToStringWithNameAndSymbol() {
    TemperatureType type = new TemperatureType("Fahrenheit", 'F');

    assertEquals("Fahrenheit (F)", type.toString());
  }

  @Test
  public void shouldSupportLocalizedNamesAndSymbols() {
    TemperatureType type = new TemperatureType("摂氏", 'C');

    assertEquals("摂氏 (C)", type.toString());
  }
}
