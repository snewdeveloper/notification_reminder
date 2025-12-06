package com.greenlime.notification_reminder_flutter.ui.composable// Imports
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreenSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    thumbSize: Dp = 22.dp
) {
    val activeGreen = Color(0xFF2ECC71)
    val inactiveTrack = Color(0xFF1F1F1F)

    // 🔥 Pulsating effect for the thumb
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        colors = SliderDefaults.colors(
            activeTrackColor = activeGreen,
            inactiveTrackColor = inactiveTrack,
            thumbColor = activeGreen
        ),
        thumb = {
            // Round glowing green thumb
            Box(
                modifier = Modifier
                    .size(thumbSize * pulse)   // 🔥 Pulsating
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor = activeGreen.copy(alpha = 0.4f),
                        spotColor = activeGreen.copy(alpha = 0.4f)
                    )
                    .background(activeGreen, CircleShape)
            )
        },
        track = { sliderPositions ->
            SliderDefaults.Track(
                sliderState = sliderPositions,
//                sliderPositions = sliderPositions,     // FIXED — you MUST pass this
                modifier = Modifier
                    .height(8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Transparent),
                colors = SliderDefaults.colors(
                    activeTrackColor = activeGreen,
                    inactiveTrackColor = inactiveTrack
                )
            )
        }
    )
}


fun Modifier.pulsatingEffect(
    currentValue: Float,
    isVisible: Boolean,
    color: Color = Color.Gray,
): Modifier = composed {
    var trackWidth by remember { mutableFloatStateOf(0f) }
    val thumbX by remember(currentValue) {
        mutableFloatStateOf(trackWidth * currentValue)
    }

    val transition = rememberInfiniteTransition(label = "trackAnimation")

    val animationProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 800,
                delayMillis = 200,
            )
        ), label = "width"
    )

    this then Modifier
        .onGloballyPositioned { coordinates ->
            trackWidth = coordinates.size.width.toFloat()
        }
        .drawWithContent {
            drawContent()

            val strokeWidth = size.height
            val y = size.height / 2f
            val startOffset = thumbX
            val endOffset = thumbX + animationProgress * (trackWidth - thumbX)
            val dynamicAlpha = (1f - animationProgress).coerceIn(0f, 1f)

            if (isVisible) {
                drawLine(
                    color = color.copy(alpha = dynamicAlpha),
                    start = Offset(startOffset, y),
                    end = Offset(endOffset, y),
                    cap = StrokeCap.Round,
                    strokeWidth = strokeWidth
                )
            }
        }
}