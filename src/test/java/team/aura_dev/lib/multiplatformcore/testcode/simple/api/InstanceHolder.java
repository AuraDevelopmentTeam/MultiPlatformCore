package team.aura_dev.lib.multiplatformcore.testcode.simple.api;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class InstanceHolder {
  @Getter @Setter private static TestPluginApi instance;
}
