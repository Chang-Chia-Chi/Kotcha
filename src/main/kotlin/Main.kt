import io.quarkus.logging.Log
import io.quarkus.runtime.QuarkusApplication
import io.quarkus.runtime.annotations.QuarkusMain
import kotlinx.coroutines.runBlocking
import service.LeetcodeService
import java.util.concurrent.atomic.AtomicInteger

@QuarkusMain
class Main(
    val leetcode: LeetcodeService,
) : QuarkusApplication {
    override fun run(vararg args: String?): Int {
        runBlocking {
            leetcode
//                .runCatching { fetchQuestions("weekly-contest-406") }
                .runCatching {
                    val counter = AtomicInteger(0)
                    val iter = fetchSubmissions("weekly-contest-406", listOf(1, 2, 3, 4, 5, 6, 7, 8), "global")
                    while (iter.hasNext()) {
                        Log.info("receive #${counter.incrementAndGet()} submission: [${iter.next()}]")
                    }
                }
//                .runCatching { fetchSubmittedCode("1320214961") }
                .onSuccess { println("fetch success") }
                .onFailure { Log.error("fetch failed", it) }
        }
        return 0
    }
}
