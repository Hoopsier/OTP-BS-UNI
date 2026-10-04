package application;

public class TemperatureType {

  private int id;
  private String name;
  private String symbol;

  public TemperatureType(int id, String name, String symbol) {
    this.id = id;
    this.name = name;
    this.symbol = symbol;
  }

  public TemperatureType(String name, String symbol) {
    this.name = name;
    this.symbol = symbol;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getSymbol() {
    return symbol;
  }

  @Override
  public String toString() {
    return name + " (" + symbol + ")";
  }
}
