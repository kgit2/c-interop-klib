import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import libapi.get_strings

fun main() {
    memScoped {
        val out = allocArray<CPointerVar<ByteVar>>(3)
        get_strings(out.getPointer(this@memScoped), 3)
        println(out.pointed)
        for (i in 0 until 3) {
            println(out[i]?.toKString())
        }
    }
}
