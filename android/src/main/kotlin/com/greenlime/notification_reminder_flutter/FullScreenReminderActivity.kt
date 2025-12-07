package com.greenlime.notification_reminder_flutter


import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.greenlime.notification_reminder_flutter.receiver.RingingPlayer
import com.greenlime.notification_reminder_flutter.utils.formatTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

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

@Composable
fun ReminderScreen(
    title: String,
    description: String,
    progress: Float,
    isAccepted: MutableState<Boolean>,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    player: ExoPlayer
) {
    // Animation scale objects
    val acceptScale = remember { Animatable(1f) }
    val rejectScale = remember { Animatable(1f) }

    // Track play/pause
    val isPlaying = remember(isAccepted) { mutableStateOf(false) }

    // Background overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000).copy(alpha = 0.6f))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            // Title
            Text(
                text = title,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 30.sp
            )

            // Description
            Text(
                text = description,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )

            // Player Controls
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // PLAY / PAUSE visible only after Accept
                if (isAccepted.value) {
                    Button(
                        modifier = Modifier.height(50.dp),
                        onClick = {
                            if (!isPlaying.value) {
                                player.play()
                            } else {
                                player.pause()
                            }
                            isPlaying.value = !isPlaying.value
                        },
                        colors = ButtonDefaults.buttonColors(Color.DarkGray)
                    ) {
                        Text(
                            if (!(isPlaying.value && isAccepted.value)) "Play" else "Pause",
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Green Slider
                GreenSlider(
                    value = progress,
                    onValueChange = {
                        if(!isAccepted.value) return@GreenSlider
                        if (player.duration > 0) {
                            player.seekTo((it * player.duration).toLong())
                        }
                    }
                )

                // Time labels only when accepted
                if (isAccepted.value) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(player.currentPosition),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = formatTime(player.duration),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                // ❌ Reject Button
                AnimatedButton(
                    label = "Close",
                    shouldAnimate = false,
                    background = Color.Red,
                    targetScale = rejectScale,
                    onClick = onReject
                )

                // ✔ Accept Button — animate until accepted
                if (!isAccepted.value) {
                    AnimatedButton(
                        label = "Accept",
                        shouldAnimate = true,
                        background = Color(0xFF2ECC71),
                        targetScale = acceptScale,
                        onClick = {
                            isAccepted.value = true
                            isPlaying.value = true
                            onAccept()
                        }
                    )
                }
            }
        }
    }
}


//@AndroidEntryPoint
class FullScreenReminderActivity : ComponentActivity() {


    private lateinit var ringingPlayer: RingingPlayer
    private lateinit var player: ExoPlayer

    // 🔥 State variables observed by Compose
    private val currentTitle = mutableStateOf("")
    private val currentDescription = mutableStateOf("")
    private val currentReminderId = mutableStateOf("")
    private val progress = mutableStateOf(0f)
    private val isAccepted = mutableStateOf(false)

    // ===============  NEW INTENT HANDLER  =======================
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val newTitle = intent.getStringExtra("title") ?: ""
        val newDescription = intent.getStringExtra("description") ?: ""
        val newAudioUrl = intent.getStringExtra("audio_url") ?: ""
        val newReminderId = intent.getStringExtra("reminderId") ?: ""

        updateUiWithNewPayload(
            title = newTitle,
            description = newDescription,
            audioUrl = newAudioUrl,
            reminderId = newReminderId
        )
    }

    // ===============  PAYLOAD UPDATE  =======================
    @RequiresApi(Build.VERSION_CODES.P)
    fun updateUiWithNewPayload(
        title: String,
        description: String,
        audioUrl: String,
        reminderId: String
    ) {
        // Stop current audio
        player.stop()
        player.clearMediaItems()

        // Load new audio
        player.setMediaItem(MediaItem.fromUri(audioUrl))
        player.prepare()
        player.seekTo(0)

        // Restart ringing
        ringingPlayer.stopRinging()
        ringingPlayer.startRinging()
        player.pause()


        // Update UI states (Triggers recomposition)
        currentTitle.value = title
        currentDescription.value = description
        currentReminderId.value = reminderId

        isAccepted.value = false
        progress.value = 0f
    }

    // =================== ON CREATE ==========================
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ringingPlayer = RingingPlayer(this)
        ringingPlayer.startRinging()

        // Initial payload
        currentTitle.value = intent.getStringExtra("title") ?: ""
        currentDescription.value = intent.getStringExtra("description") ?: ""
        currentReminderId.value = intent.getStringExtra("reminderId") ?: ""
        val audioUrl = intent.getStringExtra("audio_url") ?: ""

        // Setup ExoPlayer
        player = ExoPlayer.Builder(this).build().apply {
            setMediaItem(MediaItem.fromUri(audioUrl))
            prepare()
            playWhenReady = false   // play only after Accept
        }

        // Track progress every 200ms
        lifecycleScope.launch {
            while (true) {
                if (player.duration > 0) {
                    progress.value = player.currentPosition / player.duration.toFloat()
                }
                delay(200)
            }
        }
        // Compose UI
        setContent {
            ReminderScreen(
                title = currentTitle.value,
                description = currentDescription.value,
                isAccepted = isAccepted,
                progress = progress.value,

                onAccept = {
                    isAccepted.value = true
                    player.play()
                    ringingPlayer.stopRinging()
                },

                onReject = {
                    player.pause()
                    ringingPlayer.stopRinging()
                    finish()
                },

                player = player
            )
        }
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }
}