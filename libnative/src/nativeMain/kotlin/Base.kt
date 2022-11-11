import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointed
import kotlinx.cinterop.CPointer

abstract class Base<T: CPointed> {
    var arena: Arena? = null

    abstract fun handler(): CPointer<T>

    open fun free() {
        if (arena == null) {
            throw IllegalStateException("Arena is null")
        } else {
            arena?.clear()
            arena = null
        }
    }
}
