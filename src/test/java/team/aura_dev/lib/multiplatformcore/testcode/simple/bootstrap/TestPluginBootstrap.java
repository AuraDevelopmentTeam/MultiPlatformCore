package team.aura_dev.lib.multiplatformcore.testcode.simple.bootstrap;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import team.aura_dev.lib.multiplatformcore.testcode.simple.api.TestPluginApi;

public class TestPluginBootstrap {
  // Only for testing. Normally the bootstrapper wouldn't be saved!
  @Getter private final TestBootstrapper bootstrapper;
  @Getter private final TestPluginApi bootstrappedPlugin;
  public final AtomicBoolean flag;

  public TestPluginBootstrap() {
    bootstrapper = new TestBootstrapper();
    bootstrappedPlugin = bootstrapper.initializePlugin(this);

    flag = new AtomicBoolean(false);
  }

  public TestPluginBootstrap(AtomicBoolean callFlag) {
    bootstrapper = new TestBootstrapper();
    bootstrappedPlugin = bootstrapper.initializePlugin(this, callFlag);

    // Not relevant for this test
    flag = null;
  }

  public TestPluginBootstrap(Throwable exception) {
    bootstrapper = new TestBootstrapper();
    bootstrappedPlugin = bootstrapper.initializePlugin(this, null, exception);

    // Not relevant for this test
    flag = null;
  }

  public TestPluginBootstrap(String msg) {
    bootstrapper = new TestBootstrapper();
    bootstrappedPlugin = bootstrapper.initializePlugin(this, null, msg);

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

  public void apiInteraction() {
    bootstrappedPlugin.apiInteraction();
  }
}
