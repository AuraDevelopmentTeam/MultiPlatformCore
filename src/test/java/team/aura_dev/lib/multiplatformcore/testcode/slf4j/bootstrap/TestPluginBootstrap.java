package team.aura_dev.lib.multiplatformcore.testcode.slf4j.bootstrap;

import java.nio.file.Path;
import lombok.Getter;
import team.aura_dev.lib.multiplatformcore.testcode.slf4j.api.TestPluginApi;

public class TestPluginBootstrap {
  @Getter private final TestBootstrapper bootstrapper;
  @Getter private final TestPluginApi bootstrappedPlugin;

  public TestPluginBootstrap(Path libsDir) {
    bootstrapper = new TestBootstrapper();
    // Is loaded in the test environment. Not much to do. Will never load anything in the first
    // place
    bootstrapper.checkAndLoadSLF4J(libsDir, "");
    bootstrappedPlugin = bootstrapper.initializePlugin(this);
  }

  public void testCall() {
    bootstrappedPlugin.testCall();
  }
}
