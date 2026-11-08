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
      "0, 1, 2, 32",
      "100, 1, 2, 212",
      "-40, 1, 2, -40",
      "32, 2, 1, 0",
      "212, 2, 1, 100",
      "-40, 2, 1, -40",
      "25, 1, 1, 25",
      "77, 2, 2, 77"
  })
  public void shouldConvertTemperature(float input, int from, int to, float expected) throws Exception {
    assertEquals(expected, convert(input, from, to), 0.001);
  }

  @Test
  public void shouldReturnInputForUnsupportedUnitPair() throws Exception {
    assertEquals(25, convert(25, 1, 3), 0.001);
  }

  private float convert(float input, int from, int to) throws Exception {
    Method method = App.class.getDeclaredMethod(
        "convertTemperature",
        float.class,
        int.class,
        int.class);
    method.setAccessible(true);

    try {
      return (float) method.invoke(app, input, from, to);
    } catch (InvocationTargetException e) {
      throw new AssertionError("convertTemperature failed", e.getCause());
    }
  }
}
