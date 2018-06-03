package local

plugins {
    `java-library`
    id("net.ltgt.errorprone")
}

java.sourceCompatibility = JavaVersion.VERSION_1_8
if (JavaVersion.current().isJava9Compatible) {
    tasks.withType<JavaCompile> { options.compilerArgs.addAll(listOf("--release", java.targetCompatibility.majorVersion)) }
}

repositories {
    mavenCentral()
}

dependencies {
    "errorprone"("com.google.errorprone:error_prone_core:2.3.1")
}

tasks {
    "jar"(Jar::class) {
        from(java.sourceSets["main"].allJava)
    }

    "test"(Test::class) {
        include("**/*Suite.class")
    }

    "javadoc"(Javadoc::class) {
        options.encoding = "UTF-8"
        (options as CoreJavadocOptions).addBooleanOption("Xdoclint:all,-missing", true)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Werror", "-Xlint:all"))
}

val Project.java: JavaPluginConvention
    get() = the()
