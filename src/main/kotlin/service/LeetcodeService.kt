package service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import http.HttpStatement
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import jakarta.enterprise.context.ApplicationScoped
import kotlinx.io.IOException
import model.leetcode.CodeModel
import model.leetcode.QuestionModel
import model.leetcode.SubmissionModel
import model.leetcode.SubmissionModelMapper
import service.config.LeetcodeConfig

// @RateLimit(limit = 120, window = 60, minSpace = 0, windowUnit = ChronoUnit.SECONDS, minSpaceUnit = ChronoUnit.MILLIS)
@ApplicationScoped
class LeetcodeService(
    val config: LeetcodeConfig,
) {
    private val client =
        HttpClient(CIO) {
            expectSuccess = true
            defaultRequest {
                headers {
                    append("CSRFTOKEN", config.csfrtoken())
                    append("LEETCODE_SESSION", config.session())
                }
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
                connectTimeoutMillis = 5000
                socketTimeoutMillis = 5000
            }
            install(HttpRequestRetry) {
                maxRetries = 3
                retryOnExceptionIf { _, cause ->
                    cause is IOException
                }
                exponentialDelay()
            }
        }

    private val objectMapper =
        ObjectMapper()
            .apply { configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) }

    suspend fun fetchQuestions(name: String): List<QuestionModel> {
        val body: String =
            client
                .get("https://leetcode.com/contest/api/info/$name")
                .body()
        return objectMapper
            .readTree(body)
            .get("questions")
            .map { node -> objectMapper.treeToValue(node, QuestionModel::class.java) }
    }

    suspend fun fetchSubmittedCode(id: String): CodeModel {
        val body: String =
            client
                .get("https://leetcode.com/api/submissions/$id")
                .body()
        return objectMapper.readValue(body, CodeModel::class.java)
    }

    suspend fun fetchSubmissions(
        name: String,
        paginations: List<Int>,
        region: String = "global",
    ): Iterator<SubmissionModel> {
        val statement =
            HttpStatement(
                client = client,
                url = "https://leetcode.com/contest/api/ranking/$name/",
                method = HttpMethod.Get,
            ).addParameters(
                paginations.map {
                    mapOf(
                        "pagination" to it.toString(),
                        "region" to region,
                    )
                },
            )
        return statement
            .executeStatement()
            .mapBy(SubmissionModelMapper)
            .iterator()
    }
}
