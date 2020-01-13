package permissions.dispatcher.ktx

import androidx.annotation.AnyThread
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@AnyThread
object RequestCodeProvider {
    private val requestCodeTable: MutableMap<Array<out String>, AtomicInteger> = ConcurrentHashMap()

    fun nextRequestCode(key: Array<out String>): Int {
        val code = requestCodeTable[key]
        return if (code == null) {
            val newCode = AtomicInteger(0)
            requestCodeTable[key] = newCode
            newCode.getAndIncrement()
        } else {
            code.getAndIncrement()
        }
    }
}
