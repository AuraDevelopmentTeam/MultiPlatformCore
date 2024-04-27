package team.aura_dev.lib.multiplatformcore.testcode.simple.bootstrap;

import team.aura_dev.lib.multiplatformcore.bootstrap.MultiProjectBootstrapper;

public class TestBadBootstrapper extends MultiProjectBootstrapper<String> {
  public TestBadBootstrapper() {
    super(String.class);

    // Split sources in tests
    dependencyClassLoader.addURL(getClass().getProtectionDomain().getCodeSource().getLocation());
  }
}
