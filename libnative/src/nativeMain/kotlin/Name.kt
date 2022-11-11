import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cstr
import kotlinx.cinterop.ptr
import libapi.api_name

data class Name(
    val first: String,
    val last: String,
) : Base<api_name>() {
    override fun handler(): CPointer<api_name> {
        if (arena != null) {
            throw IllegalStateException("Arena is not null, you should free it first")
        }
        val arena = Arena()
        this.arena = arena
        return arena.alloc<api_name>().apply {
            this.first = this@Name.first.cstr.getPointer(arena)
            this.last = this@Name.last.cstr.getPointer(arena)
        }.ptr
    }

    override fun toString(): String {
        return "$first $last"
    }

    override fun free() {
        println("freeing name: ${toString()}")
        super.free()
    }
}
