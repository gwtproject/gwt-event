plugins {
    id("local.gwt-library")
    id("local.gwt-test")
}

dependencies {
    api(project(":"))

    testImplementation("junit:junit:4.12")
}

gwtTest {
    moduleName.set("org.gwtproject.event.logical.LogicalEvent")
    gwtVersion.set("2.8.2")
}
