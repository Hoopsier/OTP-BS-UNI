package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

  private static final String HOST = System.getenv().getOrDefault("DB_HOST", "localhost");

  private static final String PORT = System.getenv().getOrDefault("DB_PORT", "3306");

  private static final String DATABASE = System.getenv().getOrDefault("DB_NAME", "temp_temperature");

  private static final String USER = System.getenv().getOrDefault("DB_USER", "hoopsy");

  private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "123123");

  private static final String URL = "jdbc:mariadb://" + HOST + ":" + PORT + "/" + DATABASE;

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }
}
