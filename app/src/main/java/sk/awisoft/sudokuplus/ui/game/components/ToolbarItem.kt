package sk.awisoft.sudokuplus.ui.game.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.ui.theme.SudokuPlusTheme
import sk.awisoft.sudokuplus.ui.util.LightDarkPreview

enum class ToolBarItem {
    Undo,
    Hint,
    Note,
    Remove,
    Redo
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ToolbarItem(
    modifier: Modifier = Modifier,
    painter: Painter,
    toggled: Boolean = false,
    enabled: Boolean = true,
    visualEnabled: Boolean = enabled,
    badgeText: String? = null,
    onClick: () -> Unit = { },
    onLongClick: () -> Unit = { }
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .alpha(if (visualEnabled) 1f else 0.55f)
            .height(56.dp)
            .background(if (toggled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow)
            .combinedClickable(
                onClick = if (enabled) onClick else ({ }),
                onLongClick = if (enabled) onLongClick else ({ })
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painter,
                contentDescription = null,
                tint = if (toggled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }

        if (badgeText != null) {
            Badge(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = 14.dp, y = (-14).dp),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                androidx.compose.material3.Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun KeyboardItemPreview() {
    SudokuPlusTheme {
        Surface {
            Row {
                ToolbarItem(
                    modifier = Modifier.weight(1f),
                    painter = painterResource(R.drawable.ic_round_edit_24)
                )
                ToolbarItem(
                    modifier = Modifier.weight(1f),
                    painter = painterResource(R.drawable.ic_round_edit_24),
                    toggled = true
                )
            }
        }
    }
}
