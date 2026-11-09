package application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.zaxxer.hikari.HikariConfig;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class DBConnectionTest {

  @AfterEach
  public void resetDataSource() {
    DataBaseConnection.setDataSource(null);
  }

  @Test
  public void shouldConfigurePoolFromEnvironment() {
    String host = System.getenv().getOrDefault("DB_HOST", "localhost");
    String port = System.getenv().getOrDefault("DB_PORT", "3306");
    String database = System.getenv().getOrDefault("DB_NAME", "temp_temperature");
    String user = System.getenv().getOrDefault("DB_USER", "hoopsy");
    String password = System.getenv().getOrDefault("DB_PASSWORD", "123123");

    HikariConfig config = DataBaseConnection.createConfig();

    assertEquals("jdbc:mariadb://" + host + ":" + port + "/" + database, config.getJdbcUrl());
    assertEquals(user, config.getUsername());
    assertEquals(password, config.getPassword());
    assertEquals(1, config.getMinimumIdle());
    assertEquals(2, config.getMaximumPoolSize());
    assertEquals(-1, config.getInitializationFailTimeout());
  }

  @Test
  public void shouldReturnConnectionFromDataSource() throws Exception {
    DataSource dataSource = mock(DataSource.class);
    Connection connection = mock(Connection.class);
    when(dataSource.getConnection()).thenReturn(connection);

    DataBaseConnection.setDataSource(dataSource);

    assertSame(connection, DataBaseConnection.getConnection());
  }

  @Test
  public void shouldPropagateConnectionErrors() throws Exception {
    DataSource dataSource = mock(DataSource.class);
    SQLException failure = new SQLException("database unavailable");
    when(dataSource.getConnection()).thenThrow(failure);

    DataBaseConnection.setDataSource(dataSource);

    SQLException thrown = assertThrows(SQLException.class, DataBaseConnection::getConnection);

    assertSame(failure, thrown);
  }
}
