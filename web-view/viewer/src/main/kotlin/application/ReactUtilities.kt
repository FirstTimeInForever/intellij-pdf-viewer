package application

import csstype.ClassName
import react.*

object ReactUtilities {
  fun PropsWithClassName.appendClassNames(vararg names: ClassName?) {
    className = ClassName(buildString {
      if (className.unsafeCast<String?>()?.isNotEmpty() == true) {
        append(className.unsafeCast<String>())
        append(" ")
      }
      append(names.filterNotNull().joinToString(separator = " ") { it.unsafeCast<String>() })
    })
  }

  fun <T: Any> PropsWithRef<T>.withReference(reference: Ref<T>) {
    ref = reference
  }

  fun ChildrenBuilder.appendChildren(props: PropsWithChildren) {
    props.children?.let(this::child)
  }
}
