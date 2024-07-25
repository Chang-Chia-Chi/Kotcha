package utils.ratelimit

import jakarta.annotation.Priority
import jakarta.interceptor.AroundInvoke
import jakarta.interceptor.Interceptor
import jakarta.interceptor.InvocationContext
import org.slf4j.LoggerFactory
import utils.ratelimit.algorithms.RollingWindow
import utils.ratelimit.enums.OverRateLimitType
import utils.ratelimit.exceptions.RateLimitAnnotationException
import utils.ratelimit.exceptions.RateLimitException
import java.time.temporal.ChronoUnit

@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@RateLimit
class RateLimitInterceptor {
    private val log = LoggerFactory.getLogger(RateLimitInterceptor::class.java)
    private val rateLimiter = RollingWindow()

    @AroundInvoke
    fun checkRateLimit(context: InvocationContext): Any =
        when (check(context)) {
            OverRateLimitType.OK -> context.proceed()
            OverRateLimitType.MINSPACE -> throw RateLimitException("Reach min space limit")
            OverRateLimitType.TIMEWINDOW -> throw RateLimitException("Reach window limit")
        }

    private fun check(context: InvocationContext): OverRateLimitType {
        context.method
            .getAnnotation(RateLimit::class.java)
            ?.apply(::checkAnnotationValid)
            ?.run { verify("${context.javaClass.name}.${context.method.name}", this) }
            ?.let { if (it != OverRateLimitType.OK) return it }

        context.method.declaringClass
            .getAnnotation(RateLimit::class.java)
            ?.apply(::checkAnnotationValid)
            ?.run { verify(context.javaClass.name, this) }
            ?.let { if (it != OverRateLimitType.OK) return it }

        return OverRateLimitType.OK
    }

    private fun verify(
        key: String,
        annotation: RateLimit,
    ): OverRateLimitType =
        rateLimiter.verify(
            key,
            annotation.limit,
            convertMills(
                annotation.window,
                annotation.windowUnit,
            ),
            convertMills(
                annotation.minSpace,
                annotation.minSpaceUnit,
            ),
        )

    private fun convertMills(
        time: Long,
        unit: ChronoUnit,
    ): Long = time * unit.duration.toMillis()

    private fun checkAnnotationValid(annotation: RateLimit) {
        fun validate(
            condition: Boolean,
            message: String,
        ) {
            if (!condition) throw RateLimitAnnotationException(message)
        }

        validate(annotation.limit > 0, "limit should be larger than 0")
        validate(convertMills(annotation.window, annotation.windowUnit) > 0, "window setting incorrect")
        validate(convertMills(annotation.minSpace, annotation.minSpaceUnit) > 0, "min space setting incorrect")
    }
}
