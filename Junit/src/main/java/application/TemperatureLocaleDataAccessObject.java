package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** Temperature Localization's Data Access Object. */
public class TemperatureLocaleDataAccessObject {
  /** Get the symbol of the temperature unit (C or F in english). */
  public static char getSymbol(int type, String locale) {
    String sql =
        """
        SELECT symbol
        FROM temperature_translation
        WHERE lang = ?
        AND temp_id = ?
        """;
    try (Connection connection = DataBaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, locale);
      statement.setInt(2, type);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          String symbol = resultSet.getString("symbol");

          if (symbol != null && !symbol.isEmpty()) {
            return symbol.charAt(0);
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Failed to execute DAO: " + e);
    }
    return type == 1 ? 'C' : 'F';
  }

  /** Get the Unit name of the temperature Unit. (Celsius or Fahrenheit in english). */
  public static String getUnit(int type, String locale) {
    String sql =
        """
        SELECT name
        FROM temperature_translation
        WHERE lang = ?
        AND temp_id = ?
        """;
    try (Connection connection = DataBaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, locale);
      statement.setInt(2, type);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          String symbol = resultSet.getString("name");

          if (symbol != null && !symbol.isEmpty()) {
            return symbol;
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Failed to execute DAO: " + e);
    }
    return type == 1
        ? "Celsius"
        : "Fahrenheit"; // If I actually cared about scaling this, I would check the english
    // column too, but there is no need when there are 3 temperature units
    // commonly used.
  }
}
