package sk.awisoft.sudokuplus.ui.game.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
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
    onClick: () -> Unit = { },
    onLongClick: () -> Unit = { }
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(if (toggled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = if (toggled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
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
