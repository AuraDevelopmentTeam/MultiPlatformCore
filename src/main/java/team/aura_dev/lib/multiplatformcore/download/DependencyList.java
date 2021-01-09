package team.aura_dev.lib.multiplatformcore.download;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import team.aura_dev.lib.multiplatformcore.dependency.RuntimeDependency;

/**
 * A little helper class to conveniently add dependencies with or without conditions.
 *
 * @author Yannick Schinko
 */
public class DependencyList {
  private final List<Entry> dependencyEntries = new LinkedList<>();

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
    return dependencyEntries.stream().flatMap(Entry::checkCondition).collect(Collectors.toList());
  }

  /**
   * Adds a dependency that always meets it condition.
   *
   * @param dependency The dependency to add to this list.
   */
  public void add(RuntimeDependency dependency) {
    addEntry(new Entry(dependency));
  }

  /**
   * Adds a dependency with a condition that doesn't depend on the dependency itself.
   *
   * @param dependency The dependency to add to this list.
   * @param condition A condition to be evaluated when determining which dependencies to load.<br>
   *     <strong>Does not</strong> depend on the dependency itself.
   */
  public void add(RuntimeDependency dependency, Supplier<Boolean> condition) {
    addEntry(new Entry(dependency, condition));
  }

  /**
   * Adds a dependency with a condition that does depend on the dependency itself.
   *
   * @param dependency The dependency to add to this list.
   * @param condition A condition to be evaluated when determining which dependencies to load.<br>
   *     <strong>Does</strong> depend on the dependency itself.
   */
  public void add(RuntimeDependency dependency, Predicate<RuntimeDependency> condition) {
    addEntry(new Entry(dependency, condition));
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
    addEntry(new Entry(dependency, new ClassMissingPredicate(className)));
  }

  /**
   * A little helper method that adds the {@link Entry Entries} to the list.<br>
   * Currently does nothing itself besides adding the {@link Entry} to the list. Justification is
   * that child classes need a way to add {@link Entry Entries} themselves and also there may be
   * logic in the future to prevent duplicate entries. Like {@code and}ing or {@code or}ing all
   * conditions together.
   *
   * @param entry The {@link Entry} to add to the list.
   */
  protected void addEntry(Entry entry) {
    dependencyEntries.add(entry);
  }

  /**
   * A class that represents a {@link RuntimeDependency} associated with its {@link Predicate} or
   * condition. Has a little {@link Stream} helper function.
   */
  @RequiredArgsConstructor
  protected static class Entry {
    private final RuntimeDependency dependency;
    private final Predicate<RuntimeDependency> condition;

    public Entry(RuntimeDependency dependency) {
      this(dependency, x -> true);
    }

    public Entry(RuntimeDependency dependency, Supplier<Boolean> condition) {
      this(dependency, x -> condition.get());
    }

    /**
     * Helper function to be used in a {@link Stream}. Filters out dependencies that don't need to
     * be loaded and maps it to the respective dependency in one step. To be used in {@link
     * Stream#flatMap(Function)}.
     *
     * @return a {@link Stream} with just the dependency if the conditon evaluated to {@code true}
     *     or an empty {@link Stream} if not.
     */
    public Stream<RuntimeDependency> checkCondition() {
      return condition.test(dependency) ? Stream.of(dependency) : Stream.empty();
    }
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
