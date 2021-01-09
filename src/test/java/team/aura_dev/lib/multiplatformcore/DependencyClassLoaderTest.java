package team.aura_dev.lib.multiplatformcore;

import static org.junit.Assert.assertEquals;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.junit.Test;

public class DependencyClassLoaderTest {
  @Test
  public void constructorTests() {
    final DependencyClassLoader one =
        AccessController.doPrivileged(
            (PrivilegedAction<DependencyClassLoader>) () -> new DependencyClassLoader("@group@"));
    final DependencyClassLoader two =
        AccessController.doPrivileged(
            (PrivilegedAction<DependencyClassLoader>)
                () -> new DependencyClassLoader(getClass().getClassLoader(), "@group@"));

    assertEquals("@group@.api.", one.apiPackageName);
    assertEquals("@group@.api.", two.apiPackageName);
  }
}
