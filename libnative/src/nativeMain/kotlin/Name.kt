import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cstr
import kotlinx.cinterop.ptr
import libapi.api_name

data class Name(
    val first: String,
    val last: String,
) : Base<api_name>() {
    override val handler: CPointer<api_name> = arena.alloc<api_name>().apply {
        this.first = this@Name.first.cstr.getPointer(arena)
        this.last = this@Name.last.cstr.getPointer(arena)
    }.ptr

    override fun toString(): String {
        return "$first $last"
    }

    override fun free() {
        println("freeing name")
        super.free()
    }
}
