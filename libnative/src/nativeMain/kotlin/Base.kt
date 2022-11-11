import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointed
import kotlinx.cinterop.CPointer

abstract class Base<T: CPointed> {
    val arena = Arena()

    abstract val handler: CPointer<T>

    open fun free() {
        arena.clear()
    }
}
