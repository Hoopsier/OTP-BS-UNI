package application;

/** Temperature Type for easily interpretable Temperature formats. */
public class TemperatureType {

  private int id;
  private String name;
  private char symbol;

  /** Simple constructor with id and locale only. */
  public TemperatureType(int id, String locale) {
    this.id = id;
    name = TemperatureLocaleDataAccessObject.getUnit(id, locale);
    symbol = TemperatureLocaleDataAccessObject.getSymbol(id, locale);
  }

  /** Complex constructor with name and symbol. (Avoid using). */
  public TemperatureType(String name, char symbol) {
    this.name = name;
    this.symbol = symbol;
  }

  /** Used to update the name and symbol during runtime. */
  public void setLocale(String locale) {
    name = TemperatureLocaleDataAccessObject.getUnit(id, locale);
    symbol = TemperatureLocaleDataAccessObject.getSymbol(id, locale);
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
