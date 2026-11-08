package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.control.Label;

public class TempRecordDao {

  private record HistoryRow(double inputValue, double outputValue, int fromId, int toId) {
  }

  public void saveRecord(TempRecord record) {

    String sql = """
        INSERT INTO temp_record
        (input_value, output_value, from_unit_id, to_unit_id)
        VALUES (?, ?, ?, ?)
        """;

    try (
        Connection connection = DBConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setDouble(1, record.getInputValue());
      statement.setDouble(2, record.getOutputValue());
      statement.setInt(3, record.getFromUnitId());
      statement.setInt(4, record.getToUnitId());

      statement.executeUpdate();

      System.out.println("Temperature record saved.");

    } catch (Exception e) {

      e.printStackTrace();
    }
  }

  public void setLastFiveRecordsLabels(Label[] labels, String locale, ResourceBundle rb) {
    String sql = """
        SELECT input_value, output_value, from_unit_id, to_unit_id
        FROM temp_record
        ORDER BY id DESC
        LIMIT 5;
        """;
    List<HistoryRow> rows = new ArrayList<>();

    try (
        Connection connection = DBConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery()) {

      while (rs.next() && rows.size() < labels.length) {
        rows.add(new HistoryRow(
            rs.getDouble("input_value"),
            rs.getDouble("output_value"),
            rs.getInt("from_unit_id"),
            rs.getInt("to_unit_id")));
      }
    } catch (Exception e) {

      e.printStackTrace();
      return;
    }

    // Localize after the query is closed so the lookups reuse the pooled
    // connection instead of opening a second one.
    int i = 0;
    for (HistoryRow row : rows) {
      String fromUnit = TemperatureLocaleDAO.getUnit(row.fromId(), locale);
      String toUnit = TemperatureLocaleDAO.getUnit(row.toId(), locale);
      String outputSymbol = String.valueOf(TemperatureLocaleDAO.getSymbol(row.toId(), locale));

      String text = MessageFormat.format(
          rb.getString("history_format"),
          String.valueOf(row.inputValue()), fromUnit, toUnit,
          String.valueOf(Math.round(row.outputValue())), outputSymbol);
      labels[i].setText(text);
      i++;
    }

    // Clear any labels not filled (fewer than 5 records in DB)
    for (; i < labels.length; i++) {
      labels[i].setText("");
    }
  }
}
