package application;

public class TemperatureType {

  private int id;
  private String name;
  private char symbol;

  public TemperatureType(int id, String locale) {
    this.id = id;
    name = TemperatureLocaleDAO.getUnit(id, locale);
    symbol = TemperatureLocaleDAO.getSymbol(id, locale);
  }

  public TemperatureType(String name, char symbol) {
    this.name = name;
    this.symbol = symbol;
  }

  public void setLocale(String locale) {
    name = TemperatureLocaleDAO.getUnit(id, locale);
    symbol = TemperatureLocaleDAO.getSymbol(id, locale);
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public char getSymbol() {
    return symbol;
  }

  @Override
  public String toString() {
    return name + " (" + symbol + ")";
  }
}
