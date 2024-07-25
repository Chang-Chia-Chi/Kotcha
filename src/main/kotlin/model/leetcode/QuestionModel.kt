package model.leetcode

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class QuestionModel(
    @JsonProperty("id") val id: Int,
    @JsonProperty("question_id") val questionId: Int,
    @JsonProperty("credit") val credit: Int,
    @JsonProperty("title") val title: String,
    @JsonProperty("title_slug") val titleSlug: String,
    @JsonProperty("category_slug") val categorySlug: String,
)
