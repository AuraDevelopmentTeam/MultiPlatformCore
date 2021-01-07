package team.aura_dev.lib.multiplatformcore.download;

import lombok.experimental.UtilityClass;
import team.aura_dev.lib.multiplatformcore.dependency.RuntimeDependency;

@UtilityClass
public class TestRuntimeDependencies {
  public static final RuntimeDependency CONFIGURATE_HOCON =
      RuntimeDependency.builder(
              "org.spongepowered",
              "configurate-hocon",
              "3.6.1",
              "6395403afce7b9bbf4e26ef74c13da9a",
              "e3f199dbd91de753a70f63606f530fdb8644bbd5")
          .maven(RuntimeDependency.Maven.SPONGE)
          .transitive()
          .exclusion("com.google.code.findbugs:jsr305")
          .exclusion("com.google.errorprone:error_prone_annotations")
          .exclusion("com.google.j2objc:j2objc-annotations")
          .exclusion("org.codehaus.mojo:animal-sniffer-annotations")
          .build();
}
