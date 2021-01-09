package team.aura_dev.lib.multiplatformcore.testcode.slf4j;

import java.nio.file.Path;
import lombok.Getter;

public class TestPluginBootstrap {
  @Getter private final TestBootstrapper bootstrapper;
  @Getter private final TestPluginApi bootstrappedPlugin;

  public TestPluginBootstrap(Path libsDir) {
    bootstrapper = new TestBootstrapper();
    // Is loaded in the test environment. Not much to do. Will never load anything in the first
    // place
    bootstrapper.checkAndLoadSLF4J(libsDir, "");
    bootstrapper.initializePlugin(this);

    bootstrappedPlugin = bootstrapper.getPlugin();
  }

  public void testCall() {
    bootstrappedPlugin.testCall();
  }
}
