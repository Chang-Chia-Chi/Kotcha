package model.leetcode

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import enums.QuestionLevelEnum

@JsonIgnoreProperties(ignoreUnknown = true)
data class QuestionModel(
    @JsonProperty("id") val id: String,
    @JsonProperty("question_id") val questionId: String,
    @JsonProperty("credit") val credit: Int,
    @JsonProperty("title") val title: String,
    @JsonProperty("title_slug") val titleSlug: String,
    @JsonProperty("category_slug") val categorySlug: String,
) {
    fun level(): QuestionLevelEnum =
        when {
            credit < 4 -> QuestionLevelEnum.EASY
            credit in 4..5 -> QuestionLevelEnum.MEDIUM
            else -> QuestionLevelEnum.HARD
        }
}
