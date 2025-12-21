package sk.awisoft.sudokuplus.ui.settings.notifications

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.ui.components.AnimatedNavigation
import sk.awisoft.sudokuplus.ui.components.GrantPermissionCard
import sk.awisoft.sudokuplus.ui.components.PreferenceRow
import sk.awisoft.sudokuplus.ui.components.PreferenceRowSwitch
import sk.awisoft.sudokuplus.ui.components.ScrollbarLazyColumn
import sk.awisoft.sudokuplus.ui.settings.SettingsScaffoldLazyColumn

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun SettingsNotificationsScreen(
    viewModel: SettingsNotificationsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current

    val dailyChallengeEnabled by viewModel.dailyChallengeEnabled.collectAsStateWithLifecycle(initialValue = false)
    val dailyChallengeTime by viewModel.dailyChallengeTime.collectAsStateWithLifecycle(initialValue = Pair(8, 0))

    val streakReminderEnabled by viewModel.streakReminderEnabled.collectAsStateWithLifecycle(initialValue = false)
    val streakReminderTime by viewModel.streakReminderTime.collectAsStateWithLifecycle(initialValue = Pair(20, 0))

    var hasPermission by remember { mutableStateOf(viewModel.hasNotificationPermission()) }
    var showDailyChallengeTimePicker by rememberSaveable { mutableStateOf(false) }
    var showStreakReminderTimePicker by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    SettingsScaffoldLazyColumn(
        titleText = stringResource(R.string.notifications_title),
        navigator = navigator
    ) { paddingValues ->
        ScrollbarLazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            // Permission card for Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasPermission) {
                item {
                    GrantPermissionCard(
                        title = stringResource(R.string.notifications_permission_required),
                        details = stringResource(R.string.notifications_permission_rationale),
                        painter = rememberVectorPainter(image = Icons.Outlined.Notifications),
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(stringResource(R.string.action_grant))
                            }
                        },
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp, top = 8.dp)
                    )
                }
            }

            // Daily Challenge Notification
            item {
                PreferenceRowSwitch(
                    title = stringResource(R.string.notifications_daily_challenge),
                    subtitle = stringResource(R.string.notifications_daily_challenge_desc),
                    checked = dailyChallengeEnabled,
                    enabled = hasPermission || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU,
                    onClick = {
                        viewModel.updateDailyChallengeEnabled(!dailyChallengeEnabled)
                    },
                    painter = rememberVectorPainter(Icons.Outlined.NotificationsActive)
                )
            }

            item {
                PreferenceRow(
                    title = stringResource(R.string.notifications_daily_challenge_time),
                    subtitle = formatTime(dailyChallengeTime.first, dailyChallengeTime.second),
                    enabled = dailyChallengeEnabled && (hasPermission || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU),
                    onClick = { showDailyChallengeTimePicker = true },
                    painter = rememberVectorPainter(Icons.Outlined.Schedule)
                )
            }

            // Streak Reminder
            item {
                PreferenceRowSwitch(
                    title = stringResource(R.string.notifications_streak_reminder),
                    subtitle = stringResource(R.string.notifications_streak_reminder_desc),
                    checked = streakReminderEnabled,
                    enabled = hasPermission || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU,
                    onClick = {
                        viewModel.updateStreakReminderEnabled(!streakReminderEnabled)
                    },
                    painter = rememberVectorPainter(Icons.Outlined.NotificationsActive)
                )
            }

            item {
                PreferenceRow(
                    title = stringResource(R.string.notifications_streak_reminder_time),
                    subtitle = formatTime(streakReminderTime.first, streakReminderTime.second),
                    enabled = streakReminderEnabled && (hasPermission || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU),
                    onClick = { showStreakReminderTimePicker = true },
                    painter = rememberVectorPainter(Icons.Outlined.Schedule)
                )
            }
        }
    }

    // Time Picker Dialogs
    if (showDailyChallengeTimePicker) {
        TimePickerDialog(
            initialHour = dailyChallengeTime.first,
            initialMinute = dailyChallengeTime.second,
            onConfirm = { hour, minute ->
                viewModel.updateDailyChallengeTime(hour, minute)
                showDailyChallengeTimePicker = false
            },
            onDismiss = { showDailyChallengeTimePicker = false }
        )
    }

    if (showStreakReminderTimePicker) {
        TimePickerDialog(
            initialHour = streakReminderTime.first,
            initialMinute = streakReminderTime.second,
            onConfirm = { hour, minute ->
                viewModel.updateStreakReminderTime(hour, minute)
                showStreakReminderTimePicker = false
            },
            onDismiss = { showStreakReminderTimePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(timePickerState.hour, timePickerState.minute)
                }
            ) {
                Text(stringResource(R.string.dialog_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

private fun formatTime(hour: Int, minute: Int): String {
    return String.format("%02d:%02d", hour, minute)
}
