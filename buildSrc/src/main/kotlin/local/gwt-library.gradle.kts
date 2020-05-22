package local

plugins {
    `java-library`
}

apply(plugin = "net.ltgt.errorprone")

java.sourceCompatibility = JavaVersion.VERSION_1_8
if (JavaVersion.current().isJava9Compatible) {
    tasks.withType<JavaCompile>().configureEach { options.compilerArgs.addAll(listOf("--release", java.targetCompatibility.majorVersion)) }
}

repositories {
    mavenCentral()
}

dependencies {
    "errorprone"("com.google.errorprone:error_prone_core:2.3.4")
    "errorproneJavac"("com.google.errorprone:javac:9+181-r4173-1")
}

tasks {
    jar {
        from(sourceSets.main.map { it.allJava })
    }

    test {
        include("**/*Suite.class")
    }

    javadoc {
        options.encoding = "UTF-8"
        (options as CoreJavadocOptions).addBooleanOption("Xdoclint:all,-missing", true)
        if (JavaVersion.current().isJava9Compatible) {
            (options as CoreJavadocOptions).addBooleanOption("html5", true)
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Werror", "-Xlint:all"))
}
