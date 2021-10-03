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
  //   Module unnamedModule = ReflectionHacker.class.getClassLoader().getUnnamedModule();
  //
  //   javaBaseModule.addOpens("java.lang", unnamedModule);
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
    Class<?> classLoaderClass = Class.forName("java.lang.ClassLoader");

    Object moduleLayer = moduleLayerClass.getMethod("boot").invoke(null);

    Object javaBaseModule =
        ((Optional<?>)
                moduleLayerClass
                    .getMethod("findModule", String.class)
                    .invoke(moduleLayer, "java.base"))
            .get();
    Object unnamedModule =
        classLoaderClass
            .getMethod("getUnnamedModule")
            .invoke(ReflectionHacker.class.getClassLoader());

    moduleClass
        .getMethod("addOpens", String.class, moduleClass)
        .invoke(javaBaseModule, "java.lang", unnamedModule);
  }
}
