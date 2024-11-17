package extension

import http.HttpStatement
import io.ktor.client.*
import io.ktor.http.*

fun HttpClient.createStatement(
    url: String,
    method: HttpMethod,
): HttpStatement =
    HttpStatement(
        client = this,
        url = url,
        method = method,
    )
