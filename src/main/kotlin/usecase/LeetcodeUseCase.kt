package usecase

import enums.QuestionLevelEnum
import jakarta.enterprise.context.ApplicationScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import service.LeetcodeService

@ApplicationScoped
class LeetcodeUseCase(
    val leetcodeService: LeetcodeService,
) {
    private val contestRankingPageSize = 25

    suspend fun fetchSubmittedCodes(
        name: String,
        top: Int,
        level: List<QuestionLevelEnum>,
    ) {
        val questions = CoroutineScope(Dispatchers.IO).async { leetcodeService.fetchQuestions(name) }.await().associateBy { it.questionId }
    }
}
