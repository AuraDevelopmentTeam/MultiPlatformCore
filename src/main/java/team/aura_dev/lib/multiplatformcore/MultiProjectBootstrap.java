package team.aura_dev.lib.multiplatformcore;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import lombok.Getter;

public abstract class MultiProjectBootstrap {
  @Getter protected final DependencyClassLoader dependencyClassLoader;

  protected Object plugin;
  protected Class<?> pluginClass;

  protected MultiProjectBootstrap() {
    dependencyClassLoader =
        AccessController.doPrivileged(
            (PrivilegedAction<DependencyClassLoader>)
                () -> new DependencyClassLoader(getPackageName(), getApiPackageName()));
  }

  protected MultiProjectBootstrap(
      PrivilegedAction<DependencyClassLoader> dependencyClassLoaderGenerator) {
    this(AccessController.doPrivileged(dependencyClassLoaderGenerator));
  }

  protected MultiProjectBootstrap(DependencyClassLoader dependencyClassLoader) {
    this.dependencyClassLoader = dependencyClassLoader;
  }

  protected String getPackageName() {
    return getClass().getPackage().getName();
  }

  protected String getApiPackageName() {
    return getPackageName() + ".api";
  }

  /**
   * Bootstraps the actual plugin class.
   *
   * @param bootstrapPlugin the instance of the bootstrap class. Used to determine the actual plugin
   *     name. Also gets prepended to the other parameters
   * @param params parameters forwarded to the plugin class constructor
   */
  public void initializePlugin(Object bootstrapPlugin, Object... params) {
    // Add plugin instance as first parameter
    final Object[] mergedParams = new Object[params.length + 2];
    mergedParams[0] = dependencyClassLoader;
    mergedParams[1] = bootstrapPlugin;
    System.arraycopy(params, 0, mergedParams, 2, params.length);

    plugin =
        initializePlugin(
            bootstrapPlugin.getClass().getName().replace("Bootstrap", ""), mergedParams);
    pluginClass = plugin.getClass();
  }

  protected void callMethod(String name) {
    callMethod(name, new Class<?>[0]);
  }

  protected void callMethod(String name, Class<?>[] types, Object... params) {
    try {
      pluginClass.getMethod(name, types).invoke(plugin, params);
    } catch (InvocationTargetException e) {
      // Properly unwrap the InvocationTargetException
      throw new IllegalStateException(
          "Calling " + name + " resulted in an exception", e.getTargetException());
    } catch (IllegalAccessException
        | IllegalArgumentException
        | NoSuchMethodException
        | SecurityException e) {
      throw new IllegalStateException("Calling " + name + " failed", e);
    }
  }

  private Object initializePlugin(String pluginClassName, Object... params) {
    try {
      final Class<?> pluginClass = dependencyClassLoader.loadClass(pluginClassName);
      // Checking if the parameter count matches is good enough of a way to find the matching
      // constructor in this case
      // 10/10 parameter matching
      // TODO: Improve matching
      final Constructor<?> constructor =
          Arrays.stream(pluginClass.getConstructors())
              .filter(con -> con.getParameterCount() == params.length)
              .findFirst()
              .orElseThrow(NoSuchMethodException::new);
      return constructor.newInstance(params);
    } catch (InvocationTargetException e) {
      // Properly unwrap the InvocationTargetException
      throw new IllegalStateException(
          "Loading the plugin class resulted in an exception ", e.getTargetException());
    } catch (Exception e) {
      // Catch all checked and unchecked exceptions
      throw new IllegalStateException("Loading the plugin class failed", e);
    }
  }
}
