package team.aura_dev.lib.multiplatformcore.testcode.simple.bootstrap;

import team.aura_dev.lib.multiplatformcore.bootstrap.MultiProjectBootstrapper;
import team.aura_dev.lib.multiplatformcore.testcode.simple.api.TestPluginApi;

public class TestBootstrapper extends MultiProjectBootstrapper<TestPluginApi> {
  public TestBootstrapper() {
    super(TestPluginApi.class);

    // Split sources in tests
    dependencyClassLoader.addURL(getClass().getProtectionDomain().getCodeSource().getLocation());
  }
}
