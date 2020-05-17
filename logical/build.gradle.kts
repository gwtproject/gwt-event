plugins {
    id("local.gwt-library")
    id("local.gwt-test")
    id("local.maven-publish")
}

base.archivesBaseName = "gwt-logical-event"

dependencies {
    api(project(":"))

    testImplementation("junit:junit:4.13")
}

gwtTest {
    moduleName.set("org.gwtproject.event.logical.LogicalEvent")
    gwtVersion.set("2.9.0")
}
