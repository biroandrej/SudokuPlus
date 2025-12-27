package sk.awisoft.sudokuplus

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sk.awisoft.sudokuplus.core.PreferencesConstants
import sk.awisoft.sudokuplus.data.datastore.AppSettingsManager
import sk.awisoft.sudokuplus.data.datastore.PlayGamesSettingsManager
import sk.awisoft.sudokuplus.data.datastore.ThemeSettingsManager
import sk.awisoft.sudokuplus.destinations.HomeScreenDestination
import sk.awisoft.sudokuplus.destinations.ImportFromFileScreenDestination
import sk.awisoft.sudokuplus.destinations.MoreScreenDestination
import sk.awisoft.sudokuplus.destinations.StatisticsScreenDestination
import sk.awisoft.sudokuplus.destinations.WelcomeScreenDestination
import sk.awisoft.sudokuplus.playgames.PlayGamesManager
import sk.awisoft.sudokuplus.ui.components.navigation_bar.NavigationBarComponent
import sk.awisoft.sudokuplus.ui.theme.BoardColors
import sk.awisoft.sudokuplus.ui.theme.SudokuBoardColorsImpl
import sk.awisoft.sudokuplus.ui.theme.SudokuPlusTheme

val LocalBoardColors = staticCompositionLocalOf { SudokuBoardColorsImpl() }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var settings: AppSettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before super.onCreate()
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Track whether settings have been loaded
        var settingsLoaded = false

        // Keep splash screen visible until theme settings are loaded
        splashScreen.setKeepOnScreenCondition { !settingsLoaded }

        setContent {
            val mainViewModel: MainActivityViewModel = hiltViewModel()

            // Collect combined theme settings - null until all settings are loaded
            val themeSettings by mainViewModel.themeSettings.collectAsStateWithLifecycle()
            val firstLaunch by mainViewModel.firstLaunch.collectAsStateWithLifecycle(false)

            // Wait for theme settings to load before rendering
            val settings = themeSettings ?: return@setContent

            // Mark settings as loaded to dismiss splash screen
            settingsLoaded = true

            SudokuPlusTheme(
                darkTheme =
                when (settings.darkTheme) {
                    1 -> false
                    2 -> true
                    else -> isSystemInDarkTheme()
                },
                dynamicColor = settings.dynamicColors,
                amoled = settings.amoledBlack,
                colorSeed = Color(PreferencesConstants.DEFAULT_THEME_SEED_COLOR)
            ) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()

                var bottomBarState by rememberSaveable { mutableStateOf(false) }

                LaunchedEffect(navBackStackEntry) {
                    bottomBarState =
                        when (navBackStackEntry?.destination?.route) {
                            StatisticsScreenDestination.route, HomeScreenDestination.route, MoreScreenDestination.route -> true
                            else -> false
                        }
                }
                LaunchedEffect(firstLaunch) {
                    if (firstLaunch) {
                        navController.navigate(
                            route = WelcomeScreenDestination.route,
                            navOptions =
                            navOptions {
                                popUpTo(HomeScreenDestination.route) {
                                    inclusive = true
                                }
                            }
                        )
                    }
                }

                // Handle file import deep link (ACTION_VIEW intent)
                var handledDeepLink by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    if (!handledDeepLink && intent?.action == Intent.ACTION_VIEW) {
                        intent?.data?.let { uri ->
                            handledDeepLink = true
                            navController.navigate(
                                ImportFromFileScreenDestination(
                                    fileUri = uri.toString(),
                                    fromDeepLink = true
                                ).route
                            )
                        }
                    }
                }

                val resolvedDarkTheme =
                    when (settings.darkTheme) {
                        1 -> false
                        2 -> true
                        else -> isSystemInDarkTheme()
                    }

                // Update status bar icons based on theme
                SideEffect {
                    val insetsController = WindowCompat.getInsetsController(
                        window,
                        window.decorView
                    )
                    insetsController.isAppearanceLightStatusBars = !resolvedDarkTheme
                    insetsController.isAppearanceLightNavigationBars = !resolvedDarkTheme
                }

                val boardColors =
                    if (settings.monetSudokuBoard) {
                        SudokuBoardColorsImpl(
                            foregroundColor = BoardColors.foregroundColor(resolvedDarkTheme),
                            notesColor = BoardColors.notesColor(resolvedDarkTheme),
                            altForegroundColor = BoardColors.altForegroundColor(),
                            errorColor = BoardColors.errorColor(),
                            highlightColor = BoardColors.highlightColor(resolvedDarkTheme),
                            thickLineColor = BoardColors.thickLineColor(resolvedDarkTheme),
                            thinLineColor = BoardColors.thinLineColor(resolvedDarkTheme)
                        )
                    } else {
                        SudokuBoardColorsImpl(
                            foregroundColor = MaterialTheme.colorScheme.onSurface,
                            notesColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                            altForegroundColor = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.7f
                            ),
                            errorColor = BoardColors.errorColor(),
                            highlightColor = MaterialTheme.colorScheme.outline,
                            thickLineColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.55f),
                            thinLineColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.25f)
                        )
                    }
                CompositionLocalProvider(LocalBoardColors provides boardColors) {
                    Scaffold(
                        bottomBar = {
                            NavigationBarComponent(
                                navController = navController,
                                isVisible = bottomBarState
                            )
                        },
                        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
                    ) { paddingValues ->
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            navController = navController,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }
}

data class ThemeSettings(
    val dynamicColors: Boolean,
    val darkTheme: Int,
    val amoledBlack: Boolean,
    val monetSudokuBoard: Boolean
)

@HiltViewModel
class MainActivityViewModel
@Inject
constructor(
    themeSettingsManager: ThemeSettingsManager,
    appSettingsManager: AppSettingsManager,
    private val playGamesSettingsManager: PlayGamesSettingsManager,
    private val playGamesManager: PlayGamesManager
) : ViewModel() {
    val firstLaunch = appSettingsManager.firstLaunch

    // Combine all theme settings into a single flow
    // This ensures we only render once ALL settings are loaded
    val themeSettings: StateFlow<ThemeSettings?> =
        combine(
            themeSettingsManager.dynamicColors,
            themeSettingsManager.darkTheme,
            themeSettingsManager.amoledBlack,
            themeSettingsManager.monetSudokuBoard
        ) { dynamicColors, darkTheme, amoledBlack, monetSudokuBoard ->
            ThemeSettings(
                dynamicColors = dynamicColors,
                darkTheme = darkTheme,
                amoledBlack = amoledBlack,
                monetSudokuBoard = monetSudokuBoard
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    fun trySilentSignIn(activity: android.app.Activity) {
        viewModelScope.launch {
            val playGamesEnabled = playGamesSettingsManager.playGamesEnabled.first()
            if (playGamesEnabled && !playGamesManager.isSignedIn.value) {
                playGamesManager.silentSignIn(activity)
            }
        }
    }
}
