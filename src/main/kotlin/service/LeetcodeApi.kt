package service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import jakarta.enterprise.context.ApplicationScoped
import model.leetcode.QuestionModel
import model.leetcode.SubmissionModel
import utils.ratelimit.RateLimit
import java.time.temporal.ChronoUnit

@ApplicationScoped
@RateLimit(limit = 120, window = 60, minSpace = 200, windowUnit = ChronoUnit.SECONDS, minSpaceUnit = ChronoUnit.MILLIS)
class LeetcodeApi {
    private val client =
        HttpClient(CIO) {
            expectSuccess = true
            defaultRequest {
                headers {
                    append("CSRFTOKEN", "")
                    append("LEETCODE_SESSION", "")
                }
            }
        }

    private val objectMapper =
        ObjectMapper()
            .also { it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) }

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

    suspend fun fetchSubmissions(
        name: String,
        pagination: Int,
        region: String,
    ): List<SubmissionModel> {
        val body: String =
            client
                .get("https://leetcode.com/contest/api/ranking/$name/?pagination=$pagination&region=$region")
                .body()
        return objectMapper
            .readTree(body)
            .get("submissions")
            .map { node ->
                objectMapper.treeToValue(
                    node.fields().next().value,
                    SubmissionModel::class.java,
                )
            }
    }
}
