package sk.awisoft.sudokuplus.ui.more

import androidx.compose.foundation.background
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.destinations.AboutScreenDestination
import sk.awisoft.sudokuplus.destinations.BackupScreenDestination
import sk.awisoft.sudokuplus.destinations.FoldersScreenDestination
import sk.awisoft.sudokuplus.destinations.LearnScreenDestination
import sk.awisoft.sudokuplus.destinations.SettingsCategoriesScreenDestination
import sk.awisoft.sudokuplus.ui.components.AnimatedNavigation
import sk.awisoft.sudokuplus.ui.components.PreferenceRow
import sk.awisoft.sudokuplus.ui.components.ScrollbarLazyColumn
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(style = AnimatedNavigation::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    navigator: DestinationsNavigator
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
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
