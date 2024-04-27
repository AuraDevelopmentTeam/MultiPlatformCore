package team.aura_dev.lib.multiplatformcore.testcode.slf4j.bootstrap;

import team.aura_dev.lib.multiplatformcore.bootstrap.MultiProjectSLF4JBootstrapper;
import team.aura_dev.lib.multiplatformcore.testcode.slf4j.TestPluginApi;

public class TestBootstrapper extends MultiProjectSLF4JBootstrapper<TestPluginApi> {
  public TestBootstrapper() {
    super(TestPluginApi.class);

    // Split sources in tests
    dependencyClassLoader.addURL(getClass().getProtectionDomain().getCodeSource().getLocation());
  }
}
