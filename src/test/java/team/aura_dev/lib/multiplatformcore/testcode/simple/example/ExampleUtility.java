package team.aura_dev.lib.multiplatformcore.testcode.simple.example;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExampleUtility {
  public static void setFlag(AtomicBoolean flag) {
    flag.set(true);
  }
}
