package team.aura_dev.lib.multiplatformcore.dependency;

import static org.junit.Assert.assertEquals;

import eu.mikroskeem.picomaven.artifact.TransitiveDependencyProcessor.DownloadableTransitiveDependency;
import org.junit.Test;

public class TransitivePatternExcluderTest {
  @Test
  public void matchingTest() {
    assertMatches(true, null, null);
    assertMatches(true, "x", null);
    assertMatches(false, "y", null);
    assertMatches(true, null, "x");
    assertMatches(false, null, "y");
    assertMatches(true, "x", "x");
    assertMatches(false, "y", "x");
    assertMatches(false, "x", "y");
    assertMatches(false, "y", "y");
  }

  @Test
  public void arrayConstructorTest() {
    assertMatches(true, new TransitivePatternExcluder(new String[] {}));
    assertMatches(true, new TransitivePatternExcluder(new String[] {"x"}));
    assertMatches(false, new TransitivePatternExcluder(new String[] {"y"}));
    assertMatches(true, new TransitivePatternExcluder(new String[] {"x", "x"}));
    assertMatches(false, new TransitivePatternExcluder(new String[] {"y", "x"}));
    assertMatches(false, new TransitivePatternExcluder(new String[] {"x", "y"}));
    assertMatches(false, new TransitivePatternExcluder(new String[] {"y", "y"}));
  }

  private static void assertMatches(boolean match, String groupId, String artifactId) {
    assertMatches(match, new TransitivePatternExcluder(groupId, artifactId));
  }

  private static void assertMatches(boolean match, TransitivePatternExcluder patternExcluder) {
    final DownloadableTransitiveDependency dependency =
        new DownloadableTransitiveDependency(null, "x", "x", null, null, null, true);

    patternExcluder.accept(dependency);

    // If it matches it's no longer allowed
    assertEquals(match, !dependency.isAllowed());
  }
}
