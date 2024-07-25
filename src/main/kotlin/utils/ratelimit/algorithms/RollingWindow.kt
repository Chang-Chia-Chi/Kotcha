package utils.ratelimit.algorithms

import kotlinx.datetime.Clock
import utils.ratelimit.enums.OverRateLimitType
import java.util.*
import kotlin.math.max

class RollingWindow {
    private val requestHistory = HashMap<String, PriorityQueue<Long>>()

    private val clock: () -> Long = { Clock.System.now().toEpochMilliseconds() }

    private var maxEventTimeMills: Long = -1

    @Synchronized
    fun verify(
        key: String,
        limit: Int,
        timeWindowMills: Long,
        minSpaceMills: Long,
    ): OverRateLimitType {
        val now = clock()
        if (maxEventTimeMills > 0 && now - maxEventTimeMills >= minSpaceMills) return OverRateLimitType.MINSPACE

        val queue = requestHistory.getOrPut(key) { PriorityQueue<Long>() }
        while (queue.isNotEmpty() && queue.peek() < now - timeWindowMills) queue.poll()
        if (queue.size >= limit) return OverRateLimitType.TIMEWINDOW

        queue.add(now)
        maxEventTimeMills = max(maxEventTimeMills, now)
        return OverRateLimitType.OK
    }
}
