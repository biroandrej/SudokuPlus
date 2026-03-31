package sk.awisoft.sudokuplus.data.database.repository

import java.time.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.seasonal.model.EventChallenge
import sk.awisoft.sudokuplus.core.seasonal.model.EventReward
import sk.awisoft.sudokuplus.core.seasonal.model.EventRewardType
import sk.awisoft.sudokuplus.core.seasonal.model.EventTheme
import sk.awisoft.sudokuplus.core.seasonal.model.EventType
import sk.awisoft.sudokuplus.core.seasonal.model.SeasonalEvent
import sk.awisoft.sudokuplus.data.database.model.SeasonalEventEntity

object SeasonalEventMapper {

    private val json = Json { ignoreUnknownKeys = true }

    fun toEntity(event: SeasonalEvent): SeasonalEventEntity = SeasonalEventEntity(
        id = event.id,
        title = event.title,
        description = event.description,
        eventType = event.eventType.value,
        startDate = event.startDate.toEpochDay(),
        endDate = event.endDate.toEpochDay(),
        themePrimaryColor = event.theme.primaryColor,
        themeSecondaryColor = event.theme.secondaryColor,
        themeBackgroundColor = event.theme.backgroundColor,
        themeAccentColor = event.theme.accentColor,
        challengesJson = json.encodeToString(event.challenges.map { it.toDto() }),
        rewardsJson = json.encodeToString(event.rewards.map { it.toDto() }),
        badgeId = event.badgeId,
        syncedAt = System.currentTimeMillis()
    )

    fun toDomain(entity: SeasonalEventEntity): SeasonalEvent = SeasonalEvent(
        id = entity.id,
        title = entity.title,
        description = entity.description,
        eventType = EventType.get(entity.eventType),
        startDate = LocalDate.ofEpochDay(entity.startDate),
        endDate = LocalDate.ofEpochDay(entity.endDate),
        theme = EventTheme(
            primaryColor = entity.themePrimaryColor,
            secondaryColor = entity.themeSecondaryColor,
            backgroundColor = entity.themeBackgroundColor,
            accentColor = entity.themeAccentColor
        ),
        challenges = json.decodeFromString<List<ChallengeDto>>(entity.challengesJson)
            .map { it.toDomain() },
        rewards = json.decodeFromString<List<RewardDto>>(entity.rewardsJson)
            .map { it.toDomain() },
        badgeId = entity.badgeId
    )

    @Serializable
    private data class ChallengeDto(
        val day: Int,
        val difficulty: String,
        val xpMultiplier: Double
    )

    @Serializable
    private data class RewardDto(
        val type: String,
        val amount: Int
    )

    private fun EventChallenge.toDto() = ChallengeDto(
        day = day,
        difficulty = difficulty.value,
        xpMultiplier = xpMultiplier
    )

    private fun ChallengeDto.toDomain() = EventChallenge(
        day = day,
        difficulty = GameDifficulty.get(difficulty),
        xpMultiplier = xpMultiplier
    )

    private fun EventReward.toDto() = RewardDto(
        type = type.value,
        amount = amount
    )

    private fun RewardDto.toDomain() = EventReward(
        type = EventRewardType.get(type),
        amount = amount
    )
}
