package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TempRecordDaoTest {

  private final TempRecordDao dao = new TempRecordDao();

  @Test
  public void shouldInsertAllRecordValues() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    TempRecord record = new TempRecord(20.5, 68.9, 1, 2);

    try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {
      db.when(DBConnection::getConnection).thenReturn(connection);
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

    try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {
      db.when(DBConnection::getConnection)
          .thenThrow(new SQLException("database unavailable"));

      TestOutput.silence(() -> assertDoesNotThrow(() -> dao.saveRecord(record)));
    }
  }

  @Test
  public void shouldNotThrowWhenInsertFails() throws Exception {
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    TempRecord record = new TempRecord(10, 50, 1, 2);

    try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {
      db.when(DBConnection::getConnection).thenReturn(connection);
      when(connection.prepareStatement(anyString())).thenReturn(statement);
      when(statement.executeUpdate()).thenThrow(new SQLException("insert failed"));

      TestOutput.silence(() -> assertDoesNotThrow(() -> dao.saveRecord(record)));
    }

    verify(statement).close();
    verify(connection).close();
  }
}
