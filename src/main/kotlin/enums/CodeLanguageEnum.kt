package enums

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
enum class CodeLanguageEnum(
    val value: String,
) {
    @JsonProperty("python3")
    PYTHON3("python3"),

    @JsonProperty("cpp")
    CPP("cpp"),
}
