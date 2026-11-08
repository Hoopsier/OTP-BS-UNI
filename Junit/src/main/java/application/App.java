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
import javafx.scene.text.Font;
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
  private Label historyTitleLabel;
  private ComboBox<TemperatureType> fromUnit;
  private ComboBox<TemperatureType> toUnit;
  private List<TemperatureType> units;
  private VBox historyBox;
  private Label[] history;

  @Override
  public void start(Stage stage) {
    System.out.println(TemperatureLocaleDAO.getSymbol(1, "ar"));
    System.out.println(TemperatureLocaleDAO.getUnit(1, "ar"));
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

    fromUnit = new ComboBox<>();
    fromUnit.setPrefWidth(250);

    // To unit
    toLabel = new Label(rb.getString("to"));

    toUnit = new ComboBox<>();
    toUnit.setPrefWidth(250);

    // Load units from MariaDB
    units = unitDAO.getAllUnits(locale.getLanguage());
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
            from.getId(),
            to.getId());

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

        recordDAO.setLastFiveRecordsLabels(history, locale.getLanguage(), rb);

      } catch (NumberFormatException e) {
        resultLabel.setText(rb.getString("numberFault"));
      }
    });

    // Layout
    VBox layout = new VBox();
    layout.setPadding(new Insets(20));

    historyTitleLabel = new Label(rb.getString("history"));
    historyBox = new VBox();
    history = new Label[5];
    for (int i = 0; i < history.length; i++) {
      history[i] = new Label();
      historyBox.getChildren().add(history[i]);
      history[i].setFont(Font.font("Noto Sans CJK JP", 16));
    }
    recordDAO.setLastFiveRecordsLabels(history, locale.getLanguage(), rb);

    // Make sure you have Noto Sans CJK installed, otherwise you can't see japanese
    title.setFont(Font.font("Noto Sans CJK JP", 24));
    inputLabel.setFont(Font.font("Noto Sans CJK JP", 16));
    inputField.setFont(Font.font("Noto Sans CJK JP", 16));
    fromLabel.setFont(Font.font("Noto Sans CJK JP", 16));
    toLabel.setFont(Font.font("Noto Sans CJK JP", 16));
    convertButton.setFont(Font.font("Noto Sans CJK JP", 16));
    resultLabel.setFont(Font.font("Noto Sans CJK JP", 16));
    historyTitleLabel.setFont(Font.font("Noto Sans CJK JP", 24));
    fromUnit.setStyle("-fx-font: 16px \"Noto Sans CJK JP\";");
    toUnit.setStyle("-fx-font: 16px \"Noto Sans CJK JP\";");

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
        resultLabel,
        historyTitleLabel,
        historyBox);

    root = new Scene(layout, 400, 450);
    stage.setTitle("Temperature Converter");
    stage.setScene(root);
    stage.show();
  }

  private float convertTemperature(float temperature, int from, int to) {
    System.out.println(from);
    if (from == 1
        && to == 2) {
      return converter.celToFahr(temperature);
    } else if (from == 2
        && to == 1) {
      return converter.fahrToCel(temperature);
    }
    System.out.println(to);

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
    historyTitleLabel.setText(rb.getString("history"));
    units = unitDAO.getAllUnits(locale.getLanguage());
    fromUnit.getItems().clear();
    toUnit.getItems().clear();
    fromUnit.getItems().addAll(units);
    toUnit.getItems().addAll(units);
    recordDAO.setLastFiveRecordsLabels(history, locale.getLanguage(), rb);
  }
}
