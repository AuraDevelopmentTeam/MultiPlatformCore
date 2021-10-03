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
  //   ownModule.addExports("java.lang", javaBaseModule);
  //   ownModule.addOpens("java.lang", javaBaseModule);
  // }

  public static void allowJreAccess()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException,
          ClassNotFoundException, NoSuchElementException {
    System.out.println("Method enter");

    Class<?> moduleLayerClass;

    try {
      System.out.println("Loading classes...");

      moduleLayerClass = Class.forName("java.lang.ModuleLayer");
    } catch (ClassNotFoundException e) {
      System.out.println("Loading classes FAILED!");

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

    System.out.println("Own Module: " + ownModule);

    moduleClass
        .getMethod("addExports", String.class, moduleClass)
        .invoke(ownModule, "java.lang", javaBaseModule);
    moduleClass
        .getMethod("addOpens", String.class, moduleClass)
        .invoke(ownModule, "java.lang", javaBaseModule);
  }
}
