package sk.awisoft.sudokuplus.ui.onboarding

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.core.Cell
import sk.awisoft.sudokuplus.core.notification.DailyChallengeNotificationWorker
import sk.awisoft.sudokuplus.core.notification.StreakReminderWorker
import sk.awisoft.sudokuplus.core.qqwing.GameType
import sk.awisoft.sudokuplus.core.utils.SudokuParser
import sk.awisoft.sudokuplus.data.datastore.AppSettingsManager
import sk.awisoft.sudokuplus.data.datastore.NotificationSettingsManager
import sk.awisoft.sudokuplus.destinations.BackupScreenDestination
import sk.awisoft.sudokuplus.destinations.HomeScreenDestination
import sk.awisoft.sudokuplus.destinations.SettingsCategoriesScreenDestination
import sk.awisoft.sudokuplus.destinations.SettingsLanguageScreenDestination
import sk.awisoft.sudokuplus.ui.components.board.Board
import sk.awisoft.sudokuplus.ui.util.getCurrentLocaleString

@Destination<RootGraph>
@Composable
fun WelcomeScreen(viewModel: WelcomeViewModel = hiltViewModel(), navigator: DestinationsNavigator) {
    val context = LocalContext.current
    val currentLanguage by remember {
        mutableStateOf(
            getCurrentLocaleString(context)
        )
    }

    // Staggered animation states
    var showLogo by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showBoard by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }
    var visibleItemIndex by remember { mutableIntStateOf(0) }

    // Permission launcher for notifications
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            // Schedule notifications if permission was granted
            if (isGranted) {
                viewModel.scheduleNotifications()
            }
            // Navigate to home regardless of permission result
            viewModel.setFirstLaunch()
            navigator.popBackStack()
            navigator.navigate(HomeScreenDestination())
        }

    // Function to handle start button click
    fun onStartClick() {
        // On Android 13+, request notification permission first
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // On older Android versions, just schedule notifications and proceed
            viewModel.scheduleNotifications()
            viewModel.setFirstLaunch()
            navigator.popBackStack()
            navigator.navigate(HomeScreenDestination())
        }
    }

    LaunchedEffect(Unit) {
        delay(100)
        showLogo = true
        delay(300)
        showTitle = true
        delay(200)
        showBoard = true
        delay(300)
        showButton = true
        // Stagger the option items
        for (i in 1..3) {
            delay(150)
            visibleItemIndex = i
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showButton,
                enter =
                slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec =
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn()
            ) {
                Surface(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    tonalElevation = 3.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onStartClick() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.action_start))
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Floating numbers background
            FloatingNumbersBackground(
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WelcomeHeroCard(
                    showLogo = showLogo,
                    showTitle = showTitle,
                    modifier = Modifier.fillMaxWidth()
                )

                AnimatedVisibility(
                    visible = showBoard,
                    enter = fadeIn() + scaleIn(initialScale = 0.98f)
                ) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                        CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.intro_rules),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Board(
                                board = viewModel.previewBoard,
                                size = 9,
                                selectedCell = viewModel.selectedCell,
                                onClick = { cell -> viewModel.selectedCell = cell }
                            )
                        }
                    }
                }

                WelcomeQuickActions(
                    visibleItemIndex = visibleItemIndex,
                    currentLanguage = currentLanguage,
                    onLanguageClick = { navigator.navigate(SettingsLanguageScreenDestination()) },
                    onRestoreBackupClick = { navigator.navigate(BackupScreenDestination) },
                    onSettingsClick = {
                        navigator.navigate(
                            SettingsCategoriesScreenDestination(false)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun WelcomeHeroCard(showLogo: Boolean, showTitle: Boolean, modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    val gradient =
        remember(colorScheme.primaryContainer, colorScheme.secondaryContainer) {
            Brush.linearGradient(
                colors =
                listOf(
                    colorScheme.primaryContainer,
                    colorScheme.secondaryContainer
                )
            )
        }

    ElevatedCard(
        modifier = modifier,
        colors =
        CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Box(
            modifier =
            Modifier
                .background(gradient)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedVisibility(
                    visible = showLogo,
                    enter =
                    scaleIn(
                        animationSpec =
                        spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn()
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                    ) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            AnimatedSudokuLogo(
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = showTitle,
                    enter =
                    slideInVertically(
                        initialOffsetY = { -it / 2 },
                        animationSpec =
                        spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WelcomeQuickActions(
    visibleItemIndex: Int,
    currentLanguage: String,
    onLanguageClick: () -> Unit,
    onRestoreBackupClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 2
        ) {
            AnimatedVisibility(
                visible = visibleItemIndex >= 1,
                enter = fadeIn() + scaleIn(initialScale = 0.96f)
            ) {
                WelcomeActionTile(
                    title = stringResource(R.string.pref_app_language),
                    subtitle = currentLanguage,
                    icon = Icons.Rounded.Language,
                    onClick = onLanguageClick
                )
            }

            AnimatedVisibility(
                visible = visibleItemIndex >= 2,
                enter = fadeIn() + scaleIn(initialScale = 0.96f)
            ) {
                WelcomeActionTile(
                    title = stringResource(R.string.onboard_restore_backup),
                    subtitle = stringResource(R.string.onboard_restore_backup_description),
                    icon = Icons.Rounded.Restore,
                    onClick = onRestoreBackupClick
                )
            }

            AnimatedVisibility(
                visible = visibleItemIndex >= 3,
                enter = fadeIn() + scaleIn(initialScale = 0.96f)
            ) {
                WelcomeActionTile(
                    title = stringResource(R.string.settings_title),
                    subtitle = stringResource(R.string.onboard_settings_description),
                    icon = Icons.Rounded.Settings,
                    onClick = onSettingsClick
                )
            }
        }
    }
}

@Composable
private fun FlowRowScope.WelcomeActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.weight(1f),
        onClick = onClick,
        colors =
        CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier =
                Modifier
                    .size(44.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.large
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FloatingNumbersBackground(modifier: Modifier = Modifier) {
    val numbers =
        remember {
            List(15) {
                FloatingNumber(
                    value = Random.nextInt(1, 10),
                    x = Random.nextFloat(),
                    y = Random.nextFloat(),
                    size = Random.nextFloat() * 20f + 16f,
                    speed = Random.nextFloat() * 0.3f + 0.1f,
                    alpha = Random.nextFloat() * 0.08f + 0.02f
                )
            }
        }

    val infiniteTransition = rememberInfiniteTransition(label = "floating numbers")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
        infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "float offset"
    )

    val numberColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) {
        numbers.forEach { number ->
            val yOffset = ((number.y + animatedOffset * number.speed) % 1.2f) - 0.1f
            val xWave = sin(yOffset * 6f + number.x * 10f) * 0.02f

            drawContext.canvas.nativeCanvas.apply {
                val paint =
                    android.graphics.Paint().apply {
                        color = numberColor.copy(alpha = number.alpha).toArgb()
                        textSize = number.size * density
                        isAntiAlias = true
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                drawText(
                    number.value.toString(),
                    (number.x + xWave) * size.width,
                    yOffset * size.height,
                    paint
                )
            }
        }
    }
}

private data class FloatingNumber(
    val value: Int,
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val alpha: Float
)

@Composable
private fun AnimatedSudokuLogo(modifier: Modifier = Modifier) {
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val primaryColor = MaterialTheme.colorScheme.primary
    val accentColor = MaterialTheme.colorScheme.primaryContainer

    // Cell reveal animation
    var cellsRevealed by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        for (i in 1..9) {
            delay(80)
            cellsRevealed = i
        }
    }

    // Subtle pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "logo pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec =
        infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(
        modifier = modifier.scale(pulseScale)
    ) {
        val gridSize = size.minDimension
        val cellSize = gridSize / 3f
        val cornerRadius = 8f
        val padding = 4f

        // Draw background
        drawRoundRect(
            color = accentColor.copy(alpha = 0.3f),
            topLeft = Offset.Zero,
            size = Size(gridSize, gridSize),
            cornerRadius = CornerRadius(cornerRadius * 2, cornerRadius * 2)
        )

        // Draw cells that are revealed
        val cellOrder =
            listOf(
                Pair(1, 1), // Center first
                Pair(0, 0), Pair(0, 2), Pair(2, 0), Pair(2, 2), // Corners
                Pair(0, 1), Pair(1, 0), Pair(1, 2), Pair(2, 1) // Edges
            )

        cellOrder.take(cellsRevealed).forEachIndexed { index, (row, col) ->
            val cellAlpha = if (index == cellsRevealed - 1) 0.7f else 0.4f
            drawRoundRect(
                color = primaryColor.copy(alpha = cellAlpha),
                topLeft =
                Offset(
                    col * cellSize + padding,
                    row * cellSize + padding
                ),
                size = Size(cellSize - padding * 2, cellSize - padding * 2),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )
        }

        // Draw grid lines
        for (i in 1 until 3) {
            // Vertical
            drawLine(
                color = gridColor,
                start = Offset(i * cellSize, padding),
                end = Offset(i * cellSize, gridSize - padding),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
            // Horizontal
            drawLine(
                color = gridColor,
                start = Offset(padding, i * cellSize),
                end = Offset(gridSize - padding, i * cellSize),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }

        // Draw border
        drawRoundRect(
            color = primaryColor.copy(alpha = 0.8f),
            topLeft = Offset.Zero,
            size = Size(gridSize, gridSize),
            cornerRadius = CornerRadius(cornerRadius * 2, cornerRadius * 2),
            style = Stroke(width = 3f)
        )
    }
}

@HiltViewModel
class WelcomeViewModel
@Inject
constructor(
    private val settingsDataManager: AppSettingsManager,
    private val notificationSettingsManager: NotificationSettingsManager,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    var selectedCell by mutableStateOf(Cell(-1, -1, 0))

    // all heart shaped ❤
    val previewBoard =
        SudokuParser().parseBoard(
            board =
            listOf(
                "072000350340502018100030009800000003030000070050000020008000600000103000760050041",
                "017000230920608054400010009200000001060000020040000090002000800000503000390020047",
                "052000180480906023600020007500000008020000060030000090005000300000708000370060014",
                "025000860360208017700010003600000002040000090030000070006000100000507000490030058",
                "049000380280309056600050007300000002010000030070000090003000800000604000420080013",
                "071000420490802073300060009200000007060000090010000080007000900000703000130090068",
                "023000190150402086800050004700000008090000030080000010008000700000306000530070029",
                "097000280280706013300080007600000002040000060030000090001000400000105000860040051",
                "049000180160904023700010004200000008090000060080000050005000600000706000470020031"
            ).random(),
            gameType = GameType.Default9x9
        )

    fun setFirstLaunch(value: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsDataManager.setFirstLaunch(value)
        }
    }

    fun scheduleNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            // Get default times from settings
            val dailyHour = notificationSettingsManager.dailyChallengeNotificationHour.first()
            val dailyMinute = notificationSettingsManager.dailyChallengeNotificationMinute.first()
            val streakHour = notificationSettingsManager.streakReminderHour.first()
            val streakMinute = notificationSettingsManager.streakReminderMinute.first()

            // Schedule the notifications with default times
            DailyChallengeNotificationWorker.schedule(context, dailyHour, dailyMinute)
            StreakReminderWorker.schedule(context, streakHour, streakMinute)
        }
    }
}
