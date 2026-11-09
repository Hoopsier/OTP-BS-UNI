package application;

/** Temperature converter class. Could be static. */
public class TemperatureConverter {
  float fahrenheitToCelsius(float fahrenheit) {
    return (fahrenheit - 32) * 5 / 9;
  }

  float celsiusToFahrenheit(float celsius) {
    return (celsius * 9 / 5) + 32;
  }

  boolean isExtremeTemperature(float celsius) {
    return celsius > 50 || celsius < -40;
  }
}
