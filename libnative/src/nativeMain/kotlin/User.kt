import kotlinx.cinterop.CPointer
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import libapi.api_user

data class User(
    val name: Name,
    val age: Int,
) : Base<api_user>() {
    override var handler: CPointer<api_user> = arena.alloc<api_user>().apply {
        this.name = this@User.name.handler
        this.age = this@User.age
    }.ptr

    override fun toString(): String {
        return "name=$name, age=$age"
    }

    override fun free() {
        println("freeing user")
        name.free()
        super.free()
    }
}

internal val userList = mutableListOf<User>()
fun getUserList(size: CPointer<IntVar>) {
    size.pointed.value = userList.size

}

fun createUser(firstName: String, lastName: String, age: Int): CPointer<api_user> {
    return createUserForKClass(firstName, lastName, age).handler
}

fun createUserForKClass(firstName: String, lastName: String, age: Int): User {
    val name = Name(firstName, lastName)
    val user = User(name, age)
    userList.add(user)
    return user
}
