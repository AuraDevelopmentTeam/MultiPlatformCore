package team.aura_dev.lib.multiplatformcore.testcode;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import team.aura_dev.lib.multiplatformcore.testcode.api.TestPluginApi;

public class TestPluginBootstrap {
  // Only for testing. Normally the bootstrapper wouldn't be saved!
  @Getter private final TestBootstrapper bootstrapper;
  @Getter private final TestPluginApi bootstrappedPlugin;
  public final AtomicBoolean flag;

  public TestPluginBootstrap() {
    bootstrapper = new TestBootstrapper();
    bootstrapper.initializePlugin(this);

    bootstrappedPlugin = bootstrapper.getPlugin();
    flag = new AtomicBoolean(false);
  }

  public TestPluginBootstrap(AtomicBoolean callFlag) {
    bootstrapper = new TestBootstrapper();
    bootstrapper.initializePlugin(this, callFlag);

    bootstrappedPlugin = bootstrapper.getPlugin();
    // Not relevant for this test
    flag = null;
  }

  public TestPluginBootstrap(Throwable exception) {
    bootstrapper = new TestBootstrapper();
    bootstrapper.initializePlugin(this, null, exception);

    bootstrappedPlugin = bootstrapper.getPlugin();
    // Not relevant for this test
    flag = null;
  }

  public TestPluginBootstrap(String msg) {
    bootstrapper = new TestBootstrapper();
    bootstrapper.initializePlugin(this, null, msg);

    bootstrappedPlugin = bootstrapper.getPlugin();
    // Not relevant for this test
    flag = null;
  }

  public void testCall() {
    bootstrappedPlugin.testCall();
  }

  public void updateFlag() {
    bootstrappedPlugin.updateFlag();
  }

  public boolean updateBootstrapFlag() {
    bootstrappedPlugin.updateBootstrapFlag();

    return flag.get();
  }

  public void updateUtilityFlag() {
    bootstrappedPlugin.updateUtilityFlag();
  }

  public void exceptionTest(Throwable exception) throws Throwable {
    bootstrappedPlugin.exceptionTest(exception);
  }

  public void configurateTest(Path libsDir) {
    bootstrappedPlugin.configurateTest(libsDir);
  }

  public void configurateNoLoadTest(Path libsDir) {
    bootstrappedPlugin.configurateNoLoadTest(libsDir);
  }
}
