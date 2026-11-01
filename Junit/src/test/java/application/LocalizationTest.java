package application;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class LocalizationTest {

  private static final List<String> UI_KEYS = List.of(
      "title",
      "temperature",
      "input",
      "result",
      "from",
      "to",
      "selectFault",
      "convert-btn",
      "numberFault",
      "celcius",
      "fahrenheit");

  @ParameterizedTest(name = "{0}_{1} bundle is complete")
  @CsvSource({
      "en, US",
      "fi, FI",
      "ja, JP",
      "sv, SE",
      "ar, AE"
  })
  public void shouldProvideEveryUiKeyForEachLocale(String language, String country) {
    ResourceBundle bundle = ResourceBundle.getBundle("Messages", Locale.of(language, country));

    assertEquals(language, bundle.getLocale().getLanguage());
    assertEquals(country, bundle.getLocale().getCountry());

    for (String key : UI_KEYS) {
      assertFalse(
          bundle.getString(key).isBlank(),
          () -> key + " is blank for " + language + "_" + country);
    }
  }
}
