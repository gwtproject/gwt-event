plugins {
    id("local.gwt-library")
    id("local.maven-publish")
}

base.archivesBaseName = "gwt-event-legacy"

dependencies {
    api(project(":"))
}
