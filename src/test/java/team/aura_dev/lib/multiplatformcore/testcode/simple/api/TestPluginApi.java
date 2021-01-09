package team.aura_dev.lib.multiplatformcore.testcode.simple.api;

import java.nio.file.Path;

public interface TestPluginApi {
  public void testCall();

  public void updateFlag();

  public void updateBootstrapFlag();

  public void updateUtilityFlag();

  public void exceptionTest(Throwable exception) throws Throwable;

  public void configurateTest(Path libsDir);

  public void configurateNoLoadTest(Path libsDir);

  public void apiInteraction();
}
