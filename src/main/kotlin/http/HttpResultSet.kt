package http

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.quarkus.logging.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.sync.Semaphore
import java.util.concurrent.atomic.AtomicBoolean

class HttpResultSet(
    private val client: HttpClient,
    private val endpoint: String,
    private val httpMethod: HttpMethod,
    private val parameters: List<Map<String, String>>,
    private val concurrency: Int,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
) : Iterator<HttpResponse>,
    AutoCloseable {
    private val dispatcher = Dispatchers.IO
    private val receiveChannel: ReceiveChannel<HttpResponse> = produce()
    private val isClose = AtomicBoolean(false)
    private var currItem: HttpResponse? = null

    override fun hasNext(): Boolean {
        if (isClosed()) return false
        if (currItem != null) return true

        currItem =
            runBlocking(Dispatchers.IO) {
                receiveChannel.receiveCatching().getOrNull()
            } ?: run {
                close()
                return false
            }

        return true
    }

    override fun next(): HttpResponse {
        if (isClosed() || !hasNext()) throw NoSuchElementException("http result set has no more element or closed")

        val nextItem = currItem
        currItem = null
        return nextItem!!
    }

    override fun close() {
        Log.info("start closing result set")
        isClose.set(true)
        receiveChannel.cancel()
        currItem = null
        Log.info("complete closing result set")
    }

    fun <T : Any> mapBy(mapper: HttpResponseMapper<T>): HttpResultIterator<T> =
        HttpResultIterator(
            rs = this,
            mapper = mapper,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun produce(): ReceiveChannel<HttpResponse> =
        scope.produce(dispatcher, capacity = concurrency) {
            val semaphore = Semaphore(concurrency)
            parameters
                .map { params ->
                    async {
                        try {
                            semaphore.acquire()
                            Log.info("start request [$httpMethod] [$endpoint] with params [$params]")
                            val response =
                                client.request(endpoint) {
                                    method = httpMethod
                                    params.forEach { (name, value) ->
                                        parameter(name, value)
                                    }
                                }
                            Log.info("complete request [$httpMethod] [$endpoint] with params [$params]")
                            send(response)
                        } catch (e: CancellationException) {
                            Log.error("producer cancelled when query api [$endpoint] with params [$params]", e)
                            throw e
                        } catch (e: Exception) {
                            Log.error("unknown error when query api [$endpoint] with params [$params]", e)
                            throw e
                        } finally {
                            semaphore.release()
                        }
                    }
                }.awaitAll()
        }

    private fun isClosed(): Boolean = isClose.get()
}
