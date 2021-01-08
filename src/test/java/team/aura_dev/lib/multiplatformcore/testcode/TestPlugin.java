package team.aura_dev.lib.multiplatformcore.testcode;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import team.aura_dev.lib.multiplatformcore.DependencyClassLoader;

@RequiredArgsConstructor
public class TestPlugin {
  private final DependencyClassLoader classLoader;
  private final TestBootstrapPlugin plugin;
  private final AtomicBoolean callFlag;

  public TestPlugin(DependencyClassLoader classLoader, TestBootstrapPlugin plugin) {
    this(classLoader, plugin, (AtomicBoolean) null);
  }

  // Adding the pointless extra constructor argument to allow the code to differentiate
  public TestPlugin(
      DependencyClassLoader classLoader,
      TestBootstrapPlugin plugin,
      AtomicBoolean callFlag,
      Throwable exception)
      throws Throwable {
    this(classLoader, plugin, callFlag);

    throw exception;
  }

  public void testCall() {
    // Do Nothing
  }

  public void updateFlag() {
    callFlag.set(true);
  }

  public void updateBootstrapFlag() {
    plugin.flag.set(true);
  }

  public void exceptionTest(Throwable exception) throws Throwable {
    throw exception;
  }
}
