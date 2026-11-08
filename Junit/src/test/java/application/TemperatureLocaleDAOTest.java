package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TemperatureLocaleDAOTest {

  @Test
  public void shouldReturnLocalizedSymbolFromDatabase() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);

    try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {
      db.when(DBConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeQuery()).thenReturn(resultSet);
      when(resultSet.next()).thenReturn(true);
      when(resultSet.getString("symbol")).thenReturn("ど");

      assertEquals('ど', TemperatureLocaleDAO.getSymbol(1, "ja"));
    }

    verify(statement).setString(1, "ja");
    verify(statement).setInt(2, 1);
    verify(resultSet).close();
    verify(statement).close();
    verify(connection).close();
  }

  @Test
  public void shouldReturnLocalizedNameFromDatabase() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);

    try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {
      db.when(DBConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeQuery()).thenReturn(resultSet);
      when(resultSet.next()).thenReturn(true);
      when(resultSet.getString("name")).thenReturn("セルシウス");

      assertEquals("セルシウス", TemperatureLocaleDAO.getUnit(1, "ja"));
    }

    verify(statement).setString(1, "ja");
    verify(statement).setInt(2, 1);
    verify(resultSet).close();
    verify(statement).close();
    verify(connection).close();
  }

  @ParameterizedTest(name = "type {0} falls back to symbol {1}")
  @CsvSource({
      "1, C",
      "2, F",
      "3, F"
  })
  public void shouldFallBackToDefaultSymbolWhenNoTranslationExists(int type, char expected) throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);

    try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {
      db.when(DBConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeQuery()).thenReturn(resultSet);
      when(resultSet.next()).thenReturn(false);

      assertEquals(expected, TemperatureLocaleDAO.getSymbol(type, "en"));
    }
  }

  @Test
  public void shouldFallBackToDefaultSymbolWhenTranslationIsNull() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);

    try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {
      db.when(DBConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeQuery()).thenReturn(resultSet);
      when(resultSet.next()).thenReturn(true);
      when(resultSet.getString("symbol")).thenReturn(null);

      assertEquals('C', TemperatureLocaleDAO.getSymbol(1, "en"));
    }
  }

  @Test
  public void shouldFallBackToDefaultSymbolWhenTranslationIsEmpty() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);

    try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {
      db.when(DBConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeQuery()).thenReturn(resultSet);
      when(resultSet.next()).thenReturn(true);
      when(resultSet.getString("symbol")).thenReturn("");

      assertEquals('F', TemperatureLocaleDAO.getSymbol(2, "en"));
    }
  }

  @Test
  public void shouldFallBackToDefaultSymbolWhenDatabaseFails() {
    try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {
      db.when(DBConnection::getConnection)
          .thenThrow(new SQLException("database unavailable"));

      TestOutput.silence(() -> {
        assertEquals('C', TemperatureLocaleDAO.getSymbol(1, "en"));
        assertEquals('F', TemperatureLocaleDAO.getSymbol(2, "en"));
      });
    }
  }

  @Test
  public void shouldFallBackToDefaultSymbolWhenQueryFails() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);

    try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {
      db.when(DBConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeQuery()).thenThrow(new SQLException("query failed"));

      TestOutput.silence(() -> assertEquals('C', TemperatureLocaleDAO.getSymbol(1, "en")));
    }

    verify(statement).close();
    verify(connection).close();
  }

  @ParameterizedTest(name = "type {0} falls back to unit {1}")
  @CsvSource({
      "1, Celcius",
      "2, Fahrenheit",
      "3, Fahrenheit"
  })
  public void shouldFallBackToDefaultUnitWhenNoTranslationExists(int type, String expected) throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);

    try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {
      db.when(DBConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeQuery()).thenReturn(resultSet);
      when(resultSet.next()).thenReturn(false);

      assertEquals(expected, TemperatureLocaleDAO.getUnit(type, "en"));
    }
  }

  @Test
  public void shouldFallBackToDefaultUnitWhenTranslationIsNull() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);

    try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {
      db.when(DBConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeQuery()).thenReturn(resultSet);
      when(resultSet.next()).thenReturn(true);
      when(resultSet.getString("name")).thenReturn(null);

      assertEquals("Celcius", TemperatureLocaleDAO.getUnit(1, "en"));
    }
  }

  @Test
  public void shouldFallBackToDefaultUnitWhenTranslationIsEmpty() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);

    try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {
      db.when(DBConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeQuery()).thenReturn(resultSet);
      when(resultSet.next()).thenReturn(true);
      when(resultSet.getString("name")).thenReturn("");

      assertEquals("Fahrenheit", TemperatureLocaleDAO.getUnit(2, "en"));
    }
  }

  @Test
  public void shouldFallBackToDefaultUnitWhenDatabaseFails() {
    try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {
      db.when(DBConnection::getConnection)
          .thenThrow(new SQLException("database unavailable"));

      TestOutput.silence(() -> {
        assertEquals("Celcius", TemperatureLocaleDAO.getUnit(1, "en"));
        assertEquals("Fahrenheit", TemperatureLocaleDAO.getUnit(2, "en"));
      });
    }
  }

  @Test
  public void shouldFallBackToDefaultUnitWhenQueryFails() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);

    try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {
      db.when(DBConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeQuery()).thenThrow(new SQLException("query failed"));

      TestOutput.silence(() -> assertEquals("Celcius", TemperatureLocaleDAO.getUnit(1, "en")));
    }

    verify(statement).close();
    verify(connection).close();
  }
}
