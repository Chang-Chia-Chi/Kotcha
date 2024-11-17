package service.config

import io.smallrye.config.ConfigMapping

@ConfigMapping(prefix = "leetcode")
interface LeetcodeConfig {
    fun csfrtoken(): String

    fun session(): String
}
