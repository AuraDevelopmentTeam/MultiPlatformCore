package team.aura_dev.lib.multiplatformcore;

import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReflectionHacker {
  // This is how the method would look in a barebones implementation
  // public static void allowJreAccess() {
  //   ModuleLayer moduleLayer = ModuleLayer.boot();
  //
  //   Module javaBaseModule = moduleLayer.findModule("java.base").get();
  //   Module ownModule = ReflectionHacker.class.getModule();
  //
  //   javaBaseModule.addOpens("java.lang", ownModule);
  // }

  public static void allowJreAccess()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException,
          ClassNotFoundException, NoSuchElementException {
    Class<?> moduleLayerClass;

    try {
      moduleLayerClass = Class.forName("java.lang.ModuleLayer");
    } catch (ClassNotFoundException e) {
      // We're on an old JVM. We have nothing to do :D
      return;
    }

    Class<?> moduleClass = Class.forName("java.lang.Module");
    Class<?> classClass = Class.forName("java.lang.Class");

    Object moduleLayer = moduleLayerClass.getMethod("boot").invoke(null);

    Object javaBaseModule =
        ((Optional<?>)
                moduleLayerClass
                    .getMethod("findModule", String.class)
                    .invoke(moduleLayer, "java.base"))
            .get();
    Object ownModule = classClass.getMethod("getModule").invoke(ReflectionHacker.class);

    moduleClass
        .getMethod("addOpens", String.class, moduleClass)
        .invoke(javaBaseModule, "java.lang", ownModule);
  }
}
