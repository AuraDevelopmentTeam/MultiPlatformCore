package team.aura_dev.lib.multiplatformcore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import team.aura_dev.lib.multiplatformcore.testcode.TestBootstrapPlugin;

public class MultiProjectBootstrapTest {
  @Test
  public void constructorTest() {
    final MultiProjectBootstrap base = new MultiProjectBootstrap() {};
    final MultiProjectBootstrap generator =
        new MultiProjectBootstrap(() -> new DependencyClassLoader("@group@")) {};
    final MultiProjectBootstrap direct =
        new MultiProjectBootstrap(
            AccessController.doPrivileged(
                (PrivilegedAction<DependencyClassLoader>)
                    () -> new DependencyClassLoader("@group@"))) {};

    assertEquals(
        base.getDependencyClassLoader().packageName,
        generator.getDependencyClassLoader().packageName);
    assertEquals(
        base.getDependencyClassLoader().apiPackageName,
        generator.getDependencyClassLoader().apiPackageName);
    assertEquals(
        base.getDependencyClassLoader().packageName, direct.getDependencyClassLoader().packageName);
    assertEquals(
        base.getDependencyClassLoader().apiPackageName,
        direct.getDependencyClassLoader().apiPackageName);
  }

  @Test
  public void simpleTest() {
    final TestBootstrapPlugin plugin = new TestBootstrapPlugin();

    // Just calling with no feedback to make sure no exceptions
    plugin.testCall();
  }

  @Test
  public void flagTest() {
    final AtomicBoolean flag = new AtomicBoolean(false);
    final TestBootstrapPlugin plugin = new TestBootstrapPlugin(flag);

    plugin.updateFlag();

    assertTrue(flag.get());
  }

  @Test
  public void bootstrapFlagTest() {
    final TestBootstrapPlugin plugin = new TestBootstrapPlugin();

    assertTrue(plugin.updateBootstrapFlag());
  }

  @Test
  public void constructorExceptionTest() {
    final Throwable exception = new RuntimeException("Example Exception Message");

    try {
      new TestBootstrapPlugin(exception);

      fail("Expected an exception to be thrown");
    } catch (IllegalStateException e) {
      assertEquals("Loading the plugin class resulted in an exception ", e.getMessage());
      assertSame(exception, e.getCause());
    }
  }

  @Test
  public void methodExceptionTest() {
    final Throwable exception = new RuntimeException("Example Exception Message");
    final TestBootstrapPlugin plugin = new TestBootstrapPlugin();

    try {
      plugin.exceptionTest(exception);

      fail("Expected an exception to be thrown");
    } catch (IllegalStateException e) {
      assertEquals("Calling exceptionTest resulted in an exception", e.getMessage());
      assertSame(exception, e.getCause());
    }
  }

  @Test
  public void noSuchConstructorTest() {
    try {
      new TestBootstrapPlugin("boom");

      fail("Expected an exception to be thrown");
    } catch (IllegalStateException e) {
      assertEquals("Loading the plugin class failed", e.getMessage());
      assertEquals("argument type mismatch", e.getCause().getMessage());
    }
  }

  @Test
  public void noSuchMethodTest() {
    final TestBootstrapPlugin plugin = new TestBootstrapPlugin();

    try {
      plugin.noSuchMethodTest();

      fail("Expected an exception to be thrown");
    } catch (IllegalStateException e) {
      assertEquals("Calling noSuchMethodTest failed", e.getMessage());
      assertEquals(
          "team.aura_dev.lib.multiplatformcore.testcode.TestPlugin.noSuchMethodTest()",
          e.getCause().getMessage());
    }
  }
}
