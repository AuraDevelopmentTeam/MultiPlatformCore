package team.aura_dev.lib.multiplatformcore.download;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;

public class DependencyDownloadExceptionTest {
  private static final String standardMessage = "Standard Exception Message";
  private static final RuntimeException standardCause =
      new RuntimeException("Standard Exception Cause");

  @Test
  public void simpleConstructorTest() {
    final DependencyDownloadException nothing = new DependencyDownloadException();
    final DependencyDownloadException causeOnly = new DependencyDownloadException(standardCause);
    final DependencyDownloadException nullCause = new DependencyDownloadException((Throwable) null);
    final DependencyDownloadException messageOnly =
        new DependencyDownloadException(standardMessage);
    final DependencyDownloadException messageAndCause =
        new DependencyDownloadException(standardMessage, standardCause);

    assertEquals(null, nothing.getMessage());
    assertEquals("java.lang.RuntimeException: Standard Exception Cause", causeOnly.getMessage());
    assertEquals(null, nullCause.getMessage());
    assertEquals(standardMessage, messageOnly.getMessage());
    assertEquals(standardMessage, messageAndCause.getMessage());
  }

  @Test
  public void libPathConstructorTest() {
    final Path libsDir = Paths.get(".");

    final DependencyDownloadException messageOnly =
        new DependencyDownloadException(standardMessage, libsDir);
    final DependencyDownloadException messageAndCause =
        new DependencyDownloadException(standardMessage, libsDir, standardCause);

    final String modifiedStandardMessage =
        standardMessage
            + "\nIf you see this error for the first time delete the \""
            + libsDir.toAbsolutePath()
            + "\" folder and restart the server."
            + "\nIf this error persists feel free to report it to the plugin support.";

    assertEquals(modifiedStandardMessage, messageOnly.getMessage());
    assertEquals(modifiedStandardMessage, messageAndCause.getMessage());
  }
}
