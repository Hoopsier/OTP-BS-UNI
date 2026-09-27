package application;

/**
 * Hello world!
 */
public class App {
  public static void main(String[] args) {
    TemperatureConverter temperatureConverter = new TemperatureConverter();

    System.out.println("15 Fahrenheit to Celcius: " + temperatureConverter.fahrToCel(15));
    System.out.println("15 Celcius to Fahrenheit: " + temperatureConverter.celToFahr(15));
    System.out.println("Is 15 Celcius extreme? " + (temperatureConverter.isExtremeTemperature(15) ? "Yes" : "No"));
  }
}
