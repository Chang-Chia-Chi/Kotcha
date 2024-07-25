import io.quarkus.runtime.QuarkusApplication
import io.quarkus.runtime.annotations.QuarkusMain
import kotlinx.coroutines.runBlocking
import service.LeetcodeApi

@QuarkusMain
class Main(
    val leetcode: LeetcodeApi,
) : QuarkusApplication {
    override fun run(vararg args: String?): Int {
        runBlocking {
            repeat(4) {
                leetcode
                    //            .runCatching { fetchQuestions("weekly-contest-406") }
                    .runCatching { fetchSubmissions("weekly-contest-406", 1, "global") }
                    .onSuccess { println("fetch success: $it") }
                    .onFailure { println("fetch failed, $it") }
            }
        }
        return 0
    }
}
