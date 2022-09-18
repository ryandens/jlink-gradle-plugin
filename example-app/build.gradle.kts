plugins {
  application
}

application {
  mainClass.set("com.ryandens.example.App")
}

abstract class JlinkJreTask : AbstractExecTask<JlinkJreTask> {
  @get:Nested
  abstract val javaCompiler: Property<JavaCompiler>

  @get:Input
  abstract val modules: ListProperty<String>

  @get:OutputDirectory
  abstract val outputDirectory: DirectoryProperty

  @Inject
  constructor() : super(JlinkJreTask::class.java) {
    // Access the default toolchain
    val toolchain = project.extensions.getByType<JavaPluginExtension>().toolchain

    // acquire a provider that returns the launcher for the toolchain
    val service = project.extensions.getByType<JavaToolchainService>()
    val defaultJlinkTool = service.compilerFor(toolchain)

    // use it as our default for the property
    javaCompiler.convention(defaultJlinkTool);
    modules.convention(listOf("java.base"))

    outputDirectory.convention(project.layout.buildDirectory.dir("jlink-jre"))
  }

  override fun exec() {
    setExecutable(javaCompiler.get().metadata.installationPath.file("bin/jlink"))
    val jlinkOutput = outputDirectory.dir("jre").get().asFile
    jlinkOutput.deleteRecursively() // jlink expects the output directory to not exist when it runs
    setArgs(listOf("--module-path", javaCompiler.get().metadata.installationPath.dir("jmods").asFile.absolutePath, "--add-modules", modules.get().joinToString(","), "--output", jlinkOutput.absolutePath))
    super.exec()
  }

}

val jlinkJre = tasks.register<JlinkJreTask>("jlinkJre") {
  javaCompiler.set(javaToolchains.compilerFor {
    languageVersion.set(JavaLanguageVersion.of(17))
  })
  modules.set(listOf("java.sql"))
}


tasks.named<JavaExec>("run") {
  val jlinkOutput = jlinkJre.get().outputDirectory
  inputs.dir(jlinkOutput)

  this.javaLauncher.set(object : JavaLauncher {
    override fun getMetadata(): JavaInstallationMetadata {
      return javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(17))
      }.get().metadata
    }

    override fun getExecutablePath(): RegularFile {
      return jlinkOutput.file("jre/bin/java").get()
    }
  })
}
