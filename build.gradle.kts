import java.time.Year

plugins {
    id("local.gwt-library")
    id("local.gwt-test")
    id("local.maven-publish")

    id("net.ltgt.errorprone") version "1.1.1"
    id("com.diffplug.gradle.spotless") version "3.30.0"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("com.github.hierynomus.license") version "0.15.0"
}

buildscript {
    dependencyLocking {
        lockAllConfigurations()
        lockMode.set(LockMode.STRICT)
    }
}
allprojects {
    dependencyLocking {
        lockAllConfigurations()
        lockMode.set(LockMode.STRICT)
    }
}
tasks {
    register("allDependencies") {
        dependsOn("dependencies", subprojects.map { ":${it.name}:dependencies" })
    }
}

group = "org.gwtproject.event"

dependencies {
    testImplementation("junit:junit:4.13")
}

gwtTest {
    moduleName.set("org.gwtproject.event.Event")
    gwtVersion.set("2.9.0")
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
        (this as ExtensionAware).extra["name"] = "The GWT Project Authors"
    }
}

repositories {
    jcenter()
}

allprojects {
    apply(plugin = "com.diffplug.gradle.spotless")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    spotless {
        java {
            // local.gwt-test generates sources, we only want to check sources
            targetExclude(fileTree(buildDir) { include("**/*.java") })
            googleJavaFormat("1.7")
        }
    }
    ktlint {
        version.set("0.36.0")
        enableExperimentalRules.set(true)
    }
}

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
