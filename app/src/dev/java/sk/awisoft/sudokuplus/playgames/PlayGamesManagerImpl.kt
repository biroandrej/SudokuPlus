package sk.awisoft.sudokuplus.playgames

import android.app.Activity
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PlayGamesManagerImpl : PlayGamesManager {
    private val _isSignedIn = MutableStateFlow(false)
    override val isSignedIn: StateFlow<Boolean> = _isSignedIn.asStateFlow()

    private val _playerInfo = MutableStateFlow<PlayerInfo?>(null)
    override val playerInfo: StateFlow<PlayerInfo?> = _playerInfo.asStateFlow()

    fun initialize(context: Context) = Unit

    override suspend fun silentSignIn(context: Context): Boolean = false

    override suspend fun interactiveSignIn(activity: Activity): Boolean = false

    override suspend fun signOut() = Unit

    override suspend fun unlockAchievement(achievementId: String) = Unit

    override suspend fun incrementAchievement(achievementId: String, steps: Int) = Unit

    override suspend fun revealAchievement(achievementId: String) = Unit

    override fun showAchievementsUI(activity: Activity) = Unit

    override suspend fun submitScore(leaderboardId: String, score: Long) = Unit

    override fun showLeaderboardUI(activity: Activity, leaderboardId: String) = Unit

    override fun showAllLeaderboardsUI(activity: Activity) = Unit

    override suspend fun syncData() = Unit
}
