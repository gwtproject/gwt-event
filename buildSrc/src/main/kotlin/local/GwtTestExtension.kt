package local

import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

open class GwtTestExtension @Inject constructor(objects: ObjectFactory) {
    val moduleName = objects.property<String>()
    val gwtVersion = objects.property<String>()
}
