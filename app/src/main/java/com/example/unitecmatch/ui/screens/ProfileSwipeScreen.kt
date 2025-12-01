package com.example.unitecmatch.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unitecmatch.data.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileSwipeScreen(onNavigateToMatches: () -> Unit, onNavigateToChat: (Int) -> Unit) {
    val viewModel: ProfileViewModel = viewModel()
    val user = viewModel.getCurrentUser()
    val newMatch by viewModel.newMatch.collectAsState()

    // New Match Dialog
    newMatch?.let { matchedUser ->
        AlertDialog(
            onDismissRequest = { viewModel.clearNewMatch() },
            title = { Text("It's a Match!", style = MaterialTheme.typography.headlineLarge) },
            text = { Text("You and ${matchedUser.name} have liked each other.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.clearNewMatch()
                    onNavigateToChat(matchedUser.id)
                }) {
                    Text("Send a Message")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.clearNewMatch() }) {
                    Text("Keep Swiping")
                }
            }
        )
    }

    if (user != null) {
        val offsetX = remember { mutableStateOf(0f) }
        val coroutineScope = rememberCoroutineScope()
        val animatedOffsetX by animateFloatAsState(targetValue = offsetX.value, animationSpec = tween(durationMillis = 100))

        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f)
                    .clip(RoundedCornerShape(16.dp))
                    .graphicsLayer(
                        translationX = animatedOffsetX,
                        rotationZ = (animatedOffsetX / 60).coerceIn(-15f, 15f)
                    )
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                coroutineScope.launch {
                                    val threshold = 300f
                                    if (offsetX.value > threshold) viewModel.onLike()
                                    else if (offsetX.value < -threshold) viewModel.onDiscard()
                                    offsetX.value = 0f // Reset position
                                }
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                offsetX.value += dragAmount
                                change.consume()
                            }
                        )
                    },
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = user.photoResId),
                        contentDescription = "${user.name}'s profile picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                    startY = 600f
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "${user.name} ${user.lastName}, ${user.age}",
                            style = MaterialTheme.typography.headlineLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                        )
                        Text(text = user.description, style = MaterialTheme.typography.bodyLarge.copy(color = Color.White.copy(alpha = 0.8f)))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Interests: ${user.interests.joinToString()}", style = MaterialTheme.typography.labelLarge.copy(color = Color.White))
                    }

                    // Like/Nope indicator
                    val alpha = (kotlin.math.abs(animatedOffsetX) / 300f).coerceIn(0f, 1f)
                    if (animatedOffsetX > 0) {
                        Text("LIKE", color = Color(0xFF4CAF50), fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.align(Alignment.Center).alpha(alpha).border(6.dp, Color(0xFF4CAF50), RoundedCornerShape(12.dp)).padding(horizontal = 16.dp, vertical = 8.dp))
                    } else if (animatedOffsetX < 0) {
                        Text("NOPE", color = Color.Red, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.align(Alignment.Center).alpha(alpha).border(6.dp, Color.Red, RoundedCornerShape(12.dp)).padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                }
            }
        }
    } else {
        LaunchedEffect(Unit) {
            onNavigateToMatches()
        }
    }
}
