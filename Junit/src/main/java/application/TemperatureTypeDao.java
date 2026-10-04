package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TemperatureTypeDao {

  public List<TemperatureType> getAllUnits() {

    List<TemperatureType> units = new ArrayList<>();

    String sql = """
        SELECT id, name, symbol
        FROM temperature
        ORDER BY id
        """;

    try (
        Connection connection = DBConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()) {

      while (resultSet.next()) {

        TemperatureType unit = new TemperatureType(
            resultSet.getInt("id"),
            resultSet.getString("name"),
            resultSet.getString("symbol"));

        units.add(unit);
      }

      System.out.println("Units loaded from database: " + units.size());

    } catch (Exception e) {

      System.out.println("DATABASE ERROR:");
      e.printStackTrace();
    }

    return units;
  }
}
