package sk.awisoft.sudokuplus.core.reward

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import sk.awisoft.sudokuplus.R

/**
 * Badge rarity determines the visual treatment and prestige
 */
enum class BadgeRarity(@StringRes val displayName: Int) {
    COMMON(R.string.badge_rarity_common),
    RARE(R.string.badge_rarity_rare),
    EPIC(R.string.badge_rarity_epic),
    LEGENDARY(R.string.badge_rarity_legendary)
}

/**
 * Definition of a badge that can be earned from the reward calendar
 */
data class BadgeDefinition(
    val id: String,
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val iconRes: Int,
    val rarity: BadgeRarity,
    val day: Int // Day in the 30-day cycle when this badge is awarded
)

/**
 * All available badges from the reward calendar
 */
object BadgeDefinitions {
    val all: List<BadgeDefinition> = listOf(
        // Week 1 Badge (Day 7)
        BadgeDefinition(
            id = "week_1_warrior",
            nameRes = R.string.badge_week_1_name,
            descriptionRes = R.string.badge_week_1_desc,
            iconRes = R.drawable.ic_badge_week_1,
            rarity = BadgeRarity.COMMON,
            day = 7
        ),
        // Week 2 Badge (Day 14)
        BadgeDefinition(
            id = "week_2_warrior",
            nameRes = R.string.badge_week_2_name,
            descriptionRes = R.string.badge_week_2_desc,
            iconRes = R.drawable.ic_badge_week_2,
            rarity = BadgeRarity.COMMON,
            day = 14
        ),
        // Week 3 Badge (Day 21)
        BadgeDefinition(
            id = "week_3_warrior",
            nameRes = R.string.badge_week_3_name,
            descriptionRes = R.string.badge_week_3_desc,
            iconRes = R.drawable.ic_badge_week_3,
            rarity = BadgeRarity.RARE,
            day = 21
        ),
        // Week 4 Badge (Day 28)
        BadgeDefinition(
            id = "week_4_warrior",
            nameRes = R.string.badge_week_4_name,
            descriptionRes = R.string.badge_week_4_desc,
            iconRes = R.drawable.ic_badge_week_4,
            rarity = BadgeRarity.EPIC,
            day = 28
        ),
        // Monthly Badge (Day 30)
        BadgeDefinition(
            id = "monthly_champion",
            nameRes = R.string.badge_monthly_name,
            descriptionRes = R.string.badge_monthly_desc,
            iconRes = R.drawable.ic_badge_monthly,
            rarity = BadgeRarity.LEGENDARY,
            day = 30
        )
    )

    fun getByDay(day: Int): BadgeDefinition? = all.find { it.day == day }
}
