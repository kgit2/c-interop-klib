import kotlinx.cinterop.Arena
import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.COpaquePointerVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.get
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import libapi.api_user

data class User(
    val name: Name,
    val age: Int,
) : Base<api_user>() {
    override fun handler(): CPointer<api_user> {
        if (arena != null) {
            throw IllegalStateException("Arena is not null, you should free it first")
        }
        val arena = Arena()
        this.arena = arena
        return arena.alloc<api_user>().apply {
            this.name = this@User.name.handler()
            this.age = this@User.age
        }.ptr
    }

    override fun toString(): String {
        return "name=$name, age=$age"
    }

    override fun free() {
        println("freeing user: ${toString()}")
        name.free()
        super.free()
    }
}

fun createUser(firstName: String, lastName: String, age: Int, raw: CPointer<COpaquePointerVar>): CPointer<api_user> {
    val user = createUserForKClass(firstName, lastName, age)
    raw.pointed.value = StableRef.create(user).asCPointer()
    return user.handler()
}

fun freeUser(raw: CPointer<COpaquePointerVar>) {
    println("freeing user")
    raw.pointed.value!!.asStableRef<User>().get().free()
}

fun createUserForKClass(firstName: String, lastName: String, age: Int): User {
    val name = Name(firstName, lastName)
    val user = User(name, age)
    userList.add(user)
    return user
}

internal var userList = mutableListOf<User>()

fun getUserCArray(size: IntVar, arenaPointer: CPointer<COpaquePointerVar>): CArrayPointer<COpaquePointerVar> {
    val arena = Arena()
    arenaPointer.pointed.value = StableRef.create(arena).asCPointer()
    size.value = userList.size
    return arena.allocArray(userList.size) {
        // When acquiring cArray, you must transfer ownership so that list no longer holds a reference to items
        this.value = StableRef.create(userList.removeFirst()).asCPointer()
    }
}

fun freeUserCArray(cArray: CArrayPointer<COpaquePointerVar>, arenaPointer: CPointer<COpaquePointerVar>, size: Int) {
    for (i in 0 until size) {
        cArray[i]!!.asStableRef<User>().get().free()
    }
    // note: It is very important here that it must never be written as
    // arenaPointer.asStableRef<Arena>().get()
    // this doesn't cause compilation to fail, but a runtime error
    val arena = arenaPointer.pointed.value!!.asStableRef<Arena>().get()
    println("freeing arena")
    arena.clear()
}
