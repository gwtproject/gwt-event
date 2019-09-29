package local

import javax.inject.Inject
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property

open class GwtTestExtension @Inject constructor(objects: ObjectFactory) {
    val moduleName = objects.property<String>()
    val gwtVersion = objects.property<String>()
}
