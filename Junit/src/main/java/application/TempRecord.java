package application;

import java.time.LocalDateTime;

public class TempRecord {

  private int id;
  private double inputValue;
  private double outputValue;
  private int fromUnitId;
  private int toUnitId;
  private LocalDateTime createdAt;

  public TempRecord(
      int id,
      double inputValue,
      double outputValue,
      int fromUnitId,
      int toUnitId,
      LocalDateTime createdAt) {

    this.id = id;
    this.inputValue = inputValue;
    this.outputValue = outputValue;
    this.fromUnitId = fromUnitId;
    this.toUnitId = toUnitId;
    this.createdAt = createdAt;
  }

  public TempRecord(
      double inputValue,
      double outputValue,
      int fromUnitId,
      int toUnitId) {

    this.inputValue = inputValue;
    this.outputValue = outputValue;
    this.fromUnitId = fromUnitId;
    this.toUnitId = toUnitId;
  }

  public int getId() {
    return id;
  }

  public double getInputValue() {
    return inputValue;
  }

  public double getOutputValue() {
    return outputValue;
  }

  public int getFromUnitId() {
    return fromUnitId;
  }

  public int getToUnitId() {
    return toUnitId;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
