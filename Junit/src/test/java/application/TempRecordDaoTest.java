package application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import java.util.ResourceBundle;
import javafx.scene.control.Label;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

public class TempRecordDaoTest {

  private final TempRecordDao dao = new TempRecordDao();

  @Test
  public void shouldInsertAllRecordValues() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    TempRecord record = new TempRecord(20.5, 68.9, 1, 2);

    try (MockedStatic<DataBaseConnection> db = mockStatic(DataBaseConnection.class)) {
      db.when(DataBaseConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeUpdate()).thenReturn(1);

      dao.saveRecord(record);
    }

    verify(statement).setDouble(1, 20.5);
    verify(statement).setDouble(2, 68.9);
    verify(statement).setInt(3, 1);
    verify(statement).setInt(4, 2);

    ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
    verify(connection).prepareStatement(sql.capture());

    assertTrue(sql.getValue().contains("INSERT INTO temp_record"));
    assertTrue(sql.getValue().contains("(input_value, output_value, from_unit_id, to_unit_id)"));

    verify(statement).executeUpdate();
    verify(statement).close();
    verify(connection).close();
  }

  @Test
  public void shouldNotThrowWhenConnectionFails() {
    TempRecord record = new TempRecord(10, 50, 1, 2);

    try (MockedStatic<DataBaseConnection> db = mockStatic(DataBaseConnection.class)) {
      db.when(DataBaseConnection::getConnection)
          .thenThrow(new SQLException("database unavailable"));

      TestOutput.silence(() -> assertDoesNotThrow(() -> dao.saveRecord(record)));
    }
  }

  @Test
  public void shouldNotThrowWhenInsertFails() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    TempRecord record = new TempRecord(10, 50, 1, 2);

    try (MockedStatic<DataBaseConnection> db = mockStatic(DataBaseConnection.class)) {
      db.when(DataBaseConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeUpdate()).thenThrow(new SQLException("insert failed"));

      TestOutput.silence(() -> assertDoesNotThrow(() -> dao.saveRecord(record)));
    }

    verify(statement).close();
    verify(connection).close();
  }

  @Test
  public void shouldFillHistoryLabelsAndClearUnusedOnes() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);
    ResourceBundle rb = mock(ResourceBundle.class);
    JavaFxTestSupport.requireAvailable();
    Label[] labels = JavaFxTestSupport.createLabels(5);

    try (MockedStatic<DataBaseConnection> db = mockStatic(DataBaseConnection.class);
        MockedStatic<TemperatureLocaleDataAccessObject> locale =
            mockStatic(TemperatureLocaleDataAccessObject.class)) {
      db.when(DataBaseConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeQuery()).thenReturn(resultSet);

      when(rb.getString("history_format")).thenReturn("From {0} {1} to {2} becomes {3} {4}");
      when(resultSet.next()).thenReturn(true, true, false);
      when(resultSet.getDouble("input_value")).thenReturn(20.5, 100.0);
      when(resultSet.getDouble("output_value")).thenReturn(68.9, 212.0);
      when(resultSet.getInt("from_unit_id")).thenReturn(1, 1);
      when(resultSet.getInt("to_unit_id")).thenReturn(2, 2);

      locale.when(() -> TemperatureLocaleDataAccessObject.getUnit(1, "en")).thenReturn("Celsius");
      locale
          .when(() -> TemperatureLocaleDataAccessObject.getUnit(2, "en"))
          .thenReturn("Fahrenheit");
      locale.when(() -> TemperatureLocaleDataAccessObject.getSymbol(2, "en")).thenReturn('F');

      dao.setLastFiveRecordsLabels(labels, "en", rb);
    }

    assertEquals("From 20.5 Celsius to Fahrenheit becomes 69 F", labels[0].getText());
    assertEquals("From 100.0 Celsius to Fahrenheit becomes 212 F", labels[1].getText());
    assertEquals("", labels[2].getText());
    assertEquals("", labels[3].getText());
    assertEquals("", labels[4].getText());

    ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
    verify(connection).prepareStatement(sql.capture());

    assertTrue(
        sql.getValue().contains("SELECT input_value, output_value, from_unit_id, to_unit_id"));
    assertTrue(sql.getValue().contains("FROM temp_record"));
    assertTrue(sql.getValue().contains("ORDER BY id DESC"));
    assertTrue(sql.getValue().contains("LIMIT 5"));

    verify(resultSet).close();
    verify(statement).close();
    verify(connection).close();
  }

  @Test
  public void shouldClearAllHistoryLabelsWhenNoRecordsExist() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);
    ResourceBundle rb = mock(ResourceBundle.class);
    JavaFxTestSupport.requireAvailable();
    Label[] labels = JavaFxTestSupport.createLabels(5);

    try (MockedStatic<DataBaseConnection> db = mockStatic(DataBaseConnection.class)) {
      db.when(DataBaseConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeQuery()).thenReturn(resultSet);
      when(resultSet.next()).thenReturn(false);

      dao.setLastFiveRecordsLabels(labels, "en", rb);
    }

    for (Label label : labels) {
      assertEquals("", label.getText());
    }

    verify(resultSet).close();
    verify(statement).close();
    verify(connection).close();
  }

  @Test
  public void shouldNotOverfillLabelsWhenDatabaseHasMoreRecords() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);
    ResourceBundle rb = mock(ResourceBundle.class);
    JavaFxTestSupport.requireAvailable();
    Label[] labels = JavaFxTestSupport.createLabels(2);

    try (MockedStatic<DataBaseConnection> db = mockStatic(DataBaseConnection.class);
        MockedStatic<TemperatureLocaleDataAccessObject> locale =
            mockStatic(TemperatureLocaleDataAccessObject.class)) {
      db.when(DataBaseConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeQuery()).thenReturn(resultSet);

      when(rb.getString("history_format")).thenReturn("From {0} {1} to {2} becomes {3} {4}");
      when(resultSet.next()).thenReturn(true, true, true);
      when(resultSet.getDouble("input_value")).thenReturn(10.0, 20.0, 30.0);
      when(resultSet.getDouble("output_value")).thenReturn(50.0, 68.0, 86.0);
      when(resultSet.getInt("from_unit_id")).thenReturn(1, 1, 1);
      when(resultSet.getInt("to_unit_id")).thenReturn(2, 2, 2);

      locale.when(() -> TemperatureLocaleDataAccessObject.getUnit(1, "en")).thenReturn("Celsius");
      locale
          .when(() -> TemperatureLocaleDataAccessObject.getUnit(2, "en"))
          .thenReturn("Fahrenheit");
      locale.when(() -> TemperatureLocaleDataAccessObject.getSymbol(2, "en")).thenReturn('F');

      dao.setLastFiveRecordsLabels(labels, "en", rb);
    }

    assertEquals("From 10.0 Celsius to Fahrenheit becomes 50 F", labels[0].getText());
    assertEquals("From 20.0 Celsius to Fahrenheit becomes 68 F", labels[1].getText());
  }

  @Test
  public void shouldNotThrowWhenLoadingHistoryFails() {
    ResourceBundle rb = mock(ResourceBundle.class);
    Label[] labels = new Label[5];

    try (MockedStatic<DataBaseConnection> db = mockStatic(DataBaseConnection.class)) {
      db.when(DataBaseConnection::getConnection)
          .thenThrow(new SQLException("database unavailable"));

      TestOutput.silence(
          () -> assertDoesNotThrow(() -> dao.setLastFiveRecordsLabels(labels, "en", rb)));
    }
  }

  @Test
  public void shouldNotThrowWhenHistoryQueryFails() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    ResourceBundle rb = mock(ResourceBundle.class);
    Label[] labels = new Label[5];

    try (MockedStatic<DataBaseConnection> db = mockStatic(DataBaseConnection.class)) {
      db.when(DataBaseConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeQuery()).thenThrow(new SQLException("query failed"));

      TestOutput.silence(
          () -> assertDoesNotThrow(() -> dao.setLastFiveRecordsLabels(labels, "en", rb)));
    }

    verify(statement).close();
    verify(connection).close();
  }
}
