package sk.awisoft.sudokuplus.ui.more

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.core.update.Release
import sk.awisoft.sudokuplus.core.update.UpdateUtil
import sk.awisoft.sudokuplus.destinations.AboutScreenDestination
import sk.awisoft.sudokuplus.destinations.AutoUpdateScreenDestination
import sk.awisoft.sudokuplus.destinations.BackupScreenDestination
import sk.awisoft.sudokuplus.destinations.FoldersScreenDestination
import sk.awisoft.sudokuplus.destinations.LearnScreenDestination
import sk.awisoft.sudokuplus.destinations.SettingsCategoriesScreenDestination
import sk.awisoft.sudokuplus.ui.components.AnimatedNavigation
import sk.awisoft.sudokuplus.ui.components.PreferenceRow
import sk.awisoft.sudokuplus.ui.components.ScrollbarLazyColumn
import sk.awisoft.sudokuplus.ui.settings.autoupdate.UpdateChannel
import sk.awisoft.sudokuplus.ui.theme.RoundedPolygonShape
import sk.awisoft.sudokuplus.util.FlavorUtil
import com.materialkolor.ktx.blend
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Destination(style = AnimatedNavigation::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    navigator: DestinationsNavigator,
    viewModel: MoreViewModel = hiltViewModel()
) {
    val autoUpdateChannel by viewModel.updateChannel.collectAsStateWithLifecycle(UpdateChannel.Disabled)
    val updateDismissedName by viewModel.updateDismissedName.collectAsStateWithLifecycle("")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        ScrollbarLazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        PreferenceRow(
                            title = stringResource(R.string.settings_title),
                            subtitle = stringResource(R.string.perf_appearance_summary),
                            painter = painterResource(R.drawable.ic_settings_24),
                            onClick = { navigator.navigate(SettingsCategoriesScreenDestination()) },
                            shape = MaterialTheme.shapes.large
                        )
                    }
                }
            }

            item {
                MoreSectionHeader(title = stringResource(R.string.backup_restore_title))
            }
            item {
                MoreQuickActionsRow(
                    leftTitle = stringResource(R.string.backup_restore_title),
                    leftPainter = rememberVectorPainter(image = Icons.Rounded.SettingsBackupRestore),
                    onLeftClick = { navigator.navigate(BackupScreenDestination()) },
                    rightTitle = stringResource(R.string.title_folders),
                    rightPainter = rememberVectorPainter(Icons.Outlined.Folder),
                    onRightClick = { navigator.navigate(FoldersScreenDestination()) },
                )
            }

            item {
                MoreSectionHeader(title = stringResource(R.string.more_title))
            }
            item {
                MoreQuickActionsRow(
                    leftTitle = stringResource(R.string.learn_screen_title),
                    leftPainter = painterResource(R.drawable.ic_outline_help_outline_24),
                    onLeftClick = { navigator.navigate(LearnScreenDestination()) },
                    rightTitle = stringResource(R.string.about_title),
                    rightPainter = painterResource(R.drawable.ic_outline_info_24),
                    onRightClick = { navigator.navigate(AboutScreenDestination()) },
                )
            }

            if (!FlavorUtil.isFoss()) {
                item {
                    AnimatedVisibility(autoUpdateChannel != UpdateChannel.Disabled) {
                        var latestRelease by remember { mutableStateOf<Release?>(null) }
                        LaunchedEffect(Unit) {
                            if (latestRelease == null) {
                                withContext(Dispatchers.IO) {
                                    runCatching {
                                        latestRelease =
                                            UpdateUtil.checkForUpdate(autoUpdateChannel == UpdateChannel.Beta)
                                    }
                                }
                            }
                        }
                        latestRelease?.let { release ->
                            AnimatedVisibility(
                                visible = release.name.toString() != updateDismissedName,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                UpdateFoundBox(
                                    versionToUpdate = release.name ?: "?",
                                    onClick = { navigator.navigate(AutoUpdateScreenDestination()) },
                                    onDismissed = { viewModel.dismissUpdate(release) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MoreSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier.padding(top = 4.dp),
    )
}

@Composable
private fun MoreQuickActionsRow(
    leftTitle: String,
    leftPainter: Painter,
    onLeftClick: () -> Unit,
    rightTitle: String,
    rightPainter: Painter,
    onRightClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp),

    ) {
        ElevatedCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            onClick = onLeftClick,
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = leftPainter, contentDescription = null)
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = leftTitle,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }

        ElevatedCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            onClick = onRightClick,
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(painter = rightPainter, contentDescription = null)
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = rightTitle,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
fun UpdateFoundBox(
    versionToUpdate: String,
    onClick: () -> Unit,
    onDismissed: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = with(MaterialTheme.colorScheme) {
        primaryContainer
    }
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .background(color = containerColor)
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 20000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ), label = ""
        )
        val shape = remember {
            RoundedPolygonShape(
                RoundedPolygon.star(
                    numVerticesPerRadius = 10,
                    innerRadius = 0.8f,
                    rounding = CornerRounding(0.3f)
                )
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(50.dp)
                .offset((-10).dp, 10.dp)
                .rotate(rotation)
                .border(
                    width = 3.dp,
                    color = with(MaterialTheme.colorScheme) {
                        primary
                            .blend(onPrimaryContainer)
                            .copy(alpha = 0.5f)
                            .compositeOver(surface)
                    },
                    shape = shape
                )
        )
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onPrimaryContainer) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = stringResource(R.string.update_found_version, versionToUpdate),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismissed) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.new_update_found_action),
                    style = MaterialTheme.typography.titleSmall,
                    color = LocalContentColor.current
                        .copy(alpha = 0.75f)
                )
            }
        }
    }
}
