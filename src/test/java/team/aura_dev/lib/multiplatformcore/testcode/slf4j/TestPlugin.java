package team.aura_dev.lib.multiplatformcore.testcode.slf4j;

import lombok.RequiredArgsConstructor;
import team.aura_dev.lib.multiplatformcore.DependencyClassLoader;

@RequiredArgsConstructor
public class TestPlugin implements TestPluginApi {
  private final DependencyClassLoader classLoader;
  private final TestPluginBootstrap plugin;

  public void testCall() {
    // Do Nothing
  }
}
