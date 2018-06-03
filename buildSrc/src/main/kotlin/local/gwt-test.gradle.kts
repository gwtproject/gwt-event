package local

val extension = extensions.create("gwtTest", GwtTestExtension::class.java, objects)

val generateGwtTestSources by tasks.creating(Copy::class) {
    inputs.property("gwtTest.module", extension.moduleName)

    from(java.sourceSets["test"].allJava.sourceDirectories)
    into("$buildDir/generated-sources/gwtTest/")
    filter {
    if (it == "import junit.framework.TestCase;") {
        "import com.google.gwt.junit.client.GWTTestCase;"
    } else
    it.replace("extends TestCase {", """extends GWTTestCase {
  @Override
  public String getModuleName() { return "${extension.moduleName.get()}"; }""")
        .replace("protected void setUp()", "protected void gwtSetUp()")
        .replace("super.setUp()", "super.gwtSetUp()")
        .replace("protected void tearDown()", "protected void gwtTearDown()")
        .replace("super.tearDown()", "super.gwtTearDown()")
    }
}

java.sourceSets {
    "gwtTest" {
        java {
            srcDirs(files(generateGwtTestSources.destinationDir).builtBy(generateGwtTestSources))
        }
        compileClasspath += project.java.sourceSets["test"].compileClasspath
        runtimeClasspath += output + compileClasspath +
            project.java.sourceSets["main"].allJava.sourceDirectories +
            allJava.sourceDirectories
    }
}
configurations {
    getByName("gwtTestCompile").extendsFrom(getByName("testCompile"))
    getByName("gwtTestImplementation").extendsFrom(getByName("testImplementation"))
    getByName("gwtTestRuntime").extendsFrom(getByName("testRuntime"))
    getByName("gwtTestRuntimeOnly").extendsFrom(getByName("testRuntimeOnly"))
}
afterEvaluate {
    dependencies {
        "gwtTestImplementation"("com.google.gwt:gwt-user:${extension.gwtVersion.get()}")

        "gwtTestRuntimeOnly"("com.google.gwt:gwt-dev:${extension.gwtVersion.get()}")
    }
}

val gwtTest by tasks.creating(Test::class) {
    val warDir = file("$buildDir/gwt/www-test")
    val workDir = file("$buildDir/gwt/work")
    val cacheDir = file("$buildDir/gwt/cache")
    doFirst {
        mkdir(warDir)
        mkdir(workDir)
        mkdir(cacheDir)
    }

    testClassesDirs = java.sourceSets["gwtTest"].output.classesDirs
    classpath = java.sourceSets["gwtTest"].runtimeClasspath
    include("**/*Suite.class")
    systemProperty("gwt.args", "-ea -draftCompile -batch module -war \"$warDir\" -workDir \"$workDir\"")
    systemProperty("gwt.persistentunitcachedir", cacheDir)
}
tasks["check"].dependsOn(gwtTest)

inline val Project.java: JavaPluginConvention
    get() = the()
