package sk.awisoft.sudokuplus.playgames

import android.app.Activity
import android.content.Context
import kotlinx.coroutines.flow.StateFlow

data class PlayerInfo(
    val displayName: String,
    val playerId: String,
    val avatarUri: String?
)

interface PlayGamesManager {
    val isSignedIn: StateFlow<Boolean>
    val playerInfo: StateFlow<PlayerInfo?>

    suspend fun silentSignIn(context: Context): Boolean
    suspend fun interactiveSignIn(activity: Activity): Boolean
    suspend fun signOut()

    suspend fun unlockAchievement(achievementId: String)
    suspend fun incrementAchievement(achievementId: String, steps: Int)
    suspend fun revealAchievement(achievementId: String)
    fun showAchievementsUI(activity: Activity)

    suspend fun submitScore(leaderboardId: String, score: Long)
    fun showLeaderboardUI(activity: Activity, leaderboardId: String)
    fun showAllLeaderboardsUI(activity: Activity)

    suspend fun syncData()
}
