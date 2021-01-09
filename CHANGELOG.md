Version 1.2.1
-------------

\+ Added utility class `DependencyList`.  


Version 1.2.0
-------------

\* Fixed class loading issue by forcing to pass a `Class` instance of the base plugin class along the bootstrapper constructor.  
\* Several small fixes and better handling of rare edge cases.  
\* Pretty much full test coverage.  
\* Full Javadocs documentation.  


Version 1.1.1
-------------

\* Renaming Bootstrapper class names. From `...Bootstrap` to `...Bootstrapper`.  
\* Bootstrappers now return the actual plugin instance instead of hacking around with reflections.  


Version 1.1.0
-------------

\+ Added `MultiProjectBootstrap` class to allow easier bootstrapping.  
\+ Added `MultiProjectSLF4JBootstrap` class to allow bootstrapping with SLF4J support.  


Version 1.0.0
-------------

\+ Added `DependencyClassLoader` to allow isolating the runtime.  
\+ Added `DependencyDownloader` to allow downloading dependencies at runtime. Dependencies can be declared to resolved their dependencies, which are
   downloaded as well.  
\+ Added `RuntimeDependency` as a way to declare dependencies to download at runtime.  
