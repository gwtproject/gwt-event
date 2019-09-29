package local

plugins {
    java
}
val extension = extensions.create<GwtTestExtension>("gwtTest")

val generateGwtTestSources by tasks.registering(Copy::class) {
    inputs.property("gwtTest.module", extension.moduleName)

    from(sourceSets.test.map { it.allJava.sourceDirectories })
    into("$buildDir/generated-sources/gwtTest/")
    filter {
        if (it == "import junit.framework.TestCase;") {
            "import com.google.gwt.junit.client.GWTTestCase;"
        } else {
            it.replace(
                "extends TestCase {",
                """extends GWTTestCase {
  @Override
  public String getModuleName() { return "${extension.moduleName.get()}"; }"""
            )
                .replace("protected void setUp()", "protected void gwtSetUp()")
                .replace("super.setUp()", "super.gwtSetUp()")
                .replace("protected void tearDown()", "protected void gwtTearDown()")
                .replace("super.tearDown()", "super.gwtTearDown()")
        }
    }
}

sourceSets {
    create("gwtTest") {
        java {
            srcDirs(generateGwtTestSources.map { it.destinationDir })
        }
        compileClasspath += sourceSets.test.get().compileClasspath
        runtimeClasspath += output + compileClasspath +
            sourceSets.main.get().allJava.sourceDirectories +
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

tasks {
    val gwtTest by registering(Test::class) {
        val warDir = file("$buildDir/gwt/www-test")
        val workDir = file("$buildDir/gwt/work")
        val cacheDir = file("$buildDir/gwt/cache")
        outputs.dirs(warDir, workDir, cacheDir)

        testClassesDirs = sourceSets["gwtTest"].output.classesDirs
        classpath = sourceSets["gwtTest"].runtimeClasspath
        include("**/*Suite.class")
        systemProperty("gwt.args", "-ea -draftCompile -batch module -war \"$warDir\" -workDir \"$workDir\"")
        systemProperty("gwt.persistentunitcachedir", cacheDir)
    }
    check {
        dependsOn(gwtTest)
    }
}
