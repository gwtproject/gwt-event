package local

import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property

open class GwtTestExtension(val objects: ObjectFactory) {
    val moduleName = objects.property<String>()
    val gwtVersion = objects.property<String>()
}
