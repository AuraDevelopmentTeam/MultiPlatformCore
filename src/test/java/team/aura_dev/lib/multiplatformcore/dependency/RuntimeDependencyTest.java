package team.aura_dev.lib.multiplatformcore.dependency;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Test;
import team.aura_dev.lib.multiplatformcore.dependency.RuntimeDependency.Maven;

public class RuntimeDependencyTest {
  public static class MavenTest {
    @Test
    public void getUrlTest() throws MalformedURLException {
      final Maven maven = new Maven("https://google.com/test");

      assertEquals(new URL("https://google.com/test"), maven.getUrl());
    }

    @Test(expected = MalformedURLException.class)
    public void malformedUrlTest() throws MalformedURLException {
      final Maven maven = new Maven("xxx://xxx");

      maven.getUrl();
    }
  }
}
