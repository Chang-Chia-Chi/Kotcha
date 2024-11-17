package model.leetcode

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import enums.CodeLanguageEnum

@JsonIgnoreProperties(ignoreUnknown = true)
data class CodeModel(
    @JsonProperty("id") val id: String,
    @JsonProperty("code") val code: String = "",
    @JsonProperty("contest_submission") val contestSubmission: String = "",
    @JsonProperty("lang") val lang: CodeLanguageEnum = CodeLanguageEnum.CPP,
)
