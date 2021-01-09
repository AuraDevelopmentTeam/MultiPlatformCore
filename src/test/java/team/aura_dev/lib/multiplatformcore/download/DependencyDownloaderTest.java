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
  public void downloadAndInjectCollectionTest() throws ClassNotFoundException {
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

  @Test
  public void downloadAndInjectDependencyListTest() throws ClassNotFoundException {
    final DependencyDownloader dependencyDownloader =
        new DependencyDownloader(dependencyClassLoader, libsDir);
    final DependencyList list = new DependencyList();

    list.addIfClassMissing(
        TestRuntimeDependencies.CONFIGURATE_HOCON, "ninja.leaping.configurate.ConfigurationNode");
    list.addIfClassMissing(
        TestRuntimeDependencies.CONFIGURATE_HOCON_MD5_MISMATCH, "java.lang.Object");

    dependencyDownloader.downloadAndInjectInClasspath(list);

    assertIsFile(
        libsDir.resolve("org/spongepowered/configurate-hocon/3.6.1/configurate-hocon-3.6.1.jar"));
    assertIsFile(
        libsDir.resolve("org/spongepowered/configurate-core/3.6.1/configurate-core-3.6.1.jar"));
    assertIsFile(libsDir.resolve("com/typesafe/config/1.3.1/config-1.3.1.jar"));

    assertCanLoadClass("ninja.leaping.configurate.ConfigurationNode");
    assertCanLoadClass("com.typesafe.config.Config");
  }

  @Test(expected = DependencyDownloadException.class)
  public void md5HashMismatchTest() {
    final DependencyDownloader dependencyDownloader =
        new DependencyDownloader(dependencyClassLoader, libsDir);

    dependencyDownloader.downloadAndInjectInClasspath(
        Collections.singleton(TestRuntimeDependencies.CONFIGURATE_HOCON_MD5_MISMATCH));
  }

  @Test(expected = DependencyDownloadException.class)
  public void sha1HashMismatchTest() {
    final DependencyDownloader dependencyDownloader =
        new DependencyDownloader(dependencyClassLoader, libsDir);

    dependencyDownloader.downloadAndInjectInClasspath(
        Collections.singleton(TestRuntimeDependencies.CONFIGURATE_HOCON_SHA1_MISMATCH));
  }

  @Test(expected = DependencyDownloadException.class)
  public void wrongArtifactIdTest() {
    final DependencyDownloader dependencyDownloader =
        new DependencyDownloader(dependencyClassLoader, libsDir);

    dependencyDownloader.downloadAndInjectInClasspath(
        Collections.singleton(TestRuntimeDependencies.CONFIGURATE_HOCON_WRONG_ARTIFACT_ID));
  }

  @Test(expected = DependencyDownloadException.class)
  public void wrongClassifierTest() {
    final DependencyDownloader dependencyDownloader =
        new DependencyDownloader(dependencyClassLoader, libsDir);

    dependencyDownloader.downloadAndInjectInClasspath(
        Collections.singleton(TestRuntimeDependencies.CONFIGURATE_HOCON_WRONG_CLASSIFIER));
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
