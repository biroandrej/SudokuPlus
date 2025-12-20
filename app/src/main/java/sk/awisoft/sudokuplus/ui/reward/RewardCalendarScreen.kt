package sk.awisoft.sudokuplus.ui.reward

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.core.reward.BadgeDefinition
import sk.awisoft.sudokuplus.core.reward.BadgeDefinitions
import sk.awisoft.sudokuplus.core.reward.BadgeRarity
import sk.awisoft.sudokuplus.core.reward.DailyReward
import sk.awisoft.sudokuplus.core.reward.RewardCalendarState
import sk.awisoft.sudokuplus.core.reward.RewardType
import sk.awisoft.sudokuplus.core.reward.RewardDefinitions
import sk.awisoft.sudokuplus.data.database.model.RewardBadge
import sk.awisoft.sudokuplus.ui.components.AnimatedNavigation
import sk.awisoft.sudokuplus.ui.theme.SudokuPlusTheme
import sk.awisoft.sudokuplus.ui.util.LightDarkPreview
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun RewardCalendarScreen(
    navigator: DestinationsNavigator,
    viewModel: RewardCalendarViewModel = hiltViewModel()
) {
    val calendarState by viewModel.calendarState.collectAsStateWithLifecycle()
    val isClaimingReward by viewModel.isClaimingReward.collectAsStateWithLifecycle()
    val earnedBadges by viewModel.earnedBadges.collectAsStateWithLifecycle()
    var showClaimDialog by remember { mutableStateOf(false) }
    var claimedReward by remember { mutableStateOf<DailyReward?>(null) }
    var earnedBadge by remember { mutableStateOf<BadgeDefinition?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is RewardCalendarViewModel.UiEvent.RewardClaimed -> {
                    claimedReward = event.reward
                    earnedBadge = event.earnedBadge
                    showClaimDialog = true
                }
                is RewardCalendarViewModel.UiEvent.AlreadyClaimed -> {
                    // Already claimed today
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.reward_calendar_title)) },
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->
        calendarState?.let { state ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Progress Section
                ProgressSection(state = state)

                Spacer(modifier = Modifier.height(16.dp))

                // Bonuses Section
                if (state.bonusHints > 0 || state.xpBoostGamesRemaining > 0) {
                    BonusesSection(
                        bonusHints = state.bonusHints,
                        xpBoostGames = state.xpBoostGamesRemaining
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Claim Button
                if (state.canClaimToday) {
                    ClaimButton(
                        reward = state.todayReward,
                        isLoading = isClaimingReward,
                        onClick = { viewModel.claimReward() }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Reward Calendar Grid
                RewardCalendarGrid(
                    rewards = viewModel.rewardCycle,
                    currentDay = state.currentDay,
                    canClaimToday = state.canClaimToday
                )

                // Earned Badges Section
                if (earnedBadges.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    EarnedBadgesSection(
                        earnedBadges = earnedBadges,
                        badgeDefinitions = viewModel.badgeDefinitions
                    )
                }
            }
        }
    }

    if (showClaimDialog && claimedReward != null) {
        RewardClaimDialog(
            reward = claimedReward!!,
            earnedBadge = earnedBadge,
            onDismiss = {
                showClaimDialog = false
                claimedReward = null
                earnedBadge = null
            }
        )
    }
}

@Composable
private fun ProgressSection(
    state: RewardCalendarState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.reward_calendar_day, state.currentDay),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = stringResource(R.string.reward_calendar_total_claimed, state.totalDaysClaimed),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { state.cycleProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                gapSize = 0.dp,
                drawStopIndicator = {}
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${state.currentDay}/30",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun BonusesSection(
    bonusHints: Int,
    xpBoostGames: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (bonusHints > 0) {
            BonusCard(
                icon = Icons.Rounded.TipsAndUpdates,
                label = stringResource(R.string.reward_bonus_hints_available, bonusHints),
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }
        if (xpBoostGames > 0) {
            BonusCard(
                icon = Icons.Rounded.Bolt,
                label = stringResource(R.string.reward_xp_boost_active, xpBoostGames),
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BonusCard(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = color
            )
        }
    }
}

@Composable
private fun ClaimButton(
    reward: DailyReward,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isLoading) 0.95f else 1f,
        animationSpec = tween(100),
        label = "claim_scale"
    )

    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.CardGiftcard,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.reward_calendar_claim),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(reward.rewardType.displayName),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun RewardCalendarGrid(
    rewards: List<DailyReward>,
    currentDay: Int,
    canClaimToday: Boolean,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(rewards) { reward ->
            val isClaimed = reward.day < currentDay
            val isToday = reward.day == currentDay
            val isLocked = reward.day > currentDay

            RewardDayCard(
                reward = reward,
                isClaimed = isClaimed,
                isToday = isToday,
                isLocked = isLocked,
                canClaim = isToday && canClaimToday
            )
        }
    }
}

