package sk.awisoft.sudokuplus.ui.reward

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.core.reward.BadgeDefinition
import sk.awisoft.sudokuplus.core.reward.BadgeRarity
import sk.awisoft.sudokuplus.core.reward.DailyReward
import sk.awisoft.sudokuplus.core.reward.RewardType

@Composable
fun RewardClaimDialog(
    reward: DailyReward,
    earnedBadge: BadgeDefinition? = null,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .scale(scale.value),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon with animated background
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = getRewardColor(reward.rewardType).copy(alpha = 0.15f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getRewardIcon(reward.rewardType),
                        contentDescription = null,
                        tint = getRewardColor(reward.rewardType),
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.reward_claim_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.reward_claim_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Reward details
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = getRewardColor(reward.rewardType).copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(reward.rewardType.displayName),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = getRewardColor(reward.rewardType)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = getRewardAmountText(reward),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Badge earned section
                if (earnedBadge != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    BadgeEarnedSection(badge = earnedBadge)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.dialog_ok),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun BadgeEarnedSection(
    badge: BadgeDefinition,
    modifier: Modifier = Modifier
) {
    val badgeColor = when (badge.rarity) {
        BadgeRarity.COMMON -> Color(0xFF78909C)
        BadgeRarity.RARE -> Color(0xFF42A5F5)
        BadgeRarity.EPIC -> Color(0xFFAB47BC)
        BadgeRarity.LEGENDARY -> Color(0xFFFFD700)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = badgeColor.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.badge_earned),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = badgeColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Icon(
                imageVector = Icons.Rounded.EmojiEvents,
                contentDescription = null,
                tint = badgeColor,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(badge.nameRes),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(badge.descriptionRes),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun getRewardAmountText(reward: DailyReward): String {
    return when (reward.rewardType) {
        RewardType.HINTS -> stringResource(R.string.reward_hints_amount, reward.amount)
        RewardType.XP_BOOST -> stringResource(R.string.reward_xp_boost_amount, reward.amount)
        RewardType.BADGE -> stringResource(R.string.reward_type_badge)
    }
}

private fun getRewardIcon(rewardType: RewardType): ImageVector {
    return when (rewardType) {
        RewardType.HINTS -> Icons.Rounded.TipsAndUpdates
        RewardType.XP_BOOST -> Icons.Rounded.Bolt
        RewardType.BADGE -> Icons.Rounded.EmojiEvents
    }
}

private fun getRewardColor(rewardType: RewardType): Color {
    return when (rewardType) {
        RewardType.HINTS -> Color(0xFF4CAF50)
        RewardType.XP_BOOST -> Color(0xFFFF9800)
        RewardType.BADGE -> Color(0xFFFFD700)
    }
}
