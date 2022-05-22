package application

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import react.*

fun CoroutineScope.useEffect(
  vararg dependencies: Any,
  effect: suspend EffectBuilder.() -> Unit
) {
  react.useEffect(*dependencies) {
    val builder = this
    val job = launch {
      effect.invoke(builder)
    }
    cleanup(job::cancel)
  }
}

inline fun <T> CoroutineScope.useStateWithEffect(
  initialValue: T,
  vararg dependencies: Any,
  crossinline block: suspend (EffectBuilder).() -> T
): StateInstance<T> {
  val state = useState(initialValue)
  val (_, setState) = state
  useEffect(*dependencies) {
    setState(block.invoke(this))
  }
  return state
}

inline fun <T: Any?> CoroutineScope.useStateWithEffect(
  vararg dependencies: Any,
  crossinline block: suspend (EffectBuilder).() -> T?
): StateInstance<T?> {
  return useStateWithEffect(
    initialValue = null,
    dependencies = dependencies,
    block = block
  )
}

@Suppress("NOTHING_TO_INLINE")
@OptIn(DelicateCoroutinesApi::class)
inline fun <T> useGlobalSuspendableStateWithEffect(
  initialValue: T,
  vararg dependencies: Any,
  noinline block: suspend (EffectBuilder).() -> T
): StateInstance<T> {
  return GlobalScope.useStateWithEffect(initialValue, *dependencies, block = block)
}
