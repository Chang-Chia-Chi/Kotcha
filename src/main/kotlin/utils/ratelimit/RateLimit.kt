package utils.ratelimit

import jakarta.enterprise.util.Nonbinding
import jakarta.interceptor.InterceptorBinding
import java.time.temporal.ChronoUnit

@InterceptorBinding
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RateLimit(
    @get:Nonbinding val limit: Int = 100,
    @get:Nonbinding val window: Long = 1,
    @get:Nonbinding val minSpace: Long = 1,
    @get:Nonbinding val windowUnit: ChronoUnit = ChronoUnit.SECONDS,
    @get:Nonbinding val minSpaceUnit: ChronoUnit = ChronoUnit.SECONDS,
)
