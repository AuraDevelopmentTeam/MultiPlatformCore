package team.aura_dev.lib.multiplatformcore.download;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import team.aura_dev.lib.multiplatformcore.DependencyClassLoader;

public class DependencyDownloaderTest {
  private static final DependencyClassLoader dependencyClassLoader =
      AccessController.doPrivileged(
          (PrivilegedAction<DependencyClassLoader>)
              () -> new DependencyClassLoader("@group@.testcode"));

  @Rule public TemporaryFolder folder = new TemporaryFolder();
  private Path libsDir;

  @Before
  public void getLibsDir() throws IOException {
    libsDir = folder.newFolder("libsDir").toPath();
  }

  @Test
  public void downloadAndInjectTest() throws ClassNotFoundException {
    final DependencyDownloader dependencyDownloader =
        new DependencyDownloader(dependencyClassLoader, libsDir);

    dependencyDownloader.downloadAndInjectInClasspath(
        Collections.singleton(TestRuntimeDependencies.CONFIGURATE_HOCON));

    assertIsFile(
        libsDir.resolve("org/spongepowered/configurate-hocon/3.6.1/configurate-hocon-3.6.1.jar"));
    assertIsFile(
        libsDir.resolve("org/spongepowered/configurate-core/3.6.1/configurate-core-3.6.1.jar"));
    assertIsFile(libsDir.resolve("com/typesafe/config/1.3.1/config-1.3.1.jar"));

    assertCanLoadClass("ninja.leaping.configurate.ConfigurationNode");
    assertCanLoadClass("com.typesafe.config.Config");
  }

  private static void assertCanLoadClass(String className) throws ClassNotFoundException {
    final Class<?> loadedClass = dependencyClassLoader.loadClass(className);

    assertEquals(className, loadedClass.getName());
  }

  private static void assertIsFile(Path path) {
    final File file = path.toFile();

    assertTrue(path.toString() + " doesn't exist", file.exists());
    assertTrue(path.toString() + " ist not a file", file.isFile());
  }
}
