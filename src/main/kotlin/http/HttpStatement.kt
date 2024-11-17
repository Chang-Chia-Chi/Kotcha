package http

import io.ktor.client.*
import io.ktor.http.*

class HttpStatement(
    val client: HttpClient,
    val url: String,
    val method: HttpMethod = HttpMethod.Get,
    val concurrency: Int = 4,
) {
    private val parameters = mutableListOf<Map<String, String>>()

    fun addParameters(params: List<Map<String, String>>): HttpStatement =
        apply {
            parameters.addAll(params)
        }

    fun executeStatement(): HttpResultSet =
        HttpResultSet(
            client = client,
            endpoint = url,
            httpMethod = method,
            parameters = this.parameters,
            concurrency = concurrency,
        )
}
