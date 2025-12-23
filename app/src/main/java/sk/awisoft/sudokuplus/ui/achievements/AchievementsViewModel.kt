package sk.awisoft.sudokuplus.ui.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sk.awisoft.sudokuplus.core.achievement.AchievementDefinitions
import sk.awisoft.sudokuplus.core.achievement.AchievementEngine
import sk.awisoft.sudokuplus.data.database.model.AchievementCategory
import sk.awisoft.sudokuplus.data.database.model.AchievementDefinition
import sk.awisoft.sudokuplus.data.database.model.UserAchievement
import sk.awisoft.sudokuplus.domain.repository.AchievementRepository

data class AchievementWithProgress(
    val definition: AchievementDefinition,
    val userAchievement: UserAchievement?,
    val progressPercent: Float
) {
    val isUnlocked: Boolean
        get() = userAchievement?.isUnlocked == true

    val currentProgress: Int
        get() = userAchievement?.progress ?: 0

    val requiredProgress: Int
        get() = AchievementDefinitions.getRequirementValue(definition.requirement)
}

@HiltViewModel
class AchievementsViewModel
@Inject
constructor(
    private val achievementRepository: AchievementRepository,
    private val achievementEngine: AchievementEngine
) : ViewModel() {
    private val _selectedCategory = MutableStateFlow<AchievementCategory?>(null)
    val selectedCategory: StateFlow<AchievementCategory?> = _selectedCategory.asStateFlow()

    val categories: List<AchievementCategory> = AchievementCategory.entries

    val achievements: StateFlow<List<AchievementWithProgress>> =
        achievementRepository.getAll()
            .combine(_selectedCategory) { userAchievements, category ->
                val userMap = userAchievements.associateBy { it.achievementId }

                AchievementDefinitions.all
                    .filter { category == null || it.category == category }
                    .map { definition ->
                        val userAchievement = userMap[definition.id]
                        val required = AchievementDefinitions.getRequirementValue(
                            definition.requirement
                        )
                        val progress = userAchievement?.progress ?: 0
                        val percent =
                            if (userAchievement?.isUnlocked == true) {
                                1f
                            } else {
                                (progress.toFloat() / required).coerceIn(0f, 1f)
                            }

                        AchievementWithProgress(
                            definition = definition,
                            userAchievement = userAchievement,
                            progressPercent = percent
                        )
                    }
                    .sortedWith(
                        compareByDescending<AchievementWithProgress> { it.isUnlocked }
                            .thenByDescending { it.progressPercent }
                            .thenBy { it.definition.tier.ordinal }
                    )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unlockedCount: StateFlow<Int> =
        achievementRepository.getUnlockedCount()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCount: Int = AchievementDefinitions.all.size

    fun selectCategory(category: AchievementCategory?) {
        _selectedCategory.value = category
    }

    fun recalculateProgress() {
        viewModelScope.launch(Dispatchers.IO) {
            achievementEngine.recalculateAllProgress()
        }
    }
}
