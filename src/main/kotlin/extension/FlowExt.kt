package extension

import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Semaphore

fun <T, R> Flow<T>.mapAsync(
    concurrency: Int,
    buffer: Int = concurrency,
    transform: suspend (T) -> R,
): Flow<R> {
    require(concurrency > 0) { "Concurrency should be positive but was $concurrency" }
    require(buffer > 1) { "Buffer size should be > 1 but was $buffer" }
    if (concurrency == 1) return map { transform(it) }
    return channelFlow {
        val semaphore = Semaphore(concurrency)
        collect { value ->
            semaphore.acquire()

            val transDeferred =
                async {
                    try {
                        transform(value)
                    } finally {
                        semaphore.release()
                    }
                }

            transDeferred.invokeOnCompletion { exception ->
                if (exception != null) throw exception
            }

            send(transDeferred)
        }
    }.buffer(buffer)
        .map { it.await() }
}
