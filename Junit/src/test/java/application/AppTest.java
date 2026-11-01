package application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {

  private final App app = new App();

  @ParameterizedTest(name = "{0} {1} -> {3} {2}")
  @CsvSource({
      "0, C, F, 32",
      "100, C, F, 212",
      "-40, C, F, -40",
      "32, F, C, 0",
      "212, F, C, 100",
      "-40, F, C, -40",
      "25, C, C, 25",
      "77, F, F, 77"
  })
  public void shouldConvertTemperature(float input, String from, String to, float expected) throws Exception {
    assertEquals(expected, convert(input, from, to), 0.001);
  }

  @Test
  public void shouldReturnInputForUnsupportedUnitPair() throws Exception {
    assertEquals(25, convert(25, "C", "K"), 0.001);
  }

  private float convert(float input, String from, String to) throws Exception {
    Method method = App.class.getDeclaredMethod(
        "convertTemperature",
        float.class,
        String.class,
        String.class);
    method.setAccessible(true);

    try {
      return (float) method.invoke(app, input, from, to);
    } catch (InvocationTargetException e) {
      throw new AssertionError("convertTemperature failed", e.getCause());
    }
  }
}
