package application;

import java.time.LocalDateTime;

/**
 * Temperature record. Saved to database with.
 *
 * @id id
 * @attribute inputValue
 * @attribute outputValue
 * @attribute fromUnitId
 * @attribute toUnitId
 * @attribute createdAt
 * @attribute TempRecord
 */
public class TempRecord {

  private int id;
  private double inputValue;
  private double outputValue;
  private int fromUnitId;
  private int toUnitId;
  private LocalDateTime createdAt;

  /** Constructor for temperature record. */
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

  /** Constructor for temperature record without date. */
  public TempRecord(double inputValue, double outputValue, int fromUnitId, int toUnitId) {

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
