package sk.awisoft.sudokuplus.ui.game

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Grade
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.div
import kotlin.time.toKotlinDuration
import kotlinx.coroutines.delay
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.core.PreferencesConstants
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType
import sk.awisoft.sudokuplus.core.utils.toFormattedString
import sk.awisoft.sudokuplus.data.database.model.Record
import sk.awisoft.sudokuplus.ui.components.ConfettiEffect

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AfterGameStats(
    difficulty: GameDifficulty,
    type: GameType,
    hintsUsed: Int,
    mistakesMade: Int,
    mistakesLimit: Boolean,
    mistakesLimitCount: Int,
    giveUp: Boolean,
    notesTaken: Int,
    records: List<Record>,
    timeText: String,
    modifier: Modifier = Modifier
) {
    // Animation states for staggered reveal
    var showTitle by remember { mutableStateOf(false) }
    var showTimeSection by remember { mutableStateOf(false) }
    var showStatsSection by remember { mutableStateOf(false) }
    var visibleStatIndex by remember { mutableIntStateOf(0) }
    var showConfetti by remember { mutableStateOf(!giveUp) }

    // Title scale animation for victory
    val titleScale by animateFloatAsState(
        targetValue = if (showTitle) 1f else 0.5f,
        animationSpec =
        spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "title scale"
    )

    // Staggered animation sequence
    LaunchedEffect(Unit) {
        delay(100)
        showTitle = true
        delay(400)
        showTimeSection = true
        delay(300)
        showStatsSection = true
        // Stagger individual stat items
        for (i in 0..3) {
            delay(150)
            visibleStatIndex = i + 1
        }
    }

    Box(modifier = modifier) {
        // Confetti overlay for victory
        if (showConfetti && !giveUp) {
            ConfettiEffect(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                particleCount = 40,
                durationMillis = 2500,
                onComplete = { showConfetti = false }
            )
        }

        Column {
            // Victory/Defeat title with animation
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn() + scaleIn(initialScale = 0.5f)
            ) {
                Row(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!giveUp) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier =
                            Modifier
                                .padding(end = 8.dp)
                                .scale(titleScale)
                        )
                    }
                    Text(
                        text =
                        if (giveUp) {
                            if (mistakesLimit && mistakesLimitCount >= PreferencesConstants.MISTAKES_LIMIT) {
                                stringResource(R.string.saved_game_mistakes_limit)
                            } else {
                                stringResource(R.string.saved_game_give_up)
                            }
                        } else {
                            stringResource(R.string.game_completed)
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (giveUp) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.scale(titleScale)
                    )
                }
            }

            // Time section with slide-in animation
            if (!giveUp) {
                AnimatedVisibility(
                    visible = showTimeSection,
                    enter = fadeIn() + slideInVertically { -it / 2 }
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.time),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AnimatedStatBox(
                                visible = true,
                                text = {
                                    Text(
                                        stringResource(
                                            R.string.stat_time_current,
                                            timeText
                                        )
                                    )
                                }
                            )

                            if (records.isNotEmpty()) {
                                AnimatedStatBox(
                                    visible = true,
                                    text = {
                                        Text(
                                            text =
                                            stringResource(
                                                R.string.stat_time_average,
                                                DateUtils.formatElapsedTime(
                                                    records.sumOf { it.time.seconds } / records.count()
                                                )
                                            )
                                        )
                                    }
                                )
                                AnimatedStatBox(
                                    visible = true,
                                    text = {
                                        Text(
                                            text =
                                            stringResource(
                                                R.string.stat_time_best,
                                                records.first().time
                                                    .toKotlinDuration()
                                                    .toFormattedString()
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Statistics section with staggered items
            AnimatedVisibility(
                visible = showStatsSection,
                enter = fadeIn() + slideInVertically { -it / 2 }
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.statistics),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnimatedStatBox(
                            visible = visibleStatIndex >= 1,
                            text = {
                                Text(
                                    "${stringResource(difficulty.resName)} ${
                                        stringResource(
                                            type.resName
                                        )
                                    }"
                                )
                            },
                            icon = { Icon(Icons.Rounded.Grade, contentDescription = null) }
                        )
                        AnimatedStatBox(
                            visible = visibleStatIndex >= 2,
                            text = {
                                Text(
                                    stringResource(
                                        R.string.hints_used,
                                        hintsUsed
                                    )
                                )
                            },
                            icon = { Icon(Icons.Rounded.Lightbulb, contentDescription = null) }
                        )
                        AnimatedStatBox(
                            visible = visibleStatIndex >= 3,
                            text = {
                                Text(
                                    stringResource(
                                        R.string.mistakes_made,
                                        mistakesMade
                                    )
                                )
                            },
                            icon = { Icon(Icons.Rounded.Cancel, contentDescription = null) }
                        )
                        AnimatedStatBox(
                            visible = visibleStatIndex >= 4,
                            text = {
                                Text(
                                    stringResource(
                                        R.string.notes_taken,
                                        notesTaken
                                    )
                                )
                            },
                            icon = { Icon(Icons.Rounded.Edit, contentDescription = null) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedStatBox(
    visible: Boolean,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = { }
) {
    AnimatedVisibility(
        visible = visible,
        enter =
        fadeIn() +
            scaleIn(
                initialScale = 0.8f,
                animationSpec =
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
    ) {
        StatBox(
            text = text,
            icon = icon,
            modifier = modifier
        )
    }
}

@Composable
fun StatBox(
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = { }
) {
    Box(
        modifier =
        modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            text()
        }
    }
}
