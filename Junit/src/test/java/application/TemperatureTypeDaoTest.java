package application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

public class TemperatureTypeDaoTest {

  private final TemperatureTypeDataAccessObject dao = new TemperatureTypeDataAccessObject();

  @Test
  public void shouldReturnAllUnitsFromDatabase() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);

    try (MockedStatic<DataBaseConnection> db = mockStatic(DataBaseConnection.class);
        MockedStatic<TemperatureLocaleDataAccessObject> locale =
            mockStatic(TemperatureLocaleDataAccessObject.class)) {
      db.when(DataBaseConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeQuery()).thenReturn(resultSet);

      when(resultSet.next()).thenReturn(true, true, false);
      when(resultSet.getInt("id")).thenReturn(1, 2);

      locale.when(() -> TemperatureLocaleDataAccessObject.getUnit(1, "en")).thenReturn("Celsius");
      locale
          .when(() -> TemperatureLocaleDataAccessObject.getUnit(2, "en"))
          .thenReturn("Fahrenheit");
      locale.when(() -> TemperatureLocaleDataAccessObject.getSymbol(1, "en")).thenReturn('C');
      locale.when(() -> TemperatureLocaleDataAccessObject.getSymbol(2, "en")).thenReturn('F');

      List<TemperatureType> units = dao.getAllUnits("en");

      assertEquals(2, units.size());

      assertEquals(1, units.get(0).getId());
      assertEquals("Celsius", units.get(0).getName());
      assertEquals('C', units.get(0).getSymbol());

      assertEquals(2, units.get(1).getId());
      assertEquals("Fahrenheit", units.get(1).getName());
      assertEquals('F', units.get(1).getSymbol());
    }

    ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
    verify(connection).prepareStatement(sql.capture());

    assertTrue(sql.getValue().contains("SELECT id, name, symbol"));
    assertTrue(sql.getValue().contains("FROM temperature"));
    assertTrue(sql.getValue().contains("ORDER BY id"));

    verify(resultSet).close();
    verify(statement).close();
    verify(connection).close();
  }

  @Test
  public void shouldReturnEmptyListWhenNoUnitsExist() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);

    try (MockedStatic<DataBaseConnection> db = mockStatic(DataBaseConnection.class)) {
      db.when(DataBaseConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeQuery()).thenReturn(resultSet);
      when(resultSet.next()).thenReturn(false);

      assertTrue(dao.getAllUnits("en").isEmpty());
    }

    verify(resultSet).close();
    verify(statement).close();
    verify(connection).close();
  }

  @Test
  public void shouldReturnEmptyListWhenConnectionFails() {
    try (MockedStatic<DataBaseConnection> db = mockStatic(DataBaseConnection.class)) {
      db.when(DataBaseConnection::getConnection)
          .thenThrow(new SQLException("database unavailable"));

      TestOutput.silence(() -> assertTrue(dao.getAllUnits("en").isEmpty()));
    }
  }

  @Test
  public void shouldReturnEmptyListWhenQueryFails() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);

    try (MockedStatic<DataBaseConnection> db = mockStatic(DataBaseConnection.class)) {
      db.when(DataBaseConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeQuery()).thenThrow(new SQLException("query failed"));

      TestOutput.silence(() -> assertTrue(dao.getAllUnits("en").isEmpty()));
    }

    verify(statement).close();
    verify(connection).close();
  }
}
