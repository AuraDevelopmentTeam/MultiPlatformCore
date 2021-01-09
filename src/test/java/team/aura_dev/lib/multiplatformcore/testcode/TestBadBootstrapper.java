package team.aura_dev.lib.multiplatformcore.testcode;

import team.aura_dev.lib.multiplatformcore.MultiProjectBootstrapper;

public class TestBadBootstrapper extends MultiProjectBootstrapper<String> {
  public TestBadBootstrapper() {
    super(String.class);

    // Split sources in tests
    dependencyClassLoader.addURL(getClass().getProtectionDomain().getCodeSource().getLocation());
  }
}
