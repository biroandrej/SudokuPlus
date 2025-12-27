package sk.awisoft.sudokuplus.ui.settings.playgames

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sk.awisoft.sudokuplus.data.datastore.PlayGamesSettingsManager
import sk.awisoft.sudokuplus.playgames.PlayGamesManager

@HiltViewModel
class PlayGamesViewModel
@Inject
constructor(
    private val playGamesSettingsManager: PlayGamesSettingsManager,
    private val playGamesManager: PlayGamesManager
) : ViewModel() {

    val playGamesEnabled = playGamesSettingsManager.playGamesEnabled

    val isSignedIn = playGamesManager.isSignedIn.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    val playerInfo = playGamesManager.playerInfo.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    fun setPlayGamesEnabled(enabled: Boolean) {
        viewModelScope.launch {
            playGamesSettingsManager.setPlayGamesEnabled(enabled)
            if (!enabled) {
                playGamesManager.signOut()
            }
        }
    }

    fun signIn(activity: Activity, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = playGamesManager.interactiveSignIn(activity)
            onResult(success)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            playGamesManager.signOut()
        }
    }

    fun silentSignIn(activity: Activity) {
        viewModelScope.launch {
            playGamesManager.silentSignIn(activity)
        }
    }

    fun showAchievements(activity: Activity) {
        playGamesManager.showAchievementsUI(activity)
    }

    fun showLeaderboards(activity: Activity) {
        playGamesManager.showAllLeaderboardsUI(activity)
    }

    fun syncProgress() {
        viewModelScope.launch {
            playGamesManager.syncData()
        }
    }
}
