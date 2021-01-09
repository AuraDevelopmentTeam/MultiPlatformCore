package team.aura_dev.lib.multiplatformcore.testcode.simple;

import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import team.aura_dev.lib.multiplatformcore.DependencyClassLoader;
import team.aura_dev.lib.multiplatformcore.download.DependencyDownloader;
import team.aura_dev.lib.multiplatformcore.download.TestRuntimeDependencies;
import team.aura_dev.lib.multiplatformcore.testcode.simple.api.TestPluginApi;
import team.aura_dev.lib.multiplatformcore.testcode.simple.example.ConfigurateTest;
import team.aura_dev.lib.multiplatformcore.testcode.simple.example.ExampleUtility;

@RequiredArgsConstructor
public class TestPlugin implements TestPluginApi {
  private final DependencyClassLoader classLoader;
  private final TestPluginBootstrap plugin;
  private final AtomicBoolean callFlag;

  public TestPlugin(DependencyClassLoader classLoader, TestPluginBootstrap plugin) {
    this(classLoader, plugin, (AtomicBoolean) null);
  }

  // Adding the pointless extra constructor argument to allow the code to differentiate
  public TestPlugin(
      DependencyClassLoader classLoader,
      TestPluginBootstrap plugin,
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

  @Override
  public void updateUtilityFlag() {
    ExampleUtility.setFlag(callFlag);
  }

  public void exceptionTest(Throwable exception) throws Throwable {
    throw exception;
  }

  @Override
  public void configurateTest(Path libsDir) {
    new DependencyDownloader(classLoader, libsDir)
        .downloadAndInjectInClasspath(
            Collections.singleton(TestRuntimeDependencies.CONFIGURATE_HOCON));
    ConfigurateTest.configurateTest();
  }

  @Override
  public void configurateNoLoadTest(Path libsDir) {
    // Explicitly not loading the dependencies here to force an error
    ConfigurateTest.configurateTest();
  }
}
