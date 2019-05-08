plugins {
    `java-gradle-plugin`
    `kotlin-dsl`

    id("com.diffplug.gradle.spotless") version "3.23.0"
}
repositories {
    jcenter()
}
kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

spotless {
    val ktlintVersion = "0.32.0"
    kotlin {
        // for some reason, spotless includes *.kts but won't lint them as scripts
        targetExclude("**/*.kts")
        ktlint(ktlintVersion)
    }
    kotlinGradle {
        target("*.gradle.kts", "src/main/kotlin/**/*.gradle.kts")
        ktlint(ktlintVersion)
    }
}
