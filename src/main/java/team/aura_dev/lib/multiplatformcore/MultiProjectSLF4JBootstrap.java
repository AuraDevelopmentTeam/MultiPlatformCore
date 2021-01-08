package team.aura_dev.lib.multiplatformcore;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivilegedAction;

public abstract class MultiProjectSLF4JBootstrap extends MultiProjectBootstrap {
  protected MultiProjectSLF4JBootstrap() {
    super();
  }

  protected MultiProjectSLF4JBootstrap(
      PrivilegedAction<DependencyClassLoader> dependencyClassLoaderGenerator) {
    super(dependencyClassLoaderGenerator);
  }

  protected MultiProjectSLF4JBootstrap(DependencyClassLoader dependencyClassLoader) {
    super(dependencyClassLoader);
  }

  /**
   * Checks if SLF4J is present and loads it if not.
   *
   * @param libsPath Where to unpack the jar files to
   * @param slf4jVersion Which slf4j version to use
   * @param version Which version of the slf4j-plugin-xxx to use
   */
  public void checkAndLoadSLF4J(Path libsPath, String slf4jVersion, String version) {
    try {
      Class.forName("org.slf4j.impl.StaticLoggerBinder");

      // Class is present, we don't need to load SLF4J
      return;
    } catch (ClassNotFoundException e) {
      // Ignore and continue. We need to load SLF4J
    }

    try {
      extractAndInjectSLF4JLib(libsPath, slf4jVersion, "api");
      extractAndInjectSLF4JLib(libsPath, slf4jVersion, "plugin-" + version + "-" + slf4jVersion);
    } catch (IOException e) {
      throw new IllegalStateException("Unexpected IOException while trying to load SLF4J", e);
    }
  }

  @SuppressFBWarnings(
      value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
      justification = "We're never getting null here")
  protected void extractAndInjectSLF4JLib(Path libsPath, String slf4jVersion, String libName)
      throws IOException {
    Path outFile =
        libsPath.resolve(
            "org/slf4j/" + libName + "/slf4j-" + libName + "-" + slf4jVersion + ".jar");
    Files.createDirectories(outFile.getParent());

    if (!Files.exists(outFile)) {
      try (InputStream libStream =
          dependencyClassLoader.getResourceAsStream(
              "org/slf4j/slf4j-" + libName + "-" + slf4jVersion + ".zip")) {
        Files.copy(libStream, outFile);
      }
    }

    dependencyClassLoader.addURL(outFile.toUri().toURL());
  }
}
