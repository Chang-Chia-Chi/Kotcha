package model.leetcode

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SubmissionModel(
    @JsonProperty("id") val id: Int,
    @JsonProperty("date") val date: Long,
    @JsonProperty("question_id") val questionId: Int,
    @JsonProperty("submission_id") val submissionId: Long,
    @JsonProperty("status") val status: Int,
    @JsonProperty("contest_id") val contestId: Int,
    @JsonProperty("data_region") val dataRegion: String,
    @JsonProperty("fail_count") val failCount: Int,
    @JsonProperty("lang") val lang: String,
)
