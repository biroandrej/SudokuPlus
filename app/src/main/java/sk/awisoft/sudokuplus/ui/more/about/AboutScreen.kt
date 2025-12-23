package sk.awisoft.sudokuplus.ui.more.about

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import sk.awisoft.sudokuplus.BuildConfig
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.core.DEVELOPER_EMAIL
import sk.awisoft.sudokuplus.core.GITHUB_REPOSITORY
import sk.awisoft.sudokuplus.destinations.AboutLibrariesScreenDestination
import sk.awisoft.sudokuplus.ui.components.AnimatedNavigation
import sk.awisoft.sudokuplus.ui.theme.ColorUtils.harmonizeWithPrimary
import sk.awisoft.sudokuplus.util.FlavorUtil

@Destination<RootGraph>(style = AnimatedNavigation::class)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AboutScreen(navigator: DestinationsNavigator) {
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.about_title)) },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_round_arrow_back_24),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier =
            Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Icon(
                modifier =
                Modifier
                    .padding(12.dp)
                    .align(Alignment.CenterHorizontally)
                    .size(48.dp),
                painter = painterResource(R.drawable.ic_app_icon),
                tint = Color.Unspecified,
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier =
                Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier =
                Modifier
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    stringResource(
                        R.string.app_version,
                        BuildConfig.VERSION_NAME + if (FlavorUtil.isFoss()) "-FOSS" else "",
                        BuildConfig.VERSION_CODE
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }

            FlowRow(
                modifier =
                Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 3
            ) {
                AboutSectionBox(
                    title = stringResource(R.string.about_github_project),
                    subtitle = stringResource(R.string.about_github_source_code),
                    icon = ImageVector.vectorResource(R.drawable.ic_github_24dp),
                    onClick = { uriHandler.openUri(GITHUB_REPOSITORY) }
                )
                AboutSectionBox(
                    title = stringResource(R.string.libraries_licenses),
                    subtitle = stringResource(R.string.libraries_licenses_title),
                    icon = Icons.Outlined.Info,
                    onClick = { navigator.navigate(AboutLibrariesScreenDestination()) }
                )
                AboutSectionBox(
                    title = stringResource(R.string.about_contact_developer),
                    subtitle = stringResource(R.string.about_contact_developer_summary),
                    icon = Icons.Outlined.Email,
                    onClick = { uriHandler.openUri("mailto:$DEVELOPER_EMAIL") }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRowScope.AboutSectionBox(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    additionalContent: @Composable (ColumnScope.() -> Unit)? = null
) {
    Box(
        modifier =
        modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .fillMaxRowHeight()
            .weight(1f)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.harmonizeWithPrimary(),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = subtitle
                )
            }
            if (additionalContent != null) {
                additionalContent()
            }
        }
    }
}
