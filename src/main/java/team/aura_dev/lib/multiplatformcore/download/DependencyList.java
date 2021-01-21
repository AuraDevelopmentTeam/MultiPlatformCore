package team.aura_dev.lib.multiplatformcore.download;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import team.aura_dev.lib.multiplatformcore.dependency.RuntimeDependency;

/**
 * A little helper class to conveniently add dependencies with or without conditions.
 *
 * @author Yannick Schinko
 */
public class DependencyList {
  private final Map<RuntimeDependency, Predicate<RuntimeDependency>> dependencyEntries =
      new LinkedHashMap<>();

  /**
   * Generates a list of all dependencies which have their associated conditions met the moment this
   * method is executed.<br>
   * You most likely don't want to directly call this method. Instead you most likely want this one:
   * {@link DependencyDownloader#downloadAndInjectInClasspath(DependencyList)}
   *
   * @return A list of all dependencies which had their conditions evaluated as {@code true}
   * @see DependencyDownloader#downloadAndInjectInClasspath(DependencyList)
   */
  public List<RuntimeDependency> generateList() {
    return dependencyEntries.entrySet().stream()
        .filter(entry -> entry.getValue().test(entry.getKey()))
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  /**
   * Adds a dependency that always meets it condition.
   *
   * @param dependency The dependency to add to this list.
   */
  public void add(RuntimeDependency dependency) {
    addEntry(dependency, x -> true);
  }

  /**
   * Adds a dependency with a condition that doesn't depend on the dependency itself.
   *
   * @param dependency The dependency to add to this list.
   * @param condition A condition to be evaluated when determining which dependencies to load.<br>
   *     <strong>Does not</strong> depend on the dependency itself.
   */
  public void add(RuntimeDependency dependency, Supplier<Boolean> condition) {
    addEntry(dependency, x -> condition.get());
  }

  /**
   * Adds a dependency with a condition that does depend on the dependency itself.
   *
   * @param dependency The dependency to add to this list.
   * @param condition A condition to be evaluated when determining which dependencies to load.<br>
   *     <strong>Does</strong> depend on the dependency itself.
   */
  public void add(RuntimeDependency dependency, Predicate<RuntimeDependency> condition) {
    addEntry(dependency, condition);
  }

  /**
   * Adds a dependency with the condition that the supplied class name does not resolve to a class
   * on the classpath.
   *
   * @param dependency The dependency to add to this list.
   * @param className The class name to check to determine if this dependency needs to be added or
   *     not.
   */
  public void addIfClassMissing(RuntimeDependency dependency, String className) {
    addEntry(dependency, new ClassMissingPredicate(className));
  }

  /**
   * Removes a dependency from the list. It can be added back again later.
   *
   * @param dependency The dependency to remove from this list
   */
  public void remove(RuntimeDependency dependency) {
    dependencyEntries.remove(dependency);
  }

  /**
   * Prevents a dependency from ever fulfilling its condition.<br>
   * Essentially removes the dependency and contrary to {@link #remove(RuntimeDependency)} prevents
   * it from being added back.
   *
   * <p>The only way to ever add it back is by calling {@link #remove(RuntimeDependency)} first and
   * then adding the dependency with the new condition
   *
   * @param dependency The dependency to deny from this list
   */
  public void deny(RuntimeDependency dependency) {
    dependencyEntries.put(dependency, x -> false);
  }

  /**
   * A little helper method that adds the Entries to the map.<br>
   * If the dependency is not already in the map this just adds it to the map. If it is the previous
   * condition and the new condition are logically anded.
   *
   * @param dependency The dependency to add to this list.
   * @param condition A condition to be evaluated when determining which dependencies to load.
   */
  protected void addEntry(RuntimeDependency dependency, Predicate<RuntimeDependency> condition) {
    if (dependencyEntries.containsKey(dependency)) {
      condition = dependencyEntries.get(dependency).and(condition);
    }

    dependencyEntries.put(dependency, condition);
  }

  @RequiredArgsConstructor
  public static final class ClassMissingPredicate implements Predicate<RuntimeDependency> {
    private final String className;

    @Override
    public boolean test(RuntimeDependency runtimeDependency) {
      try {
        Class.forName(className);

        // Class found.
        // We don't need to load the dependency.
        return false;
      } catch (ClassNotFoundException e) {
        // Class not found!
        // We need to load the dependency!
        return true;
      }
    }
  }
}
