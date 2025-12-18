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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import sk.awisoft.sudokuplus.destinations.HomeScreenDestination
import sk.awisoft.sudokuplus.destinations.ImportFromFileScreenDestination
import sk.awisoft.sudokuplus.destinations.MoreScreenDestination
import sk.awisoft.sudokuplus.destinations.StatisticsScreenDestination
import sk.awisoft.sudokuplus.destinations.WelcomeScreenDestination
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import sk.awisoft.sudokuplus.core.PreferencesConstants
import sk.awisoft.sudokuplus.core.utils.GlobalExceptionHandler
import sk.awisoft.sudokuplus.data.datastore.AppSettingsManager
import sk.awisoft.sudokuplus.data.datastore.ThemeSettingsManager
import sk.awisoft.sudokuplus.ui.app_crash.CrashActivity
import sk.awisoft.sudokuplus.ui.components.navigation_bar.NavigationBarComponent
import sk.awisoft.sudokuplus.ui.theme.BoardColors
import sk.awisoft.sudokuplus.ui.theme.SudokuPlusTheme
import sk.awisoft.sudokuplus.ui.theme.SudokuBoardColorsImpl
import sk.awisoft.sudokuplus.ui.util.findActivity
import javax.inject.Inject

val LocalBoardColors = staticCompositionLocalOf { SudokuBoardColorsImpl() }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var settings: AppSettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (!BuildConfig.DEBUG) {
            GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)
        }

        setContent {
            val mainViewModel: MainActivityViewModel = hiltViewModel()

            val dynamicColors by mainViewModel.dc.collectAsStateWithLifecycle(
                PreferencesConstants.DEFAULT_DYNAMIC_COLORS
            )
            val darkTheme by mainViewModel.darkTheme.collectAsStateWithLifecycle(
                PreferencesConstants.DEFAULT_DARK_THEME)
            val amoledBlack by mainViewModel.amoledBlack.collectAsStateWithLifecycle(
                PreferencesConstants.DEFAULT_AMOLED_BLACK)
            val firstLaunch by mainViewModel.firstLaunch.collectAsStateWithLifecycle(false)

            SudokuPlusTheme(
                darkTheme = when (darkTheme) {
                    1 -> false
                    2 -> true
                    else -> isSystemInDarkTheme()
                },
                dynamicColor = dynamicColors,
                amoled = amoledBlack,
                colorSeed = Color(PreferencesConstants.DEFAULT_THEME_SEED_COLOR),
            ) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()

                var bottomBarState by rememberSaveable { mutableStateOf(false) }

                LaunchedEffect(navBackStackEntry) {
                    bottomBarState = when (navBackStackEntry?.destination?.route) {
                        StatisticsScreenDestination.route, HomeScreenDestination.route, MoreScreenDestination.route -> true
                        else -> false
                    }
                }
                LaunchedEffect(firstLaunch) {
                    if (firstLaunch) {
                        navController.navigate(
                            route = WelcomeScreenDestination.route,
                            navOptions = navOptions {
                                popUpTo(HomeScreenDestination.route) {
                                    inclusive = true
                                }
                            }
                        )
                    }
                }

                val monetSudokuBoard by mainViewModel.monetSudokuBoard.collectAsStateWithLifecycle(
                    initialValue = PreferencesConstants.DEFAULT_MONET_SUDOKU_BOARD
                )
                val boardColors =
                    if (monetSudokuBoard) {
                        SudokuBoardColorsImpl(
                            foregroundColor = BoardColors.foregroundColor,
                            notesColor = BoardColors.notesColor,
                            altForegroundColor = BoardColors.altForegroundColor,
                            errorColor = BoardColors.errorColor,
                            highlightColor = BoardColors.highlightColor,
                            thickLineColor = BoardColors.thickLineColor,
                            thinLineColor = BoardColors.thinLineColor
                        )
                    } else {
                        SudokuBoardColorsImpl(
                            foregroundColor = MaterialTheme.colorScheme.onSurface,
                            notesColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                            altForegroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            errorColor = BoardColors.errorColor,
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
                            startRoute = NavGraphs.root.startRoute,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }
}

@HiltViewModel
class MainActivityViewModel
@Inject constructor(
    themeSettingsManager: ThemeSettingsManager,
    appSettingsManager: AppSettingsManager
) : ViewModel() {
    val dc = themeSettingsManager.dynamicColors
    val darkTheme = themeSettingsManager.darkTheme
    val amoledBlack = themeSettingsManager.amoledBlack
    val firstLaunch = appSettingsManager.firstLaunch
    val monetSudokuBoard = themeSettingsManager.monetSudokuBoard
}

@Destination(
    deepLinks = [
        DeepLink(
            uriPattern = "content://",
            mimeType = "*/*",
            action = Intent.ACTION_VIEW
        )
    ]
)
@Composable
fun HandleImportFromFileDeepLink(
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val activity = context.findActivity()
        if (activity != null) {
            val intentData = activity.intent.data
            if (intentData != null) {
                navigator.navigate(
                    ImportFromFileScreenDestination(
                        fileUri = intentData.toString(),
                        fromDeepLink = true
                    )
                )
            }
        }
    }
}