@Composable
private fun RewardDayCard(
    reward: DailyReward,
    isClaimed: Boolean,
    isToday: Boolean,
    isLocked: Boolean,
    canClaim: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isClaimed -> MaterialTheme.colorScheme.primaryContainer
            isToday && canClaim -> MaterialTheme.colorScheme.tertiaryContainer
            isToday -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainerHigh
        },
        label = "bg_color"
    )

    val borderColor = if (isToday) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .then(
                if (isToday) {
                    Modifier.border(2.dp, borderColor, RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when {
                    isClaimed -> {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    isLocked -> {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = getRewardIcon(reward.rewardType),
                            contentDescription = null,
                            tint = if (reward.isSpecial) {
                                Color(0xFFFFD700)
                            } else {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = reward.day.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isClaimed -> MaterialTheme.colorScheme.onPrimaryContainer
                        isLocked -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        else -> MaterialTheme.colorScheme.onSecondaryContainer
                    }
                )
            }

            // Special day indicator
            if (reward.isSpecial && !isClaimed) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD700))
                )
            }
        }
    }
}

private fun getRewardIcon(rewardType: RewardType): ImageVector {
    return when (rewardType) {
        RewardType.HINTS -> Icons.Rounded.TipsAndUpdates
        RewardType.XP_BOOST -> Icons.Rounded.Bolt
        RewardType.BADGE -> Icons.Rounded.EmojiEvents
    }
}

@Composable
private fun EarnedBadgesSection(
    earnedBadges: List<RewardBadge>,
    badgeDefinitions: List<BadgeDefinition>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.badges_earned),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            earnedBadges.take(5).forEach { earnedBadge ->
                val definition = badgeDefinitions.find { it.id == earnedBadge.badgeId }
                if (definition != null) {
                    BadgeItem(
                        badge = definition,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BadgeItem(
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
        modifier = modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = badgeColor.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.EmojiEvents,
                    contentDescription = stringResource(badge.nameRes),
                    tint = badgeColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(badge.nameRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeColor,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun RewardCalendarScreenPreview() {
    val sampleState = RewardCalendarState(
        currentDay = 12,
        canClaimToday = true,
        todayReward = RewardDefinitions.getRewardForDay(12),
        bonusHints = 2,
        xpBoostGamesRemaining = 1,
        totalDaysClaimed = 11,
        cycleProgress = 12f / RewardDefinitions.CYCLE_LENGTH
    )
    val earnedBadges = listOf(
        RewardBadge(
            badgeId = BadgeDefinitions.all.first().id,
            earnedAt = ZonedDateTime.now(),
            cycleNumber = 1
        )
    )
    SudokuPlusTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                ProgressSection(state = sampleState)
                Spacer(modifier = Modifier.height(16.dp))
                BonusesSection(
                    bonusHints = sampleState.bonusHints,
                    xpBoostGames = sampleState.xpBoostGamesRemaining
                )
                Spacer(modifier = Modifier.height(16.dp))
                ClaimButton(
                    reward = sampleState.todayReward,
                    isLoading = false,
                    onClick = {}
                )
                Spacer(modifier = Modifier.height(16.dp))
                RewardCalendarGrid(
                    rewards = RewardDefinitions.rewardCycle,
                    currentDay = sampleState.currentDay,
                    canClaimToday = sampleState.canClaimToday
                )
                Spacer(modifier = Modifier.height(16.dp))
                EarnedBadgesSection(
                    earnedBadges = earnedBadges,
                    badgeDefinitions = BadgeDefinitions.all
                )
            }
        }
    }
}
