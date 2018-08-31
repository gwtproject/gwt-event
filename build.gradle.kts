import nl.javadude.gradle.plugins.license.LicenseExtension
import java.time.Year

plugins {
    id("local.gwt-library")
    id("local.gwt-test")
    id("local.maven-publish")

    id("net.ltgt.errorprone-javacplugin") version "0.5"
    id("com.github.sherter.google-java-format") version "0.7.1"
    id("com.github.hierynomus.license") version "0.14.0"
}

group = "org.gwtproject.event"
version = "HEAD-SNAPSHOT"

dependencies {
    testImplementation("junit:junit:4.12")
}

gwtTest {
    moduleName.set("org.gwtproject.event.Event")
    gwtVersion.set("2.8.2")
}

// Code style
allprojects {
    apply(plugin = "com.github.hierynomus.license")

    license {
        header = rootProject.file("LICENSE.header")
        encoding = "UTF-8"
        skipExistingHeaders = true
        mapping("java", "SLASHSTAR_STYLE")
        exclude("**/META-INF/**")

        (this as ExtensionAware).extra["year"] = Year.now()
        (this as ExtensionAware).extra["name"] = "Thomas Broyer"
    }
}

repositories {
    jcenter()
}

val ktlint by configurations.creating

dependencies {
    ktlint("com.github.shyiko:ktlint:0.27.0")
}

val verifyKtlint by tasks.creating(JavaExec::class) {
    description = "Check Kotlin code style."
    classpath = ktlint
    main = "com.github.shyiko.ktlint.Main"
    args("**/*.gradle.kts", "**/*.kt")
}
tasks["check"].dependsOn(verifyKtlint)

task("ktlint", JavaExec::class) {
    description = "Fix Kotlin code style violations."
    classpath = verifyKtlint.classpath
    main = verifyKtlint.main
    args("-F")
    args(verifyKtlint.args)
}

fun Project.license(configuration: LicenseExtension.() -> Unit) = configure(configuration)

/*
configurations {
  j2cl_transpiler
  j2cl
}
dependencies {
  j2cl_transpiler files('../../google/j2cl/bazel-bin/transpiler/java/com/google/j2cl/transpiler/J2clTranspiler_deploy.jar')
  j2cl files('../../google/j2cl/bazel-bin/jre/java/jre.jar')
}
task transpile(type: JavaExec) {
  inputs.files configurations.j2cl
  inputs.files sourceSets.main.allJava
  outputs.file "${buildDir}/libs/gwt-events.js.zip"

  main = 'com.google.j2cl.transpiler.J2clTranspiler'
  classpath = configurations.j2cl_transpiler
  args = [ "-cp", configurations.j2cl.asPath, "-d", "${buildDir}/libs/gwt-events.js.zip" ] \
       + sourceSets.main.allJava
}
assemble.dependsOn transpile
*/
