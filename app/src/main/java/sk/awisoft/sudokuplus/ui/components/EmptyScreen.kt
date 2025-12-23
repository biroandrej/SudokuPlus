package sk.awisoft.sudokuplus.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import sk.awisoft.sudokuplus.ui.theme.SudokuPlusTheme
import sk.awisoft.sudokuplus.ui.util.LightDarkPreview

@Composable
fun EmptyScreen(
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = { }
) {
    // Animation states
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec =
        spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "empty screen scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "empty screen alpha"
    )

    Column(
        modifier =
        modifier
            .fillMaxSize()
            .scale(scale)
            .alpha(alpha),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animated Sudoku grid illustration
        SudokuGridIllustration(
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        content()
    }
}

@Composable
private fun SudokuGridIllustration(modifier: Modifier = Modifier) {
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    val thickLineColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
    val accentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)

    Canvas(modifier = modifier) {
        val gridSize = size.minDimension
        val cellSize = gridSize / 9f
        val cornerRadius = 12f

        // Draw background with rounded corners
        drawRoundRect(
            color = accentColor,
            topLeft = Offset.Zero,
            size = Size(gridSize, gridSize),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )

        // Draw some "filled" cells to make it look like a puzzle
        val filledCells =
            listOf(
                Pair(0, 2), Pair(1, 5), Pair(2, 8),
                Pair(3, 1), Pair(4, 4), Pair(5, 7),
                Pair(6, 0), Pair(7, 3), Pair(8, 6)
            )

        filledCells.forEach { (row, col) ->
            drawRoundRect(
                color = thickLineColor.copy(alpha = 0.2f),
                topLeft = Offset(col * cellSize + 2, row * cellSize + 2),
                size = Size(cellSize - 4, cellSize - 4),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }

        // Draw thin grid lines
        for (i in 1 until 9) {
            if (i % 3 != 0) {
                // Vertical
                drawLine(
                    color = gridColor,
                    start = Offset(i * cellSize, cornerRadius),
                    end = Offset(i * cellSize, gridSize - cornerRadius),
                    strokeWidth = 1f
                )
                // Horizontal
                drawLine(
                    color = gridColor,
                    start = Offset(cornerRadius, i * cellSize),
                    end = Offset(gridSize - cornerRadius, i * cellSize),
                    strokeWidth = 1f
                )
            }
        }

        // Draw thick 3x3 box lines
        for (i in 1 until 3) {
            // Vertical
            drawLine(
                color = thickLineColor,
                start = Offset(i * 3 * cellSize, 4f),
                end = Offset(i * 3 * cellSize, gridSize - 4f),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round
            )
            // Horizontal
            drawLine(
                color = thickLineColor,
                start = Offset(4f, i * 3 * cellSize),
                end = Offset(gridSize - 4f, i * 3 * cellSize),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round
            )
        }

        // Draw border
        drawRoundRect(
            color = thickLineColor,
            topLeft = Offset.Zero,
            size = Size(gridSize, gridSize),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius),
            style = Stroke(width = 3f)
        )
    }
}

@LightDarkPreview
@Composable
private fun EmptyScreenPreview() {
    SudokuPlusTheme {
        Surface {
            EmptyScreen("There is so empty...")
        }
    }
}
