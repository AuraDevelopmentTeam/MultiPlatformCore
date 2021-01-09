package team.aura_dev.lib.multiplatformcore.testcode;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;

public class TestBootstrapPlugin {
  @Getter private final BootstrapTest bootstrapPlugin;
  final AtomicBoolean flag;

  public TestBootstrapPlugin() {
    bootstrapPlugin = new BootstrapTest();
    bootstrapPlugin.initializePlugin(this);

    flag = new AtomicBoolean(false);
  }

  public TestBootstrapPlugin(AtomicBoolean callFlag) {
    bootstrapPlugin = new BootstrapTest();
    bootstrapPlugin.initializePlugin(this, callFlag);

    // Not relevant for this test
    flag = null;
  }

  public TestBootstrapPlugin(Throwable exception) {
    bootstrapPlugin = new BootstrapTest();
    bootstrapPlugin.initializePlugin(this, null, exception);

    // Not relevant for this test
    flag = null;
  }

  public TestBootstrapPlugin(String msg) {
    bootstrapPlugin = new BootstrapTest();
    // Tries to call a constructor that doesn't exist
    bootstrapPlugin.initializePlugin(this, null, msg);

    // Not relevant for this test
    flag = null;
  }

  public void testCall() {
    bootstrapPlugin.getPlugin().testCall();
  }

  public void updateFlag() {
    bootstrapPlugin.getPlugin().updateFlag();
  }

  public boolean updateBootstrapFlag() {
    bootstrapPlugin.getPlugin().updateBootstrapFlag();

    return flag.get();
  }

  public void exceptionTest(Throwable exception) throws Throwable {
    bootstrapPlugin.getPlugin().exceptionTest(exception);
  }
}
