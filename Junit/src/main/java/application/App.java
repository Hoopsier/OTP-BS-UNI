package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class App extends Application {

  private final TemperatureConverter converter = new TemperatureConverter();
  private final TemperatureTypeDao unitDAO = new TemperatureTypeDao();
  private final TempRecordDao recordDAO = new TempRecordDao();

  @Override
  public void start(Stage stage) {

    Label title = new Label("Temperature Converter");

    // Temperature input
    Label inputLabel = new Label("Temperature:");
    TextField inputField = new TextField();
    inputField.setPromptText("Enter temperature");

    // From unit
    Label fromLabel = new Label("From:");

    ComboBox<TemperatureType> fromUnit = new ComboBox<>();
    fromUnit.setPrefWidth(250);

    // To unit
    Label toLabel = new Label("To:");

    ComboBox<TemperatureType> toUnit = new ComboBox<>();
    toUnit.setPrefWidth(250);

    // Load units from MariaDB
    List<TemperatureType> units = unitDAO.getAllUnits();
    fromUnit.getItems().addAll(units);
    toUnit.getItems().addAll(units);

    if (!units.isEmpty()) {
      fromUnit.setValue(units.get(0));
      if (units.size() > 1) {
        toUnit.setValue(units.get(1));
      }
    }

    // Convert button
    Button convertButton = new Button("Convert");

    // Result
    Label resultLabel = new Label("Result:");

    // Button action
    convertButton.setOnAction(event -> {
      try {
        float temperature = Float.parseFloat(inputField.getText());
        TemperatureType from = fromUnit.getValue();
        TemperatureType to = toUnit.getValue();

        if (from == null || to == null) {
          resultLabel.setText("Please select units.");
          return;
        }

        double result = convertTemperature(
            temperature,
            from.getName(),
            to.getName());

        resultLabel.setText(
            String.format("Result: %.2f %s", result, to.getSymbol()));

        // Save conversion to MariaDB
        TempRecord record = new TempRecord(
            temperature,
            result,
            from.getId(),
            to.getId());
        recordDAO.saveRecord(record);

      } catch (NumberFormatException e) {
        resultLabel.setText("Please enter a valid number.");
      }
    });

    // Layout
    VBox layout = new VBox(10);
    layout.setPadding(new Insets(20));
    layout.getChildren().addAll(
        title,
        inputLabel,
        inputField,
        fromLabel,
        fromUnit,
        toLabel,
        toUnit,
        convertButton,
        resultLabel);

    Scene scene = new Scene(layout, 400, 450);
    stage.setTitle("Temperature Converter");
    stage.setScene(scene);
    stage.show();
  }

  private float convertTemperature(float temperature, String from, String to) {
    if (from.equals("Celsius") && to.equals("Fahrenheit")) {
      return converter.celToFahr(temperature);
    } else if (from.equals("Fahrenheit") && to.equals("Celsius")) {
      return converter.fahrToCel(temperature);
    }
    // Same unit
    return temperature;
  }

  public static void main(String[] args) {
    launch(args);
  }
}
