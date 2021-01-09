package team.aura_dev.lib.multiplatformcore;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivilegedAction;

/**
 * @param <T>
 * @author Yannick Schinko
 */
public abstract class MultiProjectSLF4JBootstrapper<T> extends MultiProjectBootstrapper<T> {
  protected MultiProjectSLF4JBootstrapper(Class<T> pluginBaseClass) {
    super(pluginBaseClass);
  }

  protected MultiProjectSLF4JBootstrapper(
      Class<T> pluginBaseClass,
      PrivilegedAction<DependencyClassLoader> dependencyClassLoaderGenerator) {
    super(pluginBaseClass, dependencyClassLoaderGenerator);
  }

  protected MultiProjectSLF4JBootstrapper(
      Class<T> pluginBaseClass, DependencyClassLoader dependencyClassLoader) {
    super(pluginBaseClass, dependencyClassLoader);
  }

  /**
   * Checks if SLF4J is present and loads it if not.<br>
   * {@code slf4jVersion} defaults to @slf4jVersion@
   *
   * @param libsPath Where to unpack the SLF4J files to
   * @param pluginName Which plugin to use. The file name is {@code
   *     slf4j-${pluginName}-${slf4jVersion}.zip}
   * @see #checkAndLoadSLF4J(Path, String, String)
   */
  public void checkAndLoadSLF4J(Path libsPath, String pluginName) {
    checkAndLoadSLF4J(libsPath, "@slf4jVersion@", pluginName);
  }

  /**
   * Checks if SLF4J is present and loads it if not.
   *
   * @param libsPath Where to unpack the SLF4J files to
   * @param slf4jVersion Which slf4j version to use
   * @param pluginName Which plugin to use. The file name is {@code
   *     slf4j-${pluginName}-${slf4jVersion}.zip}
   */
  public void checkAndLoadSLF4J(Path libsPath, String slf4jVersion, String pluginName) {
    try {
      Class.forName("org.slf4j.impl.StaticLoggerBinder");

      // Class is present, we don't need to load SLF4J
      return;
    } catch (ClassNotFoundException e) {
      // Ignore and continue. We need to load SLF4J
    }

    try {
      extractAndInjectSLF4JLib(libsPath, slf4jVersion, "api");
      extractAndInjectSLF4JLib(libsPath, slf4jVersion, pluginName);
    } catch (IOException e) {
      throw new IllegalStateException("Unexpected IOException while trying to load SLF4J", e);
    }
  }

  /**
   * Extracts the specified SLF4J zip file from the current jar, saves it in the {@code libsPath}
   * and then injects in the {@link #dependencyClassLoader}.<br>
   * Specifically this loads the resource {@code org/slf4j/slf4j-${libName}.zip} and saves it to
   * {@code ${libsPath}/org/slf4j/${libName}/slf4j-${libName}.jar}. Then it injects it into the
   * {@link #dependencyClassLoader}.
   *
   * @param libsPath Where to unpack the SLF4J file to
   * @param slf4jVersion Which slf4j version to use
   * @param libName Which library to use. The file name is "slf4j-&lt;libName&gt;.zip"
   * @throws IOException when writing or reading fails. Several actions on the file system may
   *     trigger this.
   */
  @SuppressFBWarnings(
      value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
      justification = "We're never getting null here")
  protected void extractAndInjectSLF4JLib(Path libsPath, String slf4jVersion, String libName)
      throws IOException {
    final Path outFile =
        libsPath.resolve(
            "org/slf4j/" + libName + "/slf4j-" + libName + "-" + slf4jVersion + ".jar");
    Files.createDirectories(outFile.getParent());

    final String resourceName = "org/slf4j/slf4j-" + libName + "-" + slf4jVersion + ".zip";

    if (!Files.exists(outFile)) {
      try (InputStream libStream = dependencyClassLoader.getResourceAsStream(resourceName)) {
        if (libStream == null)
          throw new IOException("Resource \"" + resourceName + "\" could not be found");

        Files.copy(libStream, outFile);
      }
    }

    dependencyClassLoader.addURL(outFile.toUri().toURL());
  }
}
