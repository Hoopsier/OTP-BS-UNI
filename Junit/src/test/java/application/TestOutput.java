package application;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Silences the console output produced by the DAOs while a test exercises their
 * error-handling paths.
 */
final class TestOutput {

  private TestOutput() {
  }

  static void silence(Runnable action) {
    PrintStream originalOut = System.out;
    PrintStream originalErr = System.err;

    try (PrintStream sink = new PrintStream(OutputStream.nullOutputStream())) {
      System.setOut(sink);
      System.setErr(sink);
      action.run();
    } finally {
      System.setOut(originalOut);
      System.setErr(originalErr);
    }
  }
}
