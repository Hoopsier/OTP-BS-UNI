package application;

public class TemperatureConverter {
  float fahrToCel(float fahrenheit) {
    return (fahrenheit - 32) * 5 / 9;
  }

  float celToFahr(float celcius) {
    return (celcius * 9 / 5) + 32;
  }

  boolean isExtremeTemperature(float celcius) {
    return celcius > 50 || celcius < -40;
  }
}
