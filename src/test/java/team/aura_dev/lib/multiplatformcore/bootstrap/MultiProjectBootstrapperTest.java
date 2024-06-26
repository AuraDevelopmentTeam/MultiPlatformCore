package team.aura_dev.lib.multiplatformcore.bootstrap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import team.aura_dev.lib.multiplatformcore.DependencyClassLoader;
import team.aura_dev.lib.multiplatformcore.testcode.simple.bootstrap.TestBadBootstrapper;
import team.aura_dev.lib.multiplatformcore.testcode.simple.bootstrap.TestBootstrapper;
import team.aura_dev.lib.multiplatformcore.testcode.simple.bootstrap.TestPluginBootstrap;

public class MultiProjectBootstrapperTest {
  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void constructorTest() {
    final MultiProjectBootstrapper<Object> base =
        new MultiProjectBootstrapper<Object>(Object.class) {};
    final MultiProjectBootstrapper<Object> generator =
        new MultiProjectBootstrapper<Object>(
            Object.class, () -> new DependencyClassLoader("@group@")) {};
    final MultiProjectBootstrapper<Object> direct =
        new MultiProjectBootstrapper<Object>(
            Object.class,
            AccessController.doPrivileged(
                (PrivilegedAction<DependencyClassLoader>)
                    () -> new DependencyClassLoader("@group@"))) {};

    assertEquals(
        base.getDependencyClassLoader().excludedPackageNames,
        generator.getDependencyClassLoader().excludedPackageNames);
    assertEquals(
        base.getDependencyClassLoader().excludedPackageNames,
        direct.getDependencyClassLoader().excludedPackageNames);
  }

  @Test
  public void correctClassLoaderTest() {
    final TestPluginBootstrap plugin = new TestPluginBootstrap();
    final TestBootstrapper bootstrapper = plugin.getBootstrapper();

    assertEquals(
        "team.aura_dev.lib.multiplatformcore.testcode.simple.TestPlugin",
        bootstrapper.getPluginClass().getName());
    assertSame(
        bootstrapper.getDependencyClassLoader(), bootstrapper.getPluginClass().getClassLoader());
  }

  @Test
  public void simpleTest() {
    final TestPluginBootstrap plugin = new TestPluginBootstrap();

    // Just calling with no feedback to make sure no exceptions
    plugin.testCall();
  }

  @Test
  public void flagTest() {
    final AtomicBoolean flag = new AtomicBoolean(false);
    final TestPluginBootstrap plugin = new TestPluginBootstrap(flag);

    plugin.updateFlag();

    assertTrue(flag.get());
  }

  @Test
  public void bootstrapFlagTest() {
    final TestPluginBootstrap plugin = new TestPluginBootstrap();

    assertTrue(plugin.updateBootstrapFlag());
  }

  @Test
  public void utilityFlagTest() {
    final AtomicBoolean flag = new AtomicBoolean(false);
    final TestPluginBootstrap plugin = new TestPluginBootstrap(flag);

    plugin.updateUtilityFlag();

    assertTrue(flag.get());
  }

  @Test
  public void constructorExceptionTest() {
    final Throwable exception = new RuntimeException("Example Exception Message");

    try {
      new TestPluginBootstrap(exception);

      fail("Expected an exception to be thrown");
    } catch (IllegalStateException e) {
      assertEquals("Loading the plugin class resulted in an exception ", e.getMessage());
      assertSame(exception, e.getCause());
    }
  }

  @Test
  public void methodExceptionTest() {
    final Throwable exception = new RuntimeException("Example Exception Message");
    final TestPluginBootstrap plugin = new TestPluginBootstrap();

    try {
      plugin.exceptionTest(exception);

      fail("Expected an exception to be thrown");
    } catch (Throwable e) {
      assertSame(exception, e);
    }
  }

  @Test
  public void noSuchConstructorTest() {
    try {
      new TestPluginBootstrap("boom");

      fail("Expected an exception to be thrown");
    } catch (IllegalStateException e) {
      assertEquals("Loading the plugin class failed", e.getMessage());
      assertEquals("argument type mismatch", e.getCause().getMessage());
    }
  }

  @Test
  public void badBaseClassTest() {
    final TestPluginBootstrap plugin = new TestPluginBootstrap();

    try {
      final TestBadBootstrapper bootstrapper = new TestBadBootstrapper();

      bootstrapper.initializePlugin(plugin);

      fail("Expected an exception to be thrown");
    } catch (IllegalStateException e) {
      assertEquals(
          "The loaded plugin instance is of type \"team.aura_dev.lib.multiplatformcore.testcode.simple.TestPlugin\" and cannot be cast to the plugin base class \"java.lang.String\".",
          e.getMessage());
    }
  }

  @Test
  public void configurateTest() throws IOException {
    final TestPluginBootstrap plugin = new TestPluginBootstrap();

    // Just calling with no feedback to make sure no exceptions
    plugin.configurateTest(folder.newFolder("libsDir").toPath());
  }

  @Test(expected = NoClassDefFoundError.class)
  public void configurateNoLoadTest() throws IOException {
    final TestPluginBootstrap plugin = new TestPluginBootstrap();

    // Just calling with no feedback to make sure no exceptions
    plugin.configurateNoLoadTest(folder.newFolder("libsDir").toPath());
  }

  @Test
  public void apiInteractionTest() {
    final TestPluginBootstrap plugin = new TestPluginBootstrap();

    // Just calling with no feedback to make sure no exceptions
    plugin.apiInteraction();
  }
}
