package team.aura_dev.lib.multiplatformcore.bootstrap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import team.aura_dev.lib.multiplatformcore.DependencyClassLoader;
import team.aura_dev.lib.multiplatformcore.testcode.slf4j.bootstrap.TestBootstrapper;
import team.aura_dev.lib.multiplatformcore.testcode.slf4j.bootstrap.TestPluginBootstrap;

public class MultiProjectSLF4JBootstrapperTest {
  private static final String SLF4J_VERSION = "1.7.25";

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
        base.getDependencyClassLoader().excludedPackageNames,
        generator.getDependencyClassLoader().excludedPackageNames);
    assertEquals(
        base.getDependencyClassLoader().excludedPackageNames,
        direct.getDependencyClassLoader().excludedPackageNames);
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

  @Test
  public void manualLoadTest() throws IOException {
    final MultiProjectSLF4JBootstrapper<Object> bootstrapper =
        new MultiProjectSLF4JBootstrapper<Object>(Object.class) {};
    final Path libsDir = folder.newFolder("libsDir").toPath();

    bootstrapper.extractAndInjectSLF4JLib(libsDir, SLF4J_VERSION, "api");

    URL[] urls = bootstrapper.getDependencyClassLoader().getURLs();

    assertEquals(2, urls.length);
    assertUrlIsOk(urls[1]);
  }

  @Test
  public void manualDoubleLoadTest() throws IOException {
    final MultiProjectSLF4JBootstrapper<Object> bootstrapper =
        new MultiProjectSLF4JBootstrapper<Object>(Object.class) {};
    final Path libsDir = folder.newFolder("libsDir").toPath();

    bootstrapper.extractAndInjectSLF4JLib(libsDir, SLF4J_VERSION, "api");
    bootstrapper.extractAndInjectSLF4JLib(libsDir, SLF4J_VERSION, "api");

    URL[] urls = bootstrapper.getDependencyClassLoader().getURLs();

    // Will not get added twice
    assertEquals(2, urls.length);
    assertUrlIsOk(urls[1]);
  }

  @Test
  public void manualLoadNotFoundTest() throws IOException {
    final MultiProjectSLF4JBootstrapper<Object> bootstrapper =
        new MultiProjectSLF4JBootstrapper<Object>(Object.class) {};
    final Path libsDir = folder.newFolder("libsDir").toPath();

    try {
      bootstrapper.extractAndInjectSLF4JLib(libsDir, SLF4J_VERSION, "X");

      fail("Expected an exception to be thrown");
    } catch (IOException e) {
      assertEquals(
          "Resource \"org/slf4j/slf4j-X-" + SLF4J_VERSION + ".zip\" could not be found",
          e.getMessage());
    }
  }

  private static void assertUrlIsOk(URL url) {
    final String urlStr = url.toString();

    assertTrue(
        "expected URL to start with <file:/> but was:<" + urlStr + ">",
        urlStr.startsWith("file:/"));
    assertTrue(
        "expected URL to end with <libsDir/org/slf4j/slf4j-api/slf4j-api-"
            + SLF4J_VERSION
            + ".jar> but was:<"
            + urlStr
            + ">",
        urlStr.endsWith("libsDir/org/slf4j/slf4j-api/slf4j-api-" + SLF4J_VERSION + ".jar"));
  }
}
