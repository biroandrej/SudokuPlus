package sk.awisoft.sudokuplus.playgames

import android.app.Activity
import android.content.Context
import com.google.android.gms.games.AchievementsClient
import com.google.android.gms.games.GamesSignInClient
import com.google.android.gms.games.LeaderboardsClient
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.PlayGamesSdk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

object PlayGamesManagerImpl : PlayGamesManager {
    private val _isSignedIn = MutableStateFlow(false)
    override val isSignedIn: StateFlow<Boolean> = _isSignedIn.asStateFlow()

    private val _playerInfo = MutableStateFlow<PlayerInfo?>(null)
    override val playerInfo: StateFlow<PlayerInfo?> = _playerInfo.asStateFlow()

    private var gamesSignInClient: GamesSignInClient? = null
    private var achievementsClient: AchievementsClient? = null
    private var leaderboardsClient: LeaderboardsClient? = null

    fun initialize(context: Context) {
        PlayGamesSdk.initialize(context)
    }

    override suspend fun silentSignIn(context: Context): Boolean {
        return try {
            gamesSignInClient = PlayGames.getGamesSignInClient(context as Activity)
            val isAuthenticated = gamesSignInClient?.isAuthenticated?.await()
            if (isAuthenticated?.isAuthenticated == true) {
                _isSignedIn.value = true
                achievementsClient = PlayGames.getAchievementsClient(context)
                leaderboardsClient = PlayGames.getLeaderboardsClient(context)
                loadPlayerInfo(context)
                true
            } else {
                _isSignedIn.value = false
                false
            }
        } catch (e: Exception) {
            _isSignedIn.value = false
            false
        }
    }

    override suspend fun interactiveSignIn(activity: Activity): Boolean {
        return try {
            gamesSignInClient = PlayGames.getGamesSignInClient(activity)
            gamesSignInClient?.signIn()?.await()

            val isAuthenticated = gamesSignInClient?.isAuthenticated?.await()
            if (isAuthenticated?.isAuthenticated == true) {
                _isSignedIn.value = true
                achievementsClient = PlayGames.getAchievementsClient(activity)
                leaderboardsClient = PlayGames.getLeaderboardsClient(activity)
                loadPlayerInfo(activity)
                true
            } else {
                _isSignedIn.value = false
                false
            }
        } catch (e: Exception) {
            _isSignedIn.value = false
            false
        }
    }

    private suspend fun loadPlayerInfo(activity: Activity) {
        try {
            val player = PlayGames.getPlayersClient(activity).currentPlayer.await()
            _playerInfo.value = PlayerInfo(
                displayName = player.displayName,
                playerId = player.playerId,
                avatarUri = player.hiResImageUri?.toString()
            )
        } catch (e: Exception) {
            _playerInfo.value = null
        }
    }

    override suspend fun signOut() {
        _isSignedIn.value = false
        _playerInfo.value = null
        gamesSignInClient = null
        achievementsClient = null
        leaderboardsClient = null
    }

    override suspend fun unlockAchievement(achievementId: String) {
        if (!_isSignedIn.value) return
        try {
            achievementsClient?.unlock(achievementId)
        } catch (e: Exception) {
            // Silently fail - achievement will sync when connection is restored
        }
    }

    override suspend fun incrementAchievement(achievementId: String, steps: Int) {
        if (!_isSignedIn.value) return
        try {
            achievementsClient?.increment(achievementId, steps)
        } catch (e: Exception) {
            // Silently fail
        }
    }

    override suspend fun revealAchievement(achievementId: String) {
        if (!_isSignedIn.value) return
        try {
            achievementsClient?.reveal(achievementId)
        } catch (e: Exception) {
            // Silently fail
        }
    }

    override fun showAchievementsUI(activity: Activity) {
        if (!_isSignedIn.value) return
        achievementsClient?.achievementsIntent?.addOnSuccessListener { intent ->
            activity.startActivityForResult(intent, RC_ACHIEVEMENT_UI)
        }
    }

    override suspend fun submitScore(leaderboardId: String, score: Long) {
        if (!_isSignedIn.value) return
        try {
            leaderboardsClient?.submitScore(leaderboardId, score)
        } catch (e: Exception) {
            // Silently fail
        }
    }

    override fun showLeaderboardUI(activity: Activity, leaderboardId: String) {
        if (!_isSignedIn.value) return
        leaderboardsClient?.getLeaderboardIntent(leaderboardId)?.addOnSuccessListener { intent ->
            activity.startActivityForResult(intent, RC_LEADERBOARD_UI)
        }
    }

    override fun showAllLeaderboardsUI(activity: Activity) {
        if (!_isSignedIn.value) return
        leaderboardsClient?.allLeaderboardsIntent?.addOnSuccessListener { intent ->
            activity.startActivityForResult(intent, RC_LEADERBOARD_UI)
        }
    }

    override suspend fun syncData() {
        // Cloud save implementation will be added later
        // For now, this is a placeholder for future implementation
    }

    private const val RC_ACHIEVEMENT_UI = 9003
    private const val RC_LEADERBOARD_UI = 9004
}
