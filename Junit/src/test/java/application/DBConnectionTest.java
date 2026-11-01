package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

public class DBConnectionTest {

  @Test
  public void shouldConnectUsingEnvironmentConfiguration() throws Exception {
    String host = System.getenv().getOrDefault("DB_HOST", "localhost");
    String port = System.getenv().getOrDefault("DB_PORT", "3306");
    String database = System.getenv().getOrDefault("DB_NAME", "temp_temperature");
    String user = System.getenv().getOrDefault("DB_USER", "hoopsy");
    String password = System.getenv().getOrDefault("DB_PASSWORD", "123123");

    String expectedUrl = "jdbc:mariadb://" + host + ":" + port + "/" + database;
    Connection connection = mock(Connection.class);

    try (MockedStatic<DriverManager> driverManager = mockStatic(DriverManager.class)) {
      driverManager
          .when(() -> DriverManager.getConnection(expectedUrl, user, password))
          .thenReturn(connection);

      assertSame(connection, DBConnection.getConnection());

      driverManager.verify(() -> DriverManager.getConnection(expectedUrl, user, password));
    }
  }

  @Test
  public void shouldPropagateConnectionErrors() {
    SQLException failure = new SQLException("database unavailable");

    try (MockedStatic<DriverManager> driverManager = mockStatic(DriverManager.class)) {
      driverManager
          .when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
          .thenThrow(failure);

      SQLException thrown = assertThrows(SQLException.class, DBConnection::getConnection);

      assertSame(failure, thrown);
    }
  }
}
