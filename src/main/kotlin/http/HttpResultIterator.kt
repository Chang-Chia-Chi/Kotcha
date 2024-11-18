package http

import io.quarkus.logging.Log
import java.util.concurrent.atomic.AtomicBoolean

class HttpResultIterator<T>(
    private val rs: HttpResultSet,
    private val mapper: HttpResponseMapper<T>,
) : Iterator<T>,
    AutoCloseable {
    private var currIdx: Int = -1
    private var currItems: List<T>? = null
    private val isClosed = AtomicBoolean(false)

    override fun hasNext(): Boolean {
        if (isClosed()) return false
        if (currItemsHasNext()) return true

        if (rs.hasNext()) {
            val response = rs.next()
            currItems = mapper.map(response)
            currIdx = 0
            return true
        }
        close()
        return false
    }

    override fun next(): T {
        if (isClosed()) throw NoSuchElementException("http result iterator has closed")

        if (currItemsHasNext() || hasNext()) {
            val nextItem = currItems?.get(currIdx)
            currIdx++
            return nextItem!!
        }
        throw NoSuchElementException("http result iterator has no more element")
    }

    override fun close() {
        if (isClosed()) Log.warn("http result iterator already closed")

        Log.info("start closing iterator")
        isClosed.set(true)
        currItems = null
        currIdx = -1
        Log.info("complete closing iterator")
    }

    private fun currItemsHasNext(): Boolean = currItems != null && currIdx >= 0 && currIdx < currItems!!.size

    private fun isClosed(): Boolean = isClosed.get()
}
