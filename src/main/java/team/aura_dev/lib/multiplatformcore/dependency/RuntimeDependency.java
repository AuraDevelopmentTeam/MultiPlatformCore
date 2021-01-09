package team.aura_dev.lib.multiplatformcore.dependency;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import eu.mikroskeem.picomaven.artifact.ArtifactChecksums;
import eu.mikroskeem.picomaven.artifact.Dependency;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.SneakyThrows;
import lombok.Value;

/**
 * This represents a dependency that can be downloaded at runtime.
 *
 * <p>Example code:
 *
 * <pre>{@code
 * public static final RuntimeDependency CONFIGURATE_HOCON =
 *     RuntimeDependency.builder(
 *             "org.spongepowered",
 *             "configurate-hocon",
 *             "3.6.1",
 *             "6395403afce7b9bbf4e26ef74c13da9a",
 *             "e3f199dbd91de753a70f63606f530fdb8644bbd5")
 *         .maven(RuntimeDependency.Maven.SPONGE)
 *         .transitive()
 *         .exclusion("com.google.code.findbugs:jsr305")
 *         .exclusion("com.google.errorprone:error_prone_annotations")
 *         .exclusion("com.google.j2objc:j2objc-annotations")
 *         .exclusion("org.codehaus.mojo:animal-sniffer-annotations")
 *         .build();
 * }</pre>
 *
 * @author Yannick Schinko
 */
@SuppressFBWarnings(
    value = {"JLM_JSR166_UTILCONCURRENT_MONITORENTER", "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"},
    justification = "Code is generated by lombok which means I don't have any influence on it.")
@Value
@Builder
public class RuntimeDependency {
  @Nonnull private final String groupId;
  @Nonnull private final String artifactId;
  @Nonnull private final String version;
  @Builder.Default private final String classifier = null;
  @Nonnull private final String md5Hash;
  @Nonnull private final String sha1Hash;
  @Nonnull @Builder.Default private final Maven maven = Maven.MAVEN_CENTRAL;
  @Builder.Default private final boolean transitive = false;
  @Nonnull @Singular private final List<String> exclusions;

  @Getter(lazy = true)
  private final Dependency dependency = generateDependency();

  @Getter(lazy = true)
  private final List<TransitivePatternExcluder> exclusionPatterns = generateExclusionPatterns();

  private Dependency generateDependency() {
    return new Dependency(
        groupId,
        artifactId,
        version,
        classifier,
        transitive,
        Arrays.asList(
            ArtifactChecksums.md5HexSumOf(md5Hash), ArtifactChecksums.sha1HexSumOf(sha1Hash)));
  }

  private List<TransitivePatternExcluder> generateExclusionPatterns() {
    return exclusions.stream()
        .map(exclusion -> exclusion.split(":", 3))
        .map(TransitivePatternExcluder::new)
        .collect(Collectors.toList());
  }
  /**
   * Creates a new builder with these required attributes already set.<br>
   * Both hashes are required as neither MD5, nor SHA1 are considered particularly secure. But
   * there's no known way to create a modified file that has both the same MD5 and SHA1 hash. We use
   * theses types of hashes in the first place because most mavens precalculate those two.
   *
   * @param groupId The group ID of the artifact
   * @param artifactId The artifact ID of the artifact
   * @param version The version of the artifact
   * @param md5Hash The md5 hash of the artifact file in HEX notation (should be 32 characters long)
   * @param sha1Hash The sha1 hash of the artifact file in HEX notation (should be 40 characters
   *     long)
   * @return A {@link RuntimeDependencyBuilder} that can either be turned into a {@link
   *     RuntimeDependency} right away or can be further modified like given a classifier or can be
   *     transitive.
   */
  public static RuntimeDependencyBuilder builder(
      @Nonnull String groupId,
      @Nonnull String artifactId,
      @Nonnull String version,
      @Nonnull String md5Hash,
      @Nonnull String sha1Hash) {
    return new RuntimeDependencyBuilder(groupId, artifactId, version, md5Hash, sha1Hash);
  }

  /**
   * Turns this {@link RuntimeDependency} object back into a builder so you can create another
   * {@link RuntimeDependency} object that shares many properties with this.
   *
   * @return A {@link RuntimeDependencyBuilder} that if built right away would return an equivalent
   *     {@link RuntimeDependency} object to this.
   */
  public RuntimeDependencyBuilder toBuilder() {
    return builder(groupId, artifactId, version, md5Hash, sha1Hash)
        .classifier(classifier)
        .exclusions(exclusions);
  }

  public static class RuntimeDependencyBuilder {
    RuntimeDependencyBuilder(
        @Nonnull String groupId,
        @Nonnull String artifactId,
        @Nonnull String version,
        @Nonnull String md5Hash,
        @Nonnull String sha1Hash) {
      this.groupId(groupId);
      this.artifactId(artifactId);
      this.version(version);
      this.md5Hash(md5Hash);
      this.sha1Hash(sha1Hash);
    }

    public RuntimeDependencyBuilder transitive() {
      transitive$value = true;
      transitive$set = true;

      return this;
    }
  }

  /**
   * This class represents a Maven repository this class can download from.
   *
   * <p>It already contains two predefined repositories:
   *
   * <ul>
   *   <li>Maven Central
   *   <li>Sponge
   * </ul>
   */
  @SuppressFBWarnings(
      value = "NP_NONNULL_RETURN_VIOLATION",
      justification = "It's actually guaranteed that it's never null")
  @RequiredArgsConstructor
  @Getter
  public static class Maven {
    public static final Maven MAVEN_CENTRAL = new Maven("https://repo1.maven.org/maven2");
    public static final Maven SPONGE = new Maven("https://repo.spongepowered.org/maven");

    @Nonnull private final String urlString;

    @Nonnull
    @Getter(lazy = true)
    private final URL url = generateUrl();

    @SneakyThrows(MalformedURLException.class)
    @Nonnull
    private URL generateUrl() {
      return new URL(urlString);
    }
  }
}
