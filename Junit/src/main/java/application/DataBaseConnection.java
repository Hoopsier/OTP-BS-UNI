package application;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/** Database Connection object. Sets up the MariaDB connection. */
public final class DataBaseConnection {

  private static final String HOST = System.getenv().getOrDefault("DB_HOST", "localhost");

  private static final String PORT = System.getenv().getOrDefault("DB_PORT", "3306");

  private static final String DATABASE =
      System.getenv().getOrDefault("DB_NAME", "temp_temperature");

  private static final String USER = System.getenv().getOrDefault("DB_USER", "hoopsy");

  private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "123123");

  private static final String URL = "jdbc:mariadb://" + HOST + ":" + PORT + "/" + DATABASE;

  /**
   * Opening a physical MariaDB connection is expensive when the app runs in minikube. MariaDB
   * resolves the client address for every new connection and the minikube node has no PTR record,
   * so each connection waits for a DNS timeout. Reusing pooled connections pays that cost only
   * once.
   */
  private static DataSource dataSource;

  private DataBaseConnection() {}

  /** Get the proxied data source. */
  public static Connection getConnection() throws SQLException {
    return getDataSource().getConnection();
  }

  static synchronized DataSource getDataSource() {
    if (dataSource == null) {
      dataSource = new HikariDataSource(createConfig());
    }
    return dataSource;
  }

  static HikariConfig createConfig() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(URL);
    config.setUsername(USER);
    config.setPassword(PASSWORD);
    config.setPoolName("otp-bs-pool");
    config.setMinimumIdle(1);
    config.setMaximumPoolSize(2);
    config.setConnectionTimeout(15_000);
    // Do not connect while the class loads; open the pool on the first query.
    config.setInitializationFailTimeout(-1);
    return config;
  }

  /** Test hook: replaces the pool with a stub data source. */
  static synchronized void setDataSource(DataSource newDataSource) {
    dataSource = newDataSource;
  }
}
