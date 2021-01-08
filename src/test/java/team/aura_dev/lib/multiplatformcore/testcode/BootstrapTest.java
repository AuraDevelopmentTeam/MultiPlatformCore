package team.aura_dev.lib.multiplatformcore.testcode;

import team.aura_dev.lib.multiplatformcore.MultiProjectBootstrap;

public class BootstrapTest extends MultiProjectBootstrap {
  public void testCall() {
    callMethod("testCall");
  }

  public void updateFlag() {
    callMethod("updateFlag");
  }

  public void updateBootstrapFlag() {
    callMethod("updateBootstrapFlag");
  }

  public void exceptionTest(Throwable exception) {
    callMethod("exceptionTest", new Class[] {Throwable.class}, exception);
  }

  public void noSuchMethodTest() {
    callMethod("noSuchMethodTest");
  }
}
