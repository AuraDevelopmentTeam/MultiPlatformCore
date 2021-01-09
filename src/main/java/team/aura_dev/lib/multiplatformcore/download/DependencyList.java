package team.aura_dev.lib.multiplatformcore.download;

import java.util.LinkedList;
import java.util.List;
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

  public List<RuntimeDependency> generateList() {
    return dependencyEntries.stream().flatMap(Entry::checkCondition).collect(Collectors.toList());
  }

  public void add(RuntimeDependency dependency) {
    addEntry(new Entry(dependency));
  }

  public void add(RuntimeDependency dependency, Supplier<Boolean> condition) {
    addEntry(new Entry(dependency, condition));
  }

  public void add(RuntimeDependency dependency, Predicate<RuntimeDependency> condition) {
    addEntry(new Entry(dependency, condition));
  }

  public void addIfClassMissing(RuntimeDependency dependency, String className) {
    addEntry(new Entry(dependency, new ClassMatcher(className)));
  }

  protected void addEntry(Entry entry) {
    dependencyEntries.add(entry);
  }

  @RequiredArgsConstructor
  protected static final class Entry {
    private final RuntimeDependency dependency;
    private final Predicate<RuntimeDependency> condition;

    public Entry(RuntimeDependency dependency) {
      this(dependency, x -> true);
    }

    public Entry(RuntimeDependency dependency, Supplier<Boolean> condition) {
      this(dependency, x -> condition.get());
    }

    public Stream<RuntimeDependency> checkCondition() {
      return condition.test(dependency) ? Stream.of(dependency) : Stream.empty();
    }
  }

  @RequiredArgsConstructor
  public static class ClassMatcher implements Predicate<RuntimeDependency> {
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
