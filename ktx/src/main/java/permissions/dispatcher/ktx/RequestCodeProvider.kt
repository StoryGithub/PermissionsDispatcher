package permissions.dispatcher.ktx

import androidx.annotation.AnyThread
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger

@AnyThread
internal object RequestCodeProvider {
    private val requestCodeTable: ConcurrentMap<Array<out String>, AtomicInteger> = ConcurrentHashMap()

    fun containsKey(key: Array<out String>): Boolean = requestCodeTable.containsKey(key)

    fun getAndIncrement(key: Array<out String>): Int {
        val code = requestCodeTable[key]
        return if (code == null) {
            val newCode = AtomicInteger(0)
            requestCodeTable.putIfAbsent(key, newCode)
            newCode.getAndIncrement()
        } else {
            code.getAndIncrement()
        }
    }
}
