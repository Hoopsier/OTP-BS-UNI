package application;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TempRecordTest {

  @Test
  public void shouldStoreAllValuesFromFullConstructor() {
    LocalDateTime createdAt = LocalDateTime.of(2026, 11, 1, 12, 30, 45);

    TempRecord record = new TempRecord(7, 20.5, 68.9, 1, 2, createdAt);

    assertEquals(7, record.getId());
    assertEquals(20.5, record.getInputValue(), 0.0001);
    assertEquals(68.9, record.getOutputValue(), 0.0001);
    assertEquals(1, record.getFromUnitId());
    assertEquals(2, record.getToUnitId());
    assertEquals(createdAt, record.getCreatedAt());
  }

  @Test
  public void shouldLeaveIdAndTimestampUnsetWhenConstructedForInsert() {
    TempRecord record = new TempRecord(100, 212, 1, 2);

    assertEquals(0, record.getId());
    assertEquals(100, record.getInputValue(), 0.0001);
    assertEquals(212, record.getOutputValue(), 0.0001);
    assertEquals(1, record.getFromUnitId());
    assertEquals(2, record.getToUnitId());
    assertNull(record.getCreatedAt());
  }

  @Test
  public void shouldStoreNegativeAndFractionalValues() {
    TempRecord record = new TempRecord(-40.25, -40.45, 2, 1);

    assertEquals(-40.25, record.getInputValue(), 0.0001);
    assertEquals(-40.45, record.getOutputValue(), 0.0001);
  }
}
