package sk.awisoft.sudokuplus.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.core.Cell
import sk.awisoft.sudokuplus.core.qqwing.GameType
import sk.awisoft.sudokuplus.core.utils.SudokuParser
import sk.awisoft.sudokuplus.data.datastore.AppSettingsManager
import sk.awisoft.sudokuplus.destinations.BackupScreenDestination
import sk.awisoft.sudokuplus.destinations.HomeScreenDestination
import sk.awisoft.sudokuplus.destinations.SettingsCategoriesScreenDestination
import sk.awisoft.sudokuplus.destinations.SettingsLanguageScreenDestination
import sk.awisoft.sudokuplus.ui.components.board.Board
import sk.awisoft.sudokuplus.ui.util.getCurrentLocaleString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sin
import kotlin.random.Random

@Destination
@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
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

    Scaffold { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Floating numbers background
            FloatingNumbersBackground(
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .systemBarsPadding(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Animated logo and title section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    AnimatedVisibility(
                        visible = showLogo,
                        enter = scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) + fadeIn()
                    ) {
                        AnimatedSudokuLogo(
                            modifier = Modifier.size(80.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AnimatedVisibility(
                        visible = showTitle,
                        enter = slideInVertically(
                            initialOffsetY = { -it / 2 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn()
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnimatedVisibility(
                            visible = showBoard,
                            enter = fadeIn() + scaleIn(initialScale = 0.9f)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

                        AnimatedVisibility(
                            visible = showButton,
                            enter = scaleIn(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ) + fadeIn()
                        ) {
                            Button(
                                onClick = {
                                    viewModel.setFirstLaunch()
                                    navigator.popBackStack()
                                    navigator.navigate(HomeScreenDestination())
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(stringResource(R.string.action_start))
                            }
                        }

                        // Staggered option items
                        AnimatedOptionItem(
                            visible = visibleItemIndex >= 1,
                            delayIndex = 0
                        ) {
                            ItemRowBigIcon(
                                title = stringResource(R.string.pref_app_language),
                                icon = Icons.Rounded.Language,
                                subtitle = currentLanguage,
                                onClick = { navigator.navigate(SettingsLanguageScreenDestination()) },
                            )
                        }

                        AnimatedOptionItem(
                            visible = visibleItemIndex >= 2,
                            delayIndex = 1
                        ) {
                            ItemRowBigIcon(
                                title = stringResource(R.string.onboard_restore_backup),
                                icon = Icons.Rounded.Restore,
                                subtitle = stringResource(R.string.onboard_restore_backup_description),
                                onClick = {
                                    navigator.navigate(BackupScreenDestination)
                                }
                            )
                        }

                        AnimatedOptionItem(
                            visible = visibleItemIndex >= 3,
                            delayIndex = 2
                        ) {
                            ItemRowBigIcon(
                                title = stringResource(R.string.settings_title),
                                icon = Icons.Rounded.Settings,
                                subtitle = stringResource(R.string.onboard_settings_description),
                                onClick = {
                                    navigator.navigate(SettingsCategoriesScreenDestination(false))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedOptionItem(
    visible: Boolean,
    delayIndex: Int,
    content: @Composable () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "option alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "option scale"
    )

    Box(
        modifier = Modifier
            .alpha(alpha)
            .scale(scale)
    ) {
        content()
    }
}

@Composable
private fun FloatingNumbersBackground(
    modifier: Modifier = Modifier
) {
    val numbers = remember {
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
        animationSpec = infiniteRepeatable(
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
                val paint = android.graphics.Paint().apply {
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
private fun AnimatedSudokuLogo(
    modifier: Modifier = Modifier
) {
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
        animationSpec = infiniteRepeatable(
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
        val cellOrder = listOf(
            Pair(1, 1), // Center first
            Pair(0, 0), Pair(0, 2), Pair(2, 0), Pair(2, 2), // Corners
            Pair(0, 1), Pair(1, 0), Pair(1, 2), Pair(2, 1)  // Edges
        )

        cellOrder.take(cellsRevealed).forEachIndexed { index, (row, col) ->
            val cellAlpha = if (index == cellsRevealed - 1) 0.7f else 0.4f
            drawRoundRect(
                color = primaryColor.copy(alpha = cellAlpha),
                topLeft = Offset(
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemRowBigIcon(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = { },
    onClick: () -> Unit = { },
    subtitle: String? = null,
    shape: Shape = MaterialTheme.shapes.large,
    onLongClick: ((() -> Unit))? = null,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    subtitleStyle: TextStyle = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp),
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    iconBackground: Color = MaterialTheme.colorScheme.secondaryContainer,
    iconSize: Dp = 42.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(containerColor)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier.background(
                        color = iconBackground,
                        shape = MaterialTheme.shapes.medium
                    )
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize)
                            .padding(6.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        style = titleStyle
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = subtitleStyle,
                            color = LocalContentColor.current.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            trailing()
        }
    }
}

@HiltViewModel
class WelcomeViewModel
@Inject constructor(
    private val settingsDataManager: AppSettingsManager
) : ViewModel() {
    var selectedCell by mutableStateOf(Cell(-1, -1, 0))

    // all heart shaped ❤
    val previewBoard = SudokuParser().parseBoard(
        board = listOf(
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
}