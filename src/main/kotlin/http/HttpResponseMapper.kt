package http

import io.ktor.client.statement.*

interface HttpResponseMapper<T> {
    fun map(response: HttpResponse): List<T>
}
