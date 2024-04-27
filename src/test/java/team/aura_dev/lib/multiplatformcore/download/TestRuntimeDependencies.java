package team.aura_dev.lib.multiplatformcore.download;

import lombok.experimental.UtilityClass;
import team.aura_dev.lib.multiplatformcore.dependency.RuntimeDependency;

@UtilityClass
public class TestRuntimeDependencies {
  // Working ones
  public static final RuntimeDependency CONFIGURATE_HOCON =
      RuntimeDependency.builder(
              "org.spongepowered",
              "configurate-hocon",
              "3.7.3",
              "c5679b4202b226fbe07f84dfb300a8d0",
              "630e0562bd9b809428b55742aabe382f0347211f")
          .transitive()
          .exclusion("com.google.code.findbugs:jsr305")
          .exclusion("com.google.errorprone:error_prone_annotations")
          .exclusion("com.google.j2objc:j2objc-annotations")
          .exclusion("org.codehaus.mojo:animal-sniffer-annotations")
          .build();

  // Broken ones
  public static final RuntimeDependency CONFIGURATE_HOCON_MD5_MISMATCH =
      CONFIGURATE_HOCON.toBuilder().md5Hash("00000000000000000000000000000000").build();
  public static final RuntimeDependency CONFIGURATE_HOCON_SHA1_MISMATCH =
      CONFIGURATE_HOCON.toBuilder().sha1Hash("0000000000000000000000000000000000000000").build();
  public static final RuntimeDependency CONFIGURATE_HOCON_WRONG_ARTIFACT_ID =
      CONFIGURATE_HOCON.toBuilder().artifactId("xxx").build();
  public static final RuntimeDependency CONFIGURATE_HOCON_WRONG_CLASSIFIER =
      CONFIGURATE_HOCON.toBuilder().classifier("xxx").build();
}
