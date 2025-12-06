package com.greenlime.notification_reminder_flutter.ui.composable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.isActive

@Composable
fun AnimatedButton(
    label: String,
    background: Color,
    shouldAnimate: Boolean = false,
    targetScale: Animatable<Float, *>,
    onClick: () -> Unit
) {

    // Run animation only when shouldAnimate changes
    LaunchedEffect(shouldAnimate) {
        if (shouldAnimate) {
            // Loop until effect is cancelled
            while (isActive) {
                targetScale.animateTo(
                    1.12f,
                    animationSpec = tween(450)
                )
                targetScale.animateTo(
                    1f,
                    animationSpec = tween(450)
                )
            }
        } else {
            // Stop animation instantly when disabled (Accept pressed)
            targetScale.snapTo(1f)
        }
    }

    Button(
        onClick = onClick,
        modifier = Modifier
            .size(110.dp)
            .graphicsLayer {
                scaleX = targetScale.value
                scaleY = targetScale.value
            },
        colors = ButtonDefaults.buttonColors(background),
        shape = CircleShape
    ) {
        Text(
            label,
            color = Color.White,
            fontSize = 17.sp
        )
    }
}