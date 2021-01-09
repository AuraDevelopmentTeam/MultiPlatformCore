package team.aura_dev.lib.multiplatformcore.testcode.slf4j;

import team.aura_dev.lib.multiplatformcore.MultiProjectSLF4JBootstrapper;

public class TestBootstrapper extends MultiProjectSLF4JBootstrapper<TestPluginApi> {
  public TestBootstrapper() {
    super(TestPluginApi.class);

    // Split sources in tests
    dependencyClassLoader.addURL(getClass().getProtectionDomain().getCodeSource().getLocation());
  }
}
