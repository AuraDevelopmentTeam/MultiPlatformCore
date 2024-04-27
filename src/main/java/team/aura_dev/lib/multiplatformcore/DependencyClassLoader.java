package team.aura_dev.lib.multiplatformcore;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A custom {@link ClassLoader} implementation that allows adding {@link URL}s during runtime.
 *
 * <p>This class also ensures that {@link Class}es found in this ClassLoader's URL list are
 * considered first when loading a Class. This means that even if the parent ClassLoader has a Class
 * loaded with the exact same name this ClassLoader will load it again. This allows the user of this
 * Class to make sure that the URLs added will always be used. Which in consequence makes Classes
 * using this ClassLoader sandboxed from the rest of the runtime (regarding URLs added to this
 * ClassLoader). Very useful to ensure versions of libraries are exactly the ones added and no
 * conflicts with other plugins that add the same libraries to their jars.
 *
 * @author Yannick Schinko
 */
public class DependencyClassLoader extends URLClassLoader {
  protected final ClassLoader parent;
  protected final String ownClassName;
  public final List<String> excludedPackageNames;

  /**
   * Constructor that automatically detects the parent {@link ClassLoader} by using its own {@link
   * ClassLoader}.<br>
   * Generates the {@code excludedPackageNames} by appending {@code ".api"} and {@code
   * ".bootstrap"}.
   *
   * @param packageName The package base name all the plugin's classes are located in.<br>
   *     This is important because we must never load classes ourselves if they are part of the
   *     bootstrapping process or meant for external access like the API classes. The need to be
   *     loaded by the parent {@link ClassLoader}.
   * @see #DependencyClassLoader(ClassLoader, String)
   */
  public DependencyClassLoader(String packageName) {
    this(DependencyClassLoader.class.getClassLoader(), packageName);
  }

  /**
   * Constructor that automatically detects the parent {@link ClassLoader} by using its own {@link
   * ClassLoader}.
   *
   * @param excludedPackageNames A list of packages we wish to not load with this {@link
   *     ClassLoader}.<br>
   *     Use this for classes that need to accessed externally (like the api) or the bootstrap
   *     classes.
   */
  public DependencyClassLoader(String... excludedPackageNames) {
    this(DependencyClassLoader.class.getClassLoader(), excludedPackageNames);
  }

  /**
   * Constructor that allows you to specify the parent {@link ClassLoader} you want to use.<br>
   * Generates the {@code excludedPackageNames} by appending {@code ".api"} and {@code
   * ".bootstrap"}.
   *
   * @param parent parent {@link ClassLoader} to be used if a {@link Class} cannot be found in the
   *     own {@link URL}s.
   * @param packageName The package base name all the plugin's classes are located in.<br>
   *     This is important because we must never load classes ourselves if they are part of the
   *     bootstrapping process or meant for external access like the API classes. The need to be
   *     loaded by the parent {@link ClassLoader}.
   * @see #DependencyClassLoader(ClassLoader, String...)
   */
  public DependencyClassLoader(ClassLoader parent, String packageName) {
    this(parent, packageName + ".api", packageName + ".bootstrap");
  }

  /**
   * Constructor that allows you to specify the parent {@link ClassLoader} you want to use.
   *
   * @param parent parent {@link ClassLoader} to be used if a {@link Class} cannot be found in the
   *     own {@link URL}s.
   * @param excludedPackageNames A list of packages we wish to not load with this {@link
   *     ClassLoader}.<br>
   *     Use this for classes that need to accessed externally (like the api) or the bootstrap
   *     classes.
   */
  public DependencyClassLoader(ClassLoader parent, String... excludedPackageNames) {
    // Start off with adding its own jar URL
    super(getOwnJarURL(), parent);

    this.ownClassName = DependencyClassLoader.class.getName();
    this.parent = parent;
    this.excludedPackageNames =
        Arrays.stream(excludedPackageNames)
            .map(exclPackage -> exclPackage + ".")
            .collect(Collectors.toList());
  }

  @Override
  public void addURL(URL url) {
    super.addURL(url);
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    // Is the Class loaded already?
    Class<?> loadedClass = findLoadedClass(name);

    // Load class through our own ClassLoader if it hasn't been excluded
    if ((loadedClass == null)
        && !(name.equals(ownClassName)
            || this.excludedPackageNames.stream().anyMatch(name::startsWith))) {
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
}
