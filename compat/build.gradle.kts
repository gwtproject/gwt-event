import nl.javadude.gradle.plugins.license.License

plugins {
    id("local.gwt-library")
    id("local.gwt-test")
    id("local.maven-publish")
}

base.archivesBaseName = "gwt-event-compat"

dependencies {
    api(project(":"))
    api("com.google.gwt:gwt-user:2.9.0")

    testImplementation("junit:junit:4.13")
}

tasks.withType<Javadoc>().configureEach {
    // Workaround for https://github.com/gradle/gradle/issues/5630
    (options as CoreJavadocOptions).addStringOption("sourcepath", "")
}

val javaTemplates = "src/test/java-templates/"
val generateTestSources by tasks.registering(Copy::class) {
    into("$buildDir/generated-sources/java-templates/test/")
    from(javaTemplates) {
        rename { it.replace("__variant__", "Bindery") }
        filter {
            it.replace("__variant__", "Bindery")
        }
    }
    from(javaTemplates) {
        rename { it.replace("__variant__", "Gwt") }
        filter {
            it.replace("__variant__", "Gwt")
                .replace("FooEvent", "FooGwtEvent")
                .replace("BarEvent", "BarGwtEvent")
        }
    }
}
sourceSets {
    test {
        java {
            srcDirs(files(generateTestSources.map { it.destinationDir }))
        }
    }
}
listOf("licenseTest", "licenseFormatTest").forEach {
    tasks.named<License>(it) {
        source(fileTree(javaTemplates))
    }
}

gwtTest {
    moduleName.set("org.gwtproject.event.compat.EventCompat")
    gwtVersion.set("2.9.0")
}
