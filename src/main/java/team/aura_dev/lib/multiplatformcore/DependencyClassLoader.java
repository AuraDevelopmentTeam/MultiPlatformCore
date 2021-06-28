package team.aura_dev.lib.multiplatformcore;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * A custom {@link ClassLoader} implementation that allows adding {@link URL}s during runtime.
 *
 * <p>This class also ensures that {@link Class}es found in this ClassLoader's URL list are
 * considered first when loading a Class. This means that even if the parent ClassLoader has a Class
 * loaded with the exact same name this ClassLoader will load it again. This allows the user of this
 * Class to make sure that the URLs added will be always used. Which in consequence makes Classes
 * using this ClassLoader sandboxed from the rest of the runtime (regarding URLs added to this
 * ClassLoader). Very useful to ensure versions of libraries are exactly the ones added and no
 * conflicts with other plugins that add the same libraries to their jars.
 *
 * @author Yannick Schinko
 */
public class DependencyClassLoader extends URLClassLoader {
  private static final Method findLoadedClassMethod = getFindLoadedClassMethod();

  protected final ClassLoader parent;
  protected final String ownClassName;
  protected final String packageName;
  protected final String apiPackageName;

  /**
   * Constructor that automatically detects the parent {@link ClassLoader} by using its own {@link
   * ClassLoader}.<br>
   * Generates the {@code apiPackageName} by appending {@code ".api"}
   *
   * @param packageName The package base name all the plugin's classes are located in.<br>
   *     This is important because we must never load these classes ourselves if the parent {@link
   *     ClassLoader} has them loaded.
   * @see #DependencyClassLoader(String, String)
   */
  public DependencyClassLoader(String packageName) {
    this(packageName, packageName + ".api");
  }

  /**
   * Constructor that automatically detects the parent {@link ClassLoader} by using its own {@link
   * ClassLoader}.
   *
   * @param packageName The package base name all the plugin's classes are located in.<br>
   *     This is important because we must never load these classes ourselves if the parent {@link
   *     ClassLoader} has them loaded.
   * @param apiPackageName The package base name all the plugin's API classes are located in.<br>
   *     This is important because we must never load these classes ourselves no matter what.
   */
  public DependencyClassLoader(String packageName, String apiPackageName) {
    this(DependencyClassLoader.class.getClassLoader(), packageName, apiPackageName);
  }

  /**
   * Constructor that allows you to specify the parent {@link ClassLoader} you want to use.<br>
   * Generates the {@code apiPackageName} by appending {@code ".api"}
   *
   * @param parent parent {@link ClassLoader} to be used if a {@link Class} cannot be found in the
   *     own {@link URL}s.
   * @param packageName The package base name all the plugin's classes are located in.<br>
   *     This is important because we must never load these classes ourselves if the parent {@link
   *     ClassLoader} has them loaded.
   * @see #DependencyClassLoader(ClassLoader, String, String)
   */
  public DependencyClassLoader(ClassLoader parent, String packageName) {
    this(parent, packageName, packageName + ".api");
  }

  /**
   * Constructor that allows you to specify the parent {@link ClassLoader} you want to use.
   *
   * @param parent parent {@link ClassLoader} to be used if a {@link Class} cannot be found in the
   *     own {@link URL}s.
   * @param packageName The package base name all the plugin's classes are located in.<br>
   *     This is important because we must never load these classes ourselves if the parent {@link
   *     ClassLoader} has them loaded.
   * @param apiPackageName The package base name all the plugin's API classes are located in.<br>
   *     This is important because we must never load these classes ourselves no matter what.
   */
  public DependencyClassLoader(ClassLoader parent, String packageName, String apiPackageName) {
    // Start off with adding its own jar URL
    super(getOwnJarURL(), parent);

    this.ownClassName = DependencyClassLoader.class.getName();
    this.parent = parent;
    this.packageName = packageName + '.';
    this.apiPackageName = apiPackageName + '.';
  }

  @Override
  public void addURL(URL url) {
    super.addURL(url);
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    // Is the Class loaded already?
    Class<?> loadedClass = findLoadedClass(name);

    // Reuse existing instances of own Classes if loaded in parent ClassLoader
    // (Like the bootstrap Classes or this Class(Loader))
    if ((loadedClass == null) && (name.startsWith(packageName) || name.equals(ownClassName))) {
      try {
        loadedClass = (Class<?>) findLoadedClassMethod.invoke(parent, name);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        // Ignore
      }
    }

    // Never load API classes with this ClassLoader
    if ((loadedClass == null) && !name.startsWith(apiPackageName)) {
      try {
        // Find the Class from given jar URLs
        loadedClass = findClass(name);
      } catch (ClassNotFoundException e) {
        // Ignore
      }
    }

    // The Class hasn't been found yet.
    // Let's try finding it in our parent ClassLoader.
    // This will throw ClassNotFoundException in case of failure.
    if (loadedClass == null) {
      loadedClass = super.loadClass(name, resolve);
    }

    // Marked to resolve
    if (resolve) {
      resolveClass(loadedClass);
    }

    return loadedClass;
  }

  private static URL[] getOwnJarURL() {
    return new URL[] {
      DependencyClassLoader.class.getProtectionDomain().getCodeSource().getLocation()
    };
  }

  private static Method getFindLoadedClassMethod() {
    try {
      final Method method = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
      AccessController.doPrivileged(
          (PrivilegedExceptionAction<Void>)
              () -> {
                method.setAccessible(true);
                return null;
              });

      return method;
    } catch (NoSuchMethodException | SecurityException | PrivilegedActionException e) {
      // Can't continue
      throw new IllegalStateException("Exception while trying to prepare the ClassLoader", e);
    }
  }
}
