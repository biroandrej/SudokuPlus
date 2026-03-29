package sk.awisoft.sudokuplus.ui.seasonal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.core.seasonal.model.EventChallenge
import sk.awisoft.sudokuplus.core.seasonal.model.EventStatus
import sk.awisoft.sudokuplus.ui.components.AnimatedNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    navigator: DestinationsNavigator,
    viewModel: EventsViewModel = hiltViewModel()
) {
    val selectedEvent by viewModel.selectedEvent.collectAsStateWithLifecycle()

    LaunchedEffect(eventId) {
        if (selectedEvent == null || selectedEvent?.event?.id != eventId) {
            viewModel.allEvents.value.find { it.id == eventId }?.let {
                viewModel.selectEvent(it)
            }
        }
    }

    val eventWithProgress = selectedEvent

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        eventWithProgress?.event?.title
                            ?: stringResource(R.string.seasonal_events_title)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->
        if (eventWithProgress == null) {
            Box(
                modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val event = eventWithProgress.event
        val progress = eventWithProgress.progress
        val currentDay = viewModel.getCurrentDay(event)
        val challengesCompleted = progress?.challengesCompleted ?: 0
        val completionPercent = viewModel.getCompletionPercentage(event, challengesCompleted)

        LazyColumn(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Progress header
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text =
                                stringResource(
                                    R.string.seasonal_event_progress,
                                    challengesCompleted,
                                    event.challenges.size
                                ),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$completionPercent%",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        LinearProgressIndicator(
                            progress = { completionPercent / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                    }
                }
            }

            // Challenge list
            items(event.challenges, key = { it.day }) { challenge ->
                ChallengeItem(
                    challenge = challenge,
                    currentDay = currentDay,
                    isCompleted = challenge.day <= (progress?.lastChallengeDay ?: 0),
                    isActive = event.status is EventStatus.Active,
                    onPlay = { /* Will be wired when event gameplay is connected */ }
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun ChallengeItem(
    challenge: EventChallenge,
    currentDay: Int,
    isCompleted: Boolean,
    isActive: Boolean,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPlayable = isActive && challenge.day <= currentDay && !isCompleted
    val isLocked = challenge.day > currentDay && !isCompleted

    val containerColor =
        when {
            isCompleted -> MaterialTheme.colorScheme.primaryContainer
            isPlayable -> MaterialTheme.colorScheme.surfaceContainerHigh
            else -> MaterialTheme.colorScheme.surfaceContainerLow
        }
    val contentColor =
        when {
            isCompleted -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurface
        }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors =
        CardDefaults.elevatedCardColors(
            containerColor = containerColor
        )
    ) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Day badge
            Box(
                modifier =
                Modifier
                    .size(40.dp)
                    .background(
                        color =
                        when {
                            isCompleted -> MaterialTheme.colorScheme.primary
                            isPlayable -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.outlineVariant
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = challenge.day.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color =
                        if (isLocked) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onPrimary
                        }
                    )
                }
            }

            // Challenge info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.seasonal_event_day, challenge.day),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
                Text(
                    text = stringResource(challenge.difficulty.resName),
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }

            // Action
            when {
                isCompleted -> {
                    FilledTonalButton(onClick = {}, enabled = false) {
                        Icon(
                            Icons.Rounded.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(stringResource(R.string.seasonal_event_completed))
                    }
                }

                isPlayable -> {
                    Button(onClick = onPlay) {
                        Icon(
                            Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(stringResource(R.string.seasonal_event_play))
                    }
                }

                isLocked -> {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
