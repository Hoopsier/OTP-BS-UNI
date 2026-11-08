package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javafx.scene.control.Label;

public class TempRecordDao {

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
    try (
        Connection connection = DBConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery()) {

      int i = 0;
      while (rs.next() && i < labels.length) {
        System.out.println(labels[i]);
        System.out.println(i);
        double inputValue = rs.getDouble("input_value");
        double outputValue = rs.getDouble("output_value");
        int fromId = rs.getInt("from_unit_id");
        int toId = rs.getInt("to_unit_id");

        String fromUnit = TemperatureLocaleDAO.getUnit(fromId, locale);
        String toUnit = TemperatureLocaleDAO.getUnit(toId, locale);
        String outputSymbol = String.valueOf(TemperatureLocaleDAO.getSymbol(toId, locale));

        String text = MessageFormat.format(
            rb.getString("history_format"),
            String.valueOf(inputValue), fromUnit, toUnit, String.valueOf(Math.round(outputValue)), outputSymbol);
        // fill the history labels
        labels[i].setText(text);
        i++;
      }

      // Clear any labels not filled (fewer than 5 records in DB)
      for (; i < labels.length; i++) {
        labels[i].setText("");
      }
    } catch (Exception e) {

      e.printStackTrace();
    }
  }
}
