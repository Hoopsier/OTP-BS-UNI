package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** TemperatureTypeDataAccessObject. */
public class TemperatureTypeDataAccessObject {

  /** Used to get every Temperature Unit in the database. */
  public List<TemperatureType> getAllUnits(String locale) {

    List<Integer> ids = new ArrayList<>();

    String sql =
        """
        SELECT id, name, symbol
        FROM temperature
        ORDER BY id
        """;

    try (Connection connection = DataBaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()) {

      while (resultSet.next()) {
        ids.add(resultSet.getInt("id"));
      }

    } catch (Exception e) {

      System.out.println("DATABASE ERROR:");
      e.printStackTrace();
      return new ArrayList<>();
    }

    // Build the units after closing the query so the translation look ups can
    // reuse the same pooled connection instead of opening another one.
    List<TemperatureType> units = new ArrayList<>();
    for (int id : ids) {
      units.add(new TemperatureType(id, locale));
    }

    System.out.println("Units loaded from database: " + units.size());
    return units;
  }
}
