package team.aura_dev.lib.multiplatformcore.dependency;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import eu.mikroskeem.picomaven.DownloadResult;
import eu.mikroskeem.picomaven.PicoMaven;
import eu.mikroskeem.picomaven.artifact.Dependency;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import team.aura_dev.auraban.platform.common.AuraBanBaseBootstrap;

// TODO: Logging!
@UtilityClass
public class DependencyDownloader {
  private static final DependencyClassLoader classLoader =
      AuraBanBaseBootstrap.getDependencyClassLoader();

  @SuppressFBWarnings(
      value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
      justification = "SpotBugs is incorrect in this case")
  public static void downloadAndInjectInClasspath(
      Collection<RuntimeDependency> dependencies, Path libPath) {
    try {
      Files.createDirectories(libPath);
    } catch (IOException e) {
      throw new IllegalStateException("Can't create the library dirs", e);
    }

    PicoMaven.Builder picoMavenBase =
        new PicoMaven.Builder()
            .withDownloadPath(libPath)
            .withRepositoryURLs(
                Stream.concat(
                        Stream.of(RuntimeDependency.Maven.MAVEN_CENTRAL),
                        dependencies.stream().map(RuntimeDependency::getMaven))
                    .distinct()
                    .map(RuntimeDependency.Maven::getUrl)
                    .collect(Collectors.toList()))
            .withDependencies(
                dependencies.stream()
                    .map(RuntimeDependency::getDependency)
                    .collect(Collectors.toList()))
            .withTransitiveDependencyProcessors(
                dependencies.stream()
                    .map(RuntimeDependency::getExclusionPatterns)
                    .flatMap(List::stream)
                    .collect(Collectors.toList()));

    try (PicoMaven picoMaven = picoMavenBase.build()) {
      List<DownloadResult> downloads =
          picoMaven
              .downloadAllArtifacts()
              .values()
              .parallelStream()
              .flatMap(DependencyDownloader::processDownload)
              .peek(DependencyDownloader::checkDownload)
              .collect(Collectors.toList());

      downloads.stream()
          .map(DownloadResult::getAllDownloadedFiles)
          .flatMap(List::stream)
          .forEach(DependencyDownloader::injectInClasspath);
    }
  }

  private static Stream<DownloadResult> processDownload(Future<DownloadResult> future) {
    try {
      final DownloadResult result = future.get();

      final List<DownloadResult> allDownloads =
          new LinkedList<>(result.getTransitiveDependencies());
      allDownloads.add(0, result);

      return allDownloads.stream();
    } catch (InterruptedException | ExecutionException e) {
      // Rethrow because we rely on this working
      throw new DependencyDownloadException("Error while trying to download a dependency", e);
    }
  }

  private static void checkDownload(DownloadResult result) {
    if (!result.isSuccess()) {
      throw new DependencyDownloadException(
          "Downloading the dependency " + getDependencyName(result.getDependency()) + " failed",
          result.getDownloadException());
    }
  }

  private static void injectInClasspath(Path jarFile) {
    try {
      URL jarFileUrl = new URL("jar", "", "file:" + jarFile.toAbsolutePath().toString() + "!/");

      classLoader.addURL(jarFileUrl);
    } catch (MalformedURLException | IllegalArgumentException e) {
      // Rethrow because we rely on this working
      throw new DependencyDownloadException(
          "Error while trying to inject a dependency in the classloader", e);
    }
  }

  private static String getDependencyName(Dependency dependency) {
    return dependency.getGroupId()
        + ':'
        + dependency.getArtifactId()
        + ':'
        + dependency.getVersion();
  }
}
