package team.aura_dev.lib.multiplatformcore.dependency;

import eu.mikroskeem.picomaven.artifact.TransitiveDependencyProcessor;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;

/**
 * A simple {@link TransitiveDependencyProcessor} that excludes transitive dependencies that match a
 * certain criterion.
 *
 * @author Yannick Schinko
 */
@RequiredArgsConstructor
public class TransitivePatternExcluder implements TransitiveDependencyProcessor {
  private final String groupId;
  private final String artifactId;

  /**
   * A convince constructor that calls {@link #TransitivePatternExcluder(String, String)} by sending
   * the first and second element of the array to it, assuming it exists. Else it forwards null.<br>
   * Mainly useful for something like this {@code new
   * TransitivePatternExcluder("my.group:myartifact".split(":"))}
   *
   * @param splitList an array that contains the {@link #groupId} as the first element and the
   *     {@link #artifactId} as the second. If the array is too short the missing elements will be
   *     treated as null.
   * @see #TransitivePatternExcluder(String, String)
   */
  public TransitivePatternExcluder(String[] splitList) {
    this(
        (splitList.length >= 1) ? splitList[0] : null,
        (splitList.length >= 2) ? splitList[1] : null);
  }

  @Override
  public void accept(@Nonnull DownloadableTransitiveDependency dependency) {
    if ((groupId != null) && !groupId.equals(dependency.getGroupId())) return;
    if ((artifactId != null) && !artifactId.equals(dependency.getArtifactId())) return;

    // Both groupId and artifactId match so we don't want this transitive dependency
    dependency.setAllowed(false);
  }
}
