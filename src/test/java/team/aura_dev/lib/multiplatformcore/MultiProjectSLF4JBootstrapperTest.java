package team.aura_dev.lib.multiplatformcore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import team.aura_dev.lib.multiplatformcore.testcode.slf4j.TestBootstrapper;
import team.aura_dev.lib.multiplatformcore.testcode.slf4j.TestPluginBootstrap;

public class MultiProjectSLF4JBootstrapperTest {
  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void constructorTest() {
    final MultiProjectSLF4JBootstrapper<Object> base =
        new MultiProjectSLF4JBootstrapper<Object>(Object.class) {};
    final MultiProjectSLF4JBootstrapper<Object> generator =
        new MultiProjectSLF4JBootstrapper<Object>(
            Object.class, () -> new DependencyClassLoader("@group@")) {};
    final MultiProjectSLF4JBootstrapper<Object> direct =
        new MultiProjectSLF4JBootstrapper<Object>(
            Object.class,
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
  public void correctClassLoaderTest() throws IOException {
    final TestPluginBootstrap plugin =
        new TestPluginBootstrap(folder.newFolder("libsDir").toPath());
    final TestBootstrapper bootstrapper = plugin.getBootstrapper();

    assertEquals(
        "team.aura_dev.lib.multiplatformcore.testcode.slf4j.TestPlugin",
        bootstrapper.getPluginClass().getName());
    assertSame(
        bootstrapper.getDependencyClassLoader(), bootstrapper.getPluginClass().getClassLoader());
  }

  @Test
  public void simpleTest() throws IOException {
    final TestPluginBootstrap plugin =
        new TestPluginBootstrap(folder.newFolder("libsDir").toPath());

    // Just calling with no feedback to make sure no exceptions
    plugin.testCall();
  }
}
