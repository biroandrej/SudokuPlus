package sk.awisoft.sudokuplus.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.ZonedDateTime
import kotlinx.serialization.Serializable
import sk.awisoft.sudokuplus.core.reward.RewardType
import sk.awisoft.sudokuplus.data.backup.serializer.LocalDateLongSerializer
import sk.awisoft.sudokuplus.data.backup.serializer.ZonedDateTimeLongSerializer

/**
 * Tracks user's login reward progress
 */
@Serializable
@Entity(tableName = "login_reward_status")
data class LoginRewardStatus(
    @PrimaryKey
    val id: Int = 1,
    @ColumnInfo(name = "current_day")
    val currentDay: Int = 1,
    @ColumnInfo(name = "last_claim_date")
    @Serializable(with = LocalDateLongSerializer::class)
    val lastClaimDate: LocalDate? = null,
    @ColumnInfo(name = "cycle_start_date")
    @Serializable(with = LocalDateLongSerializer::class)
    val cycleStartDate: LocalDate = LocalDate.now(),
    @ColumnInfo(name = "total_days_claimed")
    val totalDaysClaimed: Int = 0,
    @ColumnInfo(name = "bonus_hints")
    val bonusHints: Int = 0,
    @ColumnInfo(name = "xp_boost_games_remaining")
    val xpBoostGamesRemaining: Int = 0
)

/**
 * Represents a claimed reward in history
 */
@Serializable
@Entity(tableName = "claimed_rewards")
data class ClaimedReward(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "day")
    val day: Int,
    @ColumnInfo(name = "reward_type")
    val rewardType: RewardType,
    @ColumnInfo(name = "amount")
    val amount: Int,
    @ColumnInfo(name = "claimed_at")
    @Serializable(with = ZonedDateTimeLongSerializer::class)
    val claimedAt: ZonedDateTime = ZonedDateTime.now()
)
