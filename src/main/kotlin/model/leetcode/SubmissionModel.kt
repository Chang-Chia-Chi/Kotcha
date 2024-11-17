package model.leetcode

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import http.HttpResponseMapper
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.quarkus.logging.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

@JsonIgnoreProperties(ignoreUnknown = true)
data class SubmissionModel(
    @JsonProperty("id") val id: String,
    @JsonProperty("date") val date: Long,
    @JsonProperty("question_id") val questionId: String,
    @JsonProperty("submission_id") val submissionId: Long,
    @JsonProperty("status") val status: Int,
    @JsonProperty("contest_id") val contestId: String,
    @JsonProperty("data_region") val dataRegion: String,
    @JsonProperty("fail_count") val failCount: Int,
    @JsonProperty("lang") val lang: String,
)

object SubmissionModelMapper : HttpResponseMapper<SubmissionModel> {
    private val objectMapper =
        ObjectMapper()
            .apply { configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) }

    override fun map(response: HttpResponse): List<SubmissionModel> {
        val body: String = runBlocking(Dispatchers.IO) { response.body() }
        Log.info("response status code [${response.status}] with body: [$body]")
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
