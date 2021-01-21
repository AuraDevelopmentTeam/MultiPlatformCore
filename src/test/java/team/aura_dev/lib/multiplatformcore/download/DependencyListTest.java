package team.aura_dev.lib.multiplatformcore.download;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;
import org.junit.Test;
import team.aura_dev.lib.multiplatformcore.dependency.RuntimeDependency;

public class DependencyListTest {
  @Test
  public void generateList() {
    final DependencyList list = new DependencyList();

    list.add(TestRuntimeDependencies.CONFIGURATE_HOCON);
    list.add(TestRuntimeDependencies.CONFIGURATE_HOCON_MD5_MISMATCH, x -> true);
    list.add(TestRuntimeDependencies.CONFIGURATE_HOCON_SHA1_MISMATCH, x -> false);

    assertEquals(
        Arrays.asList(
            TestRuntimeDependencies.CONFIGURATE_HOCON,
            TestRuntimeDependencies.CONFIGURATE_HOCON_MD5_MISMATCH),
        list.generateList());
  }

  @Test
  public void addPlainTest() {
    final DependencyList list = new DependencyList();

    list.add(TestRuntimeDependencies.CONFIGURATE_HOCON);
    list.add(TestRuntimeDependencies.CONFIGURATE_HOCON_MD5_MISMATCH);

    assertEquals(
        Arrays.asList(
            TestRuntimeDependencies.CONFIGURATE_HOCON,
            TestRuntimeDependencies.CONFIGURATE_HOCON_MD5_MISMATCH),
        list.generateList());
  }

  @Test
  public void addSupplierTest() {
    final DependencyList list = new DependencyList();

    list.add(TestRuntimeDependencies.CONFIGURATE_HOCON, () -> true);
    list.add(TestRuntimeDependencies.CONFIGURATE_HOCON_MD5_MISMATCH, () -> true);
    list.add(TestRuntimeDependencies.CONFIGURATE_HOCON_SHA1_MISMATCH, () -> false);

    assertEquals(
        Arrays.asList(
            TestRuntimeDependencies.CONFIGURATE_HOCON,
            TestRuntimeDependencies.CONFIGURATE_HOCON_MD5_MISMATCH),
        list.generateList());
  }

  @Test
  public void addPredicateTest() {
    final DependencyList list = new DependencyList();
    final Predicate<RuntimeDependency> condition =
        dep -> dep != TestRuntimeDependencies.CONFIGURATE_HOCON_SHA1_MISMATCH;

    list.add(TestRuntimeDependencies.CONFIGURATE_HOCON, condition);
    list.add(TestRuntimeDependencies.CONFIGURATE_HOCON_MD5_MISMATCH, condition);
    list.add(TestRuntimeDependencies.CONFIGURATE_HOCON_SHA1_MISMATCH, condition);

    assertEquals(
        Arrays.asList(
            TestRuntimeDependencies.CONFIGURATE_HOCON,
            TestRuntimeDependencies.CONFIGURATE_HOCON_MD5_MISMATCH),
        list.generateList());
  }

  @Test
  public void addIfClassMissingTest() {
    final DependencyList list = new DependencyList();

    list.addIfClassMissing(TestRuntimeDependencies.CONFIGURATE_HOCON, "java.lang.X");
    list.addIfClassMissing(TestRuntimeDependencies.CONFIGURATE_HOCON_MD5_MISMATCH, "java.lang.X");
    list.addIfClassMissing(
        TestRuntimeDependencies.CONFIGURATE_HOCON_SHA1_MISMATCH, "java.lang.Object");

    assertEquals(
        Arrays.asList(
            TestRuntimeDependencies.CONFIGURATE_HOCON,
            TestRuntimeDependencies.CONFIGURATE_HOCON_MD5_MISMATCH),
        list.generateList());
  }

  @Test
  public void removeTest() {
    final DependencyList list = new DependencyList();

    list.add(TestRuntimeDependencies.CONFIGURATE_HOCON);
    list.add(TestRuntimeDependencies.CONFIGURATE_HOCON_MD5_MISMATCH);

    list.remove(TestRuntimeDependencies.CONFIGURATE_HOCON);
    list.remove(TestRuntimeDependencies.CONFIGURATE_HOCON_MD5_MISMATCH);

    list.add(TestRuntimeDependencies.CONFIGURATE_HOCON);

    assertEquals(
        Collections.singletonList(TestRuntimeDependencies.CONFIGURATE_HOCON), list.generateList());
  }

  @Test
  public void denyTest() {
    final DependencyList list = new DependencyList();

    list.add(TestRuntimeDependencies.CONFIGURATE_HOCON);
    list.add(TestRuntimeDependencies.CONFIGURATE_HOCON_MD5_MISMATCH);

    list.deny(TestRuntimeDependencies.CONFIGURATE_HOCON_MD5_MISMATCH);

    list.add(TestRuntimeDependencies.CONFIGURATE_HOCON_MD5_MISMATCH);

    assertEquals(
        Collections.singletonList(TestRuntimeDependencies.CONFIGURATE_HOCON), list.generateList());
  }
}
