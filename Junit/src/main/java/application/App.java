package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

  private final TemperatureConverter converter = new TemperatureConverter();
  private final TemperatureTypeDao unitDAO = new TemperatureTypeDao();
  private final TempRecordDao recordDAO = new TempRecordDao();
  private Scene root;

  private Locale locale = Locale.of("en", "US");
  private ResourceBundle rb = ResourceBundle.getBundle("Messages", locale);

  // UI components that need language updates
  private Label title;
  private Label inputLabel;
  private TextField inputField;
  private Label fromLabel;
  private Label toLabel;
  private Button convertButton;
  private Label resultLabel;

  @Override
  public void start(Stage stage) {
    HBox hBox = new HBox();

    Button en = new Button("en");
    Button fi = new Button("fi");
    Button ja = new Button("ja");
    Button sv = new Button("sv");
    Button ar = new Button("ar");

    en.setOnAction((event) -> {
      locale = Locale.of("en", "US");
      rb = ResourceBundle.getBundle("Messages", locale);
      updateLanguage();
      root.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
    });

    fi.setOnAction((event) -> {
      locale = Locale.of("fi", "FI");
      rb = ResourceBundle.getBundle("Messages", locale);
      updateLanguage();
      root.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
    });

    ja.setOnAction((event) -> {
      locale = Locale.of("ja", "JP");
      rb = ResourceBundle.getBundle("Messages", locale);
      updateLanguage();
      root.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
    });

    sv.setOnAction((event) -> {
      locale = Locale.of("sv", "SV");
      rb = ResourceBundle.getBundle("Messages", locale);
      updateLanguage();
      root.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
    });

    ar.setOnAction((event) -> {
      locale = Locale.of("ar", "AE");
      rb = ResourceBundle.getBundle("Messages", locale);
      updateLanguage();
      root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
    });

    hBox.getChildren().addAll(en, fi, ja, sv, ar);

    title = new Label(rb.getString("title"));

    // Temperature input
    inputLabel = new Label(rb.getString("temperature"));
    inputField = new TextField();
    inputField.setPromptText(rb.getString("input"));

    // From unit
    fromLabel = new Label(rb.getString("from"));

    ComboBox<TemperatureType> fromUnit = new ComboBox<>();
    fromUnit.setPrefWidth(250);

    // To unit
    toLabel = new Label(rb.getString("to"));

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
    convertButton = new Button(rb.getString("convert-btn"));

    // Result
    resultLabel = new Label(rb.getString("result"));

    // Button action
    convertButton.setOnAction((event) -> {
      try {
        float temperature = Float.parseFloat(inputField.getText());
        TemperatureType from = fromUnit.getValue();
        TemperatureType to = toUnit.getValue();

        if (from == null || to == null) {
          resultLabel.setText(rb.getString("selectFault"));
          return;
        }

        double result = convertTemperature(
            temperature,
            from.getName(),
            to.getName());

        resultLabel.setText(
            String.format("%s %.2f %s",
                rb.getString("result"),
                result,
                to.getSymbol()));

        // Save conversion to MariaDB
        TempRecord record = new TempRecord(
            temperature,
            result,
            from.getId(),
            to.getId());

        recordDAO.saveRecord(record);

      } catch (NumberFormatException e) {
        resultLabel.setText(rb.getString("numberFault"));
      }
    });

    // Layout
    VBox layout = new VBox();
    layout.setPadding(new Insets(20));
    layout.getChildren().addAll(
        hBox,
        title,
        inputLabel,
        inputField,
        fromLabel,
        fromUnit,
        toLabel,
        toUnit,
        convertButton,
        resultLabel);

    root = new Scene(layout, 400, 450);
    stage.setTitle("Temperature Converter");
    stage.setScene(root);
    stage.show();
  }

  private float convertTemperature(float temperature, String from, String to) {
    if (from.equals(rb.getString("celcius"))
        && to.equals(rb.getString("fahrenheit"))) {
      return converter.celToFahr(temperature);
    } else if (from.equals(rb.getString("fahrenheit"))
        && to.equals(rb.getString("celcius"))) {
      return converter.fahrToCel(temperature);
    }

    // Same unit
    return temperature;
  }

  public static void main(String[] args) {
    launch(args);
  }

  private void updateLanguage() {
    title.setText(rb.getString("title"));

    inputLabel.setText(rb.getString("temperature"));
    inputField.setPromptText(rb.getString("input"));

    fromLabel.setText(rb.getString("from"));
    toLabel.setText(rb.getString("to"));

    convertButton.setText(rb.getString("convert-btn"));
    resultLabel.setText(rb.getString("result"));
  }
}
