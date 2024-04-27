package team.aura_dev.lib.multiplatformcore.testcode.slf4j;

import lombok.RequiredArgsConstructor;
import team.aura_dev.lib.multiplatformcore.DependencyClassLoader;
import team.aura_dev.lib.multiplatformcore.testcode.slf4j.api.TestPluginApi;
import team.aura_dev.lib.multiplatformcore.testcode.slf4j.bootstrap.TestPluginBootstrap;

@RequiredArgsConstructor
public class TestPlugin implements TestPluginApi {
  private final DependencyClassLoader classLoader;
  private final TestPluginBootstrap plugin;

  public void testCall() {
    // Do Nothing
  }
}
