package team.aura_dev.lib.multiplatformcore.testcode.api;

public interface TestPluginApi {
  public void testCall();

  public void updateFlag();

  public void updateBootstrapFlag();

  public void exceptionTest(Throwable exception) throws Throwable;
}
