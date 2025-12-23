package sk.awisoft.sudokuplus.ui.settings.appearance

import android.os.Build
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.time.ZonedDateTime
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.Locale
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.core.PreferencesConstants
import sk.awisoft.sudokuplus.data.datastore.AppSettingsManager
import sk.awisoft.sudokuplus.destinations.SettingsBoardThemeDestination
import sk.awisoft.sudokuplus.ui.components.AnimatedNavigation
import sk.awisoft.sudokuplus.ui.components.PreferenceRow
import sk.awisoft.sudokuplus.ui.components.PreferenceRowSwitch
import sk.awisoft.sudokuplus.ui.components.ScrollbarLazyColumn
import sk.awisoft.sudokuplus.ui.settings.DateFormatDialog
import sk.awisoft.sudokuplus.ui.settings.SelectionDialog
import sk.awisoft.sudokuplus.ui.settings.SetDateFormatPatternDialog
import sk.awisoft.sudokuplus.ui.settings.SettingsScaffoldLazyColumn

@Destination<RootGraph>(style = AnimatedNavigation::class)
@OptIn(ExperimentalStdlibApi::class)
@Composable
fun SettingsAppearanceScreen(
    viewModel: SettingsAppearanceViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    var darkModeDialog by rememberSaveable { mutableStateOf(false) }
    var dateFormatDialog by rememberSaveable { mutableStateOf(false) }
    var customFormatDialog by rememberSaveable { mutableStateOf(false) }

    val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.DEFAULT_DARK_THEME
    )
    val dateFormat by viewModel.dateFormat.collectAsStateWithLifecycle(initialValue = "")
    val dynamicColors by viewModel.dynamicColors.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.DEFAULT_DYNAMIC_COLORS
    )
    val amoledBlack by viewModel.amoledBlack.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.DEFAULT_AMOLED_BLACK
    )

    SettingsScaffoldLazyColumn(
        titleText = stringResource(R.string.pref_appearance),
        navigator = navigator
    ) { paddingValues ->
        ScrollbarLazyColumn(
            modifier =
            Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            item {
                PreferenceRow(
                    title = stringResource(R.string.pref_dark_theme),
                    subtitle =
                    when (darkTheme) {
                        0 -> stringResource(R.string.pref_dark_theme_follow)
                        1 -> stringResource(R.string.pref_dark_theme_off)
                        2 -> stringResource(R.string.pref_dark_theme_on)
                        else -> ""
                    },
                    onClick = { darkModeDialog = true },
                    painter = rememberVectorPainter(Icons.Outlined.DarkMode)
                )
            }

            item {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PreferenceRowSwitch(
                        title = stringResource(R.string.pref_dynamic_colors_title),
                        subtitle = stringResource(R.string.pref_dynamic_colors_summary),
                        checked = dynamicColors,
                        onClick = { viewModel.updateDynamicColors(!dynamicColors) },
                        painter = rememberVectorPainter(Icons.Outlined.DarkMode)
                    )
                }
            }
            item {
                PreferenceRowSwitch(
                    title = stringResource(R.string.pref_pure_black),
                    checked = amoledBlack,
                    onClick = {
                        viewModel.updateAmoledBlack(!amoledBlack)
                    },
                    painter = rememberVectorPainter(Icons.Outlined.Contrast)
                )
            }
            item {
                PreferenceRow(
                    title = stringResource(R.string.pref_board_theme_title),
                    subtitle = stringResource(R.string.pref_board_theme_summary),
                    onClick = {
                        navigator.navigate(SettingsBoardThemeDestination())
                    },
                    painter = rememberVectorPainter(Icons.Outlined.Tag)
                )
            }
            item {
                PreferenceRow(
                    title = stringResource(R.string.pref_date_format),
                    subtitle = "${dateFormat.ifEmpty { stringResource(R.string.label_default) }} (${
                        ZonedDateTime.now()
                            .format(AppSettingsManager.dateFormat(dateFormat))
                    })",
                    onClick = { dateFormatDialog = true },
                    painter = rememberVectorPainter(Icons.Outlined.EditCalendar)
                )
            }
        }
    }

    if (darkModeDialog) {
        SelectionDialog(
            title = stringResource(R.string.pref_dark_theme),
            selections =
            listOf(
                stringResource(R.string.pref_dark_theme_follow),
                stringResource(R.string.pref_dark_theme_off),
                stringResource(R.string.pref_dark_theme_on)
            ),
            selected = darkTheme,
            onSelect = { index ->
                viewModel.updateDarkTheme(index)
            },
            onDismiss = { darkModeDialog = false }
        )
    } else if (dateFormatDialog) {
        DateFormatDialog(
            title = stringResource(R.string.pref_date_format),
            entries =
            DateFormats.associateWith { dateFormatEntry ->
                val dateString =
                    ZonedDateTime.now().format(
                        when (dateFormatEntry) {
                            "" -> {
                                DateTimeFormatter.ofPattern(
                                    DateTimeFormatterBuilder.getLocalizedDateTimePattern(
                                        FormatStyle.SHORT,
                                        null,
                                        IsoChronology.INSTANCE,
                                        Locale.getDefault()
                                    )
                                )
                            }

                            else -> {
                                DateTimeFormatter.ofPattern(dateFormatEntry)
                            }
                        }
                    )
                "${dateFormatEntry.ifEmpty {
                    stringResource(
                        R.string.label_default
                    )
                }} ($dateString)"
            },
            customDateFormatText =
            if (!DateFormats.contains(dateFormat)) {
                "$dateFormat (${
                    ZonedDateTime.now().format(DateTimeFormatter.ofPattern(dateFormat))
                })"
            } else {
                stringResource(R.string.pref_date_format_custom_label)
            },
            selected = dateFormat,
            onSelect = { format ->
                if (format == "custom") {
                    customFormatDialog = true
                } else {
                    viewModel.updateDateFormat(format)
                }
                dateFormatDialog = false
            },
            onDismiss = { dateFormatDialog = false }
        )
    }

    if (customFormatDialog) {
        var customDateFormat by rememberSaveable {
            mutableStateOf(
                if (DateFormats.contains(
                        dateFormat
                    )
                ) {
                    ""
                } else {
                    dateFormat
                }
            )
        }
        var invalidCustomDateFormat by rememberSaveable { mutableStateOf(false) }
        var dateFormatPreview by rememberSaveable { mutableStateOf("") }

        SetDateFormatPatternDialog(
            onConfirm = {
                if (viewModel.checkCustomDateFormat(customDateFormat)) {
                    viewModel.updateDateFormat(customDateFormat)
                    invalidCustomDateFormat = false
                    customFormatDialog = false
                } else {
                    invalidCustomDateFormat = true
                }
            },
            onDismissRequest = { customFormatDialog = false },
            onTextValueChange = { text ->
                customDateFormat = text
                if (invalidCustomDateFormat) invalidCustomDateFormat = false

                dateFormatPreview =
                    if (viewModel.checkCustomDateFormat(customDateFormat)) {
                        ZonedDateTime.now()
                            .format(DateTimeFormatter.ofPattern(customDateFormat))
                    } else {
                        ""
                    }
            },
            customDateFormat = customDateFormat,
            invalidCustomDateFormat = invalidCustomDateFormat,
            datePreview = dateFormatPreview
        )
    }
}

private val DateFormats =
    listOf(
        "",
        "dd/MM/yy",
        "dd.MM.yy",
        "MM/dd/yy",
        "yyyy-MM-dd",
        "dd MMM yyyy",
        "MMM dd, yyyy"
    )
