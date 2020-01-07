package permissions.dispatcher.ktx

import androidx.annotation.AnyThread
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@AnyThread
object RequestCodeProvider {
    private val requestCodeTable: MutableMap<Array<String>, AtomicInteger> = ConcurrentHashMap()

    fun nextRequestCode(permissions: Array<String>): Int {
        val code = requestCodeTable.get(key = permissions)
        return if (code == null) {
            val newCode = AtomicInteger(0)
            requestCodeTable[permissions] = newCode
            newCode.getAndIncrement()
        } else {
            code.getAndIncrement()
        }
    }
}
