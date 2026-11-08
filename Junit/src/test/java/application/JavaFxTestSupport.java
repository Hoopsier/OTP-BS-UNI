package application;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.junit.jupiter.api.Assumptions;

/**
 * Starts the JavaFX toolkit once so tests can create real {@link Label}
 * instances without requiring a running {@code Application}.
 */
final class JavaFxTestSupport {

  private static boolean initialized;
  private static boolean available;

  private JavaFxTestSupport() {
  }

  static synchronized boolean isAvailable() {
    if (initialized) {
      return available;
    }
    initialized = true;

    try {
      CountDownLatch started = new CountDownLatch(1);
      Platform.startup(started::countDown);
      available = started.await(10, TimeUnit.SECONDS);
    } catch (IllegalStateException alreadyRunning) {
      available = true;
    } catch (Throwable unavailable) {
      available = false;
    }
    return available;
  }

  static void requireAvailable() {
    Assumptions.assumeTrue(
        isAvailable(),
        "JavaFX toolkit is unavailable in this environment");
  }

  static Label[] createLabels(int count) throws Exception {
    Label[] labels = new Label[count];
    runOnFxThread(() -> {
      for (int i = 0; i < count; i++) {
        labels[i] = new Label();
      }
    });
    return labels;
  }

  private static void runOnFxThread(ThrowingRunnable action) throws Exception {
    if (Platform.isFxApplicationThread()) {
      action.run();
      return;
    }

    CountDownLatch finished = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();

    Platform.runLater(() -> {
      try {
        action.run();
      } catch (Throwable t) {
        failure.set(t);
      } finally {
        finished.countDown();
      }
    });

    if (!finished.await(10, TimeUnit.SECONDS)) {
      throw new IllegalStateException("Timed out waiting for the JavaFX thread");
    }

    Throwable thrown = failure.get();
    if (thrown instanceof Exception) {
      throw (Exception) thrown;
    }
    if (thrown instanceof Error) {
      throw (Error) thrown;
    }
    if (thrown != null) {
      throw new IllegalStateException(thrown);
    }
  }

  @FunctionalInterface
  private interface ThrowingRunnable {
    void run() throws Exception;
  }
}
