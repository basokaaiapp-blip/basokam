package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AlmostBlackBg
import com.example.ui.theme.NeonElectricBlue
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.SurfaceElevated

@Composable
fun BasokaLogo(
    size: Dp = 48.dp,
    showBorder: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.28f))
            .background(
                Brush.linearGradient(
                    colors = listOf(SurfaceElevated, AlmostBlackBg)
                )
            )
            .then(
                if (showBorder) {
                    Modifier.border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            listOf(NeonElectricBlue.copy(alpha = 0.8f), NeonYellow.copy(alpha = 0.4f))
                        ),
                        shape = RoundedCornerShape(size * 0.28f)
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.65f)) {
            val w = this.size.width
            val h = this.size.height
            val strokeW = w * 0.12f

            // Futuristic 'B' letter in Neon Electric Blue
            val pathB = Path().apply {
                moveTo(w * 0.15f, h * 0.10f)
                lineTo(w * 0.15f, h * 0.90f) // Stem

                // Top loop of B
                moveTo(w * 0.15f, h * 0.10f)
                cubicTo(w * 0.55f, h * 0.10f, w * 0.60f, h * 0.48f, w * 0.15f, h * 0.48f)

                // Bottom loop of B
                moveTo(w * 0.15f, h * 0.48f)
                cubicTo(w * 0.65f, h * 0.48f, w * 0.70f, h * 0.90f, w * 0.15f, h * 0.90f)
            }

            drawPath(
                path = pathB,
                color = NeonElectricBlue,
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )

            // Futuristic 'A' letter in Neon Yellow (overlapping dynamically)
            val pathA = Path().apply {
                moveTo(w * 0.72f, h * 0.90f)
                lineTo(w * 0.84f, h * 0.18f)
                lineTo(w * 0.96f, h * 0.90f)

                moveTo(w * 0.76f, h * 0.62f)
                lineTo(w * 0.92f, h * 0.62f)
            }

            drawPath(
                path = pathA,
                color = NeonYellow,
                style = Stroke(width = strokeW * 0.9f, cap = StrokeCap.Round)
            )

            // Futuristic Tech Accent Dot
            drawCircle(
                color = NeonYellow,
                radius = strokeW * 0.45f,
                center = Offset(w * 0.84f, h * 0.08f)
            )
        }
    }
}
