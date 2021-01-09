package team.aura_dev.lib.multiplatformcore;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import lombok.Getter;

/**
 * This class is more or less the entry point into the {@link ClassLoader} magic. Creating the
 * {@link ClassLoader} and constructing the actual instance of your plugin happens all here.<br>
 * If something goes wrong with the {@link ClassLoader} itself you probably fix it in your child
 * class of this.
 *
 * @param <T> the base type of the plugin to load. Must not be the class of the plugin itself.<br>
 *     Ideally it's a minimal interface that only contains the calls the bootstrap plugin needs to
 *     call.
 * @author Yannick Schinko
 */
public abstract class MultiProjectBootstrapper<T> {
  @Getter protected final Class<T> pluginBaseClass;
  @Getter protected final DependencyClassLoader dependencyClassLoader;

  @Getter protected T plugin;
  @Getter protected Class<? extends T> pluginClass;

  /**
   * Constructs a {@link MultiProjectBootstrapper} and initializes the {@link DependencyClassLoader}
   * with the values from {@link #getPackageName()} and {@link #getApiPackageName()}.
   *
   * @param pluginBaseClass The plugin base class. After the plugin instance has been created is
   *     checked if it can be cast to this class.
   */
  protected MultiProjectBootstrapper(Class<T> pluginBaseClass) {
    this.pluginBaseClass = pluginBaseClass;
    this.dependencyClassLoader =
        AccessController.doPrivileged(
            (PrivilegedAction<DependencyClassLoader>)
                () -> new DependencyClassLoader(getPackageName(), getApiPackageName()));
  }

  /**
   * Constructs a {@link MultiProjectBootstrapper} and initializes the {@link DependencyClassLoader}
   * with the {@code dependencyClassLoaderGenerator}.
   *
   * @param pluginBaseClass The plugin base class. After the plugin instance has been created is
   *     checked if it can be cast to this class.
   * @param dependencyClassLoaderGenerator The {@link PrivilegedAction} the {@link
   *     DependencyClassLoader} will be generated from.
   */
  protected MultiProjectBootstrapper(
      Class<T> pluginBaseClass,
      PrivilegedAction<DependencyClassLoader> dependencyClassLoaderGenerator) {
    this(pluginBaseClass, AccessController.doPrivileged(dependencyClassLoaderGenerator));
  }

  /**
   * Constructs a {@link MultiProjectBootstrapper} and initializes the {@link DependencyClassLoader}
   * with {@code dependencyClassLoader}.
   *
   * @param pluginBaseClass The plugin base class. After the plugin instance has been created is
   *     checked if it can be cast to this class.
   * @param dependencyClassLoader The {@link DependencyClassLoader} instance to use.
   */
  protected MultiProjectBootstrapper(
      Class<T> pluginBaseClass, DependencyClassLoader dependencyClassLoader) {
    this.pluginBaseClass = pluginBaseClass;
    this.dependencyClassLoader = dependencyClassLoader;
  }

  /**
   * Return the package name. This is passed to the {@link DependencyClassLoader} if you call {@link
   * #MultiProjectBootstrapper(Class)}. If you call any of the other two constructors this method is
   * ignored.
   *
   * @return the package name of this class.<br>
   *     <i>Note:</i> {@code this.getClass()}, not {@code MultiProjectBootstrapper.class}. So if
   *     your implementing class is in the correct package, you don't need to override this method.
   *     Else it is a good idea to do so!
   */
  protected String getPackageName() {
    return getClass().getPackage().getName();
  }

  /**
   * Return the package name of the API package. This is passed to the {@link DependencyClassLoader}
   * if you call {@link #MultiProjectBootstrapper(Class)}. If you call any of the other two
   * constructors this method is ignored.
   *
   * @return the package name of this class with {@code .api} appended.<br>
   *     <i>Note:</i> {@code this.getClass()}, not {@code MultiProjectBootstrapper.class}. So if
   *     your API package is called {@code api} and sits in the same package as {@link
   *     #getPackageName()} returns, you don't need to override this method. Else it is a good idea
   *     to do so!
   * @see #getPackageName()
   */
  protected String getApiPackageName() {
    return getPackageName() + ".api";
  }

  /**
   * Bootstraps the actual plugin class.
   *
   * @param bootstrapPlugin the instance of the bootstrap class. Used to determine the actual plugin
   *     name. Also gets prepended to the other parameters
   * @param params parameters forwarded to the plugin class constructor
   * @return the instance of the freshly bootstrapped plugin
   * @throws IllegalStateException when the bootstrapped plugin is not of type {@link
   *     #pluginBaseClass} (as passed as the first constructor argument).
   * @throws IllegalStateException when calling the constructor caused an exception. The underlying
   *     exception is passed saved in the cause of this exception.
   */
  @SuppressWarnings("unchecked")
  public T initializePlugin(Object bootstrapPlugin, Object... params) {
    // Add plugin instance as first parameter
    final Object[] mergedParams = new Object[params.length + 2];
    mergedParams[0] = dependencyClassLoader;
    mergedParams[1] = bootstrapPlugin;
    System.arraycopy(params, 0, mergedParams, 2, params.length);

    final Object tempPlugin =
        initializePlugin(
            bootstrapPlugin.getClass().getName().replace("Bootstrap", ""), mergedParams);

    if (!pluginBaseClass.isInstance(tempPlugin)) {
      throw new IllegalStateException(
          "The loaded plugin instance is of type \""
              + tempPlugin.getClass().getName()
              + "\" and cannot be cast to the plugin base class \""
              + pluginBaseClass.getName()
              + "\".");
    }

    // "Unchecked" casts are here. But we literally just checked them above. So all is fine
    plugin = (T) tempPlugin;
    pluginClass = (Class<? extends T>) plugin.getClass();

    return plugin;
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
