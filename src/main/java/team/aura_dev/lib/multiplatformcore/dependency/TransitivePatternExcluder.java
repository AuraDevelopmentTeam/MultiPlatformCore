package team.aura_dev.lib.multiplatformcore.dependency;

import edu.umd.cs.findbugs.annotations.NonNull;
import eu.mikroskeem.picomaven.artifact.TransitiveDependencyProcessor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransitivePatternExcluder implements TransitiveDependencyProcessor {
  private final String groupId;
  private final String artifactId;

  public TransitivePatternExcluder(String[] splitList) {
    this(
        (splitList.length >= 1) ? splitList[0] : null,
        (splitList.length >= 2) ? splitList[1] : null);
  }

  @Override
  public void accept(@NonNull DownloadableTransitiveDependency dependency) {
    if ((groupId != null) && !groupId.equals(dependency.getGroupId())) return;
    if ((artifactId != null) && !artifactId.equals(dependency.getArtifactId())) return;

    // Both groupId and artifactId match so we don't want this transitive dependency
    dependency.setAllowed(false);
  }
}
