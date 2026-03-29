package sk.awisoft.sudokuplus.ui.seasonal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.core.seasonal.model.EventStatus
import sk.awisoft.sudokuplus.core.seasonal.model.SeasonalEvent
import sk.awisoft.sudokuplus.destinations.EventDetailScreenDestination
import sk.awisoft.sudokuplus.ui.components.AnimatedNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun EventsScreen(
    navigator: DestinationsNavigator,
    viewModel: EventsViewModel = hiltViewModel()
) {
    val activeEvents by viewModel.activeEvents.collectAsStateWithLifecycle()
    val upcomingEvents by viewModel.upcomingEvents.collectAsStateWithLifecycle()
    val allEvents by viewModel.allEvents.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.syncEvents() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.seasonal_events_title)) },
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->
        if (isLoading && allEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (allEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.seasonal_event_no_events),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (activeEvents.isNotEmpty()) {
                    item {
                        SectionHeader(stringResource(R.string.seasonal_event_active))
                    }
                    items(activeEvents, key = { it.id }) { event ->
                        EventCard(
                            event = event,
                            onClick = {
                                viewModel.selectEvent(event)
                                navigator.navigate(EventDetailScreenDestination(eventId = event.id))
                            }
                        )
                    }
                }

                if (upcomingEvents.isNotEmpty()) {
                    item {
                        SectionHeader(stringResource(R.string.seasonal_event_upcoming))
                    }
                    items(upcomingEvents, key = { it.id }) { event ->
                        EventCard(
                            event = event,
                            onClick = {
                                viewModel.selectEvent(event)
                                navigator.navigate(EventDetailScreenDestination(eventId = event.id))
                            }
                        )
                    }
                }

                val endedEvents = allEvents.filter { it.status is EventStatus.Ended }
                if (endedEvents.isNotEmpty()) {
                    item {
                        SectionHeader(stringResource(R.string.seasonal_event_ended))
                    }
                    items(endedEvents, key = { it.id }) { event ->
                        EventCard(
                            event = event,
                            onClick = {
                                viewModel.selectEvent(event)
                                navigator.navigate(EventDetailScreenDestination(eventId = event.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun EventCard(
    event: SeasonalEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val seasonalColors = SeasonalColors.forEventType(event.eventType)
    val isActive = event.status is EventStatus.Active
    val isEnded = event.status is EventStatus.Ended

    ElevatedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isActive) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        tint = seasonalColors.primary
                    )
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                EventStatusBadge(event)
            }

            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isActive) {
                val today = LocalDate.now()
                val daysLeft = ChronoUnit.DAYS.between(today, event.endDate).toInt()
                val progress = ChronoUnit.DAYS.between(event.startDate, today).toFloat() /
                    ChronoUnit.DAYS.between(event.startDate, event.endDate).toFloat()

                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = seasonalColors.primary,
                    trackColor = seasonalColors.primary.copy(alpha = 0.2f)
                )

                Text(
                    text = stringResource(R.string.seasonal_event_ends_in, daysLeft),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (!isEnded) {
                val today = LocalDate.now()
                val daysUntil = ChronoUnit.DAYS.between(today, event.startDate).toInt()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.seasonal_event_starts_in, daysUntil),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun EventStatusBadge(event: SeasonalEvent, modifier: Modifier = Modifier) {
    val (text, color) = when (event.status) {
        is EventStatus.Active -> stringResource(R.string.seasonal_event_active) to
            MaterialTheme.colorScheme.primary
        is EventStatus.Upcoming -> stringResource(R.string.seasonal_event_upcoming) to
            MaterialTheme.colorScheme.tertiary
        is EventStatus.Ended -> stringResource(R.string.seasonal_event_ended) to
            MaterialTheme.colorScheme.outline
    }

    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}
