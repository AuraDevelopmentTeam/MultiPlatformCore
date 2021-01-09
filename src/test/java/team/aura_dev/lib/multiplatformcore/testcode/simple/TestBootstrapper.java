package team.aura_dev.lib.multiplatformcore.testcode.simple;

import team.aura_dev.lib.multiplatformcore.MultiProjectBootstrapper;
import team.aura_dev.lib.multiplatformcore.testcode.simple.api.TestPluginApi;

public class TestBootstrapper extends MultiProjectBootstrapper<TestPluginApi> {
  public TestBootstrapper() {
    super(TestPluginApi.class);

    // Split sources in tests
    dependencyClassLoader.addURL(getClass().getProtectionDomain().getCodeSource().getLocation());
  }
}
