package team.aura_dev.lib.multiplatformcore;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

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

  /**
   * Constructor that automatically detects the parent {@link ClassLoader} by using its own {@link
   * ClassLoader}.
   */
  public DependencyClassLoader() {
    this(DependencyClassLoader.class.getClassLoader());
  }

  /**
   * Constructor that allows you to specify the parent {@link ClassLoader} you want to use.
   *
   * @param parent parent {@link ClassLoader} to be used if a {@link Class} cannot be found in the
   *     own {@link URL}s.
   */
  public DependencyClassLoader(ClassLoader parent) {
    // Start of with adding its own jar URL
    super(getOwnJarURL(), parent);

    this.parent = parent;
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
    if ((loadedClass == null) && name.startsWith("@group@")) {
      try {
        loadedClass = (Class<?>) findLoadedClassMethod.invoke(parent, name);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        // Ignore
      }
    }

    // Never load API classes with this ClassLoader
    if ((loadedClass == null) && !name.startsWith("@group@.api")) {
      try {
        // Find the Class from given jar URLs
        loadedClass = findClass(name);
      } catch (ClassNotFoundException e) {
        // Ignore
      }
    }

    // The Class hasn't been found yet
    // Let's try finding it in our parent ClassLoader
    // This'll throw ClassNotFoundException in failure
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
      method.setAccessible(true);

      return method;
    } catch (NoSuchMethodException | SecurityException e) {
      // Can't continue
      throw new IllegalStateException("Exception while trying to prepare the ClassLoader", e);
    }
  }
}
